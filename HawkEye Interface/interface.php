<?php
	
	///////////////////////////////////////////////////
	//         HawkEye Interface Search File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	
	error_reporting(E_ALL);
	session_start();
	
	//Include config, lang pack and MySQL connector
	include("config.php");
	include("langs/" . $hawkConfig["langFile"]);	
	
	//Set up output array
	$output = array(
		"error" => "",
		"columns" => $lang["results"],
		"data" => array()
	);
	
	//Check if required functions are here
	if (!function_exists("json_decode")) require('json.php');
	
	//If not logged in, throw an error
	if (!isset($_SESSION["loggedIn"]) && $hawkConfig["password"] != "")
		return error($lang["messages"]["notLoggedIn"]);
	
	if (!isset($_GET["data"]))
		return error($lang["messages"]["breakMe"]);
		
	$data = json_decode(stripslashes($_GET["data"]), true);
		
	//Get players
	$players = array();
	$res = mysql_query("SELECT * FROM `" . $hawkConfig["dbPlayerTable"] . "`");
	if (!$res)
		return error(mysql_error());
	if (mysql_num_rows($res) == 0)
		return error($lang["messages"]["noResults"]);
	while ($player = mysql_fetch_object($res))
		$players[$player->player_id] = $player->player;
	
	//Get worlds
	$worlds = array();
	$res = mysql_query("SELECT * FROM `" . $hawkConfig["dbWorldTable"] . "`");
	if (!$res)
		return error(mysql_error());
	if (mysql_num_rows($res) == 0)
		return error($lang["messages"]["noResults"]);
	while ($world = mysql_fetch_object($res))
		$worlds[$world->world_id] = $world->world;
	
	$sql = "SELECT * FROM `" . $hawkConfig["dbTable"] . "` WHERE ";
	$args = array();
	
	if ($data["players"][0] != "") {
		$pids = array();
		foreach ($data["players"] as $key => $val)
			foreach ($players as $key2 => $val2)
				if (stristr($val2, $val))
					array_push($pids, $key2);
		if (count($pids) > 0)
			array_push($args, "player_id IN (" . join(",", $pids) . ")");
		else
			return error($lang["messages"]["noResults"]);
	}
	if ($data["worlds"][0] != "") {
		$wids = array();
		foreach ($data["worlds"] as $key => $val)
			foreach ($worlds as $key2 => $val2)
				if (stristr($val2, $val))
					array_push($wids, $key2);
		if (count($wids) > 0)
			array_push($args, "world_id IN (" . join(",", $wids) . ")");
		else
			return error($lang["messages"]["noResults"]);
	}
	if (count($data["actions"]) == 0)
		return error($lang["messages"]["noActions"]);
	else
		array_push($args, "`action` IN (" . join(",", $data["actions"]) . ")");
	
	$range = $hawkConfig["radius"];
	if ($data["range"] != "")
		$range = $data["range"];
	if ($data["loc"][0] != "")
		array_push($args, "(`x` BETWEEN " . ($data["loc"][0] - $range) . " AND " . ($data["loc"][0] + $range) . ")");
	if ($data["loc"][1] != "")
		array_push($args, "(`y` BETWEEN " . ($data["loc"][1] - $range) . " AND " . ($data["loc"][1] + $range) . ")");
	if ($data["loc"][2] != "")
		array_push($args, "(`z` BETWEEN " . ($data["loc"][2] - $range) . " AND " . ($data["loc"][2] + $range) . ")");
	if ($data["block"] != "00") {
		if ($data["keywords"][0] == "")
			$data["keywords"][0] = $data["block"];
		else
			array_push($data["keywords"], $data["block"]);
	}
	
	if ($data["dateFrom"] != "" && $data["dateFrom"] != " ")
		array_push($args, "`date` >= '" . $data["dateFrom"] . "'");
	if ($data["dateTo"] != "" && $data["dateTo"] != " ")
		array_push($args, "`date` <= '" . $data["dateTo"] . "'");
	if ($data["keywords"][0] != "") {
		foreach ($data["keywords"] as $key => $val)
			$data["keywords"][$key] = "'%" . $val . "%'";
		array_push($args, "`data` LIKE " . join(" OR `data` LIKE ", $data["keywords"]));
	}
	if ($data["exclude"][0] != "") {
		foreach ($data["exclude"] as $key => $val)
			$data["exclude"][$key] = "'%" . $val . "%'";
		array_push($args, "`data` NOT LIKE " . join(" OR `data` LIKE ", $data["exclude"]));
	}
	
	//Compile SQL statement
	$sql .= join(" AND ", $args);
	if ($hawkConfig["maxResults"] > 0)
		$sql .= " LIMIT " . $hawkConfig["maxResults"];
		
	//Log query
	set_error_handler('handleError');
	if ($hawkConfig["logQueries"] == true) {
		try {
			if (!file_put_contents("log.txt", date("m.d.y G:i:s") . " - " . $_SERVER["REMOTE_ADDR"] . " - " . $sql . "\n", FILE_APPEND))
				return error("Unable to open/write to log.txt!");
		} catch (ErrorException $e) {
			if (stristr($e, "Warning:"))
				return error("Unable to open/write to log.txt!");
		}
	}
	restore_error_handler();
		
	//Run query
	$res = mysql_query($sql);
	if (!$res)
		return error(mysql_error());
	
	$items = explode("\n", file_get_contents("items.txt"));
	$results = array();
	
	//Get results from MySQL
	while ($entry = mysql_fetch_object($res))
		array_push($results, $entry);
		
	foreach ($results as $key => $entry) {
		$row = array();
		$fdata = $entry->data;
		$action = $entry->action;
		
		//Manipulate data according to action
		switch ($action) {
			case 0:
			case 10:
				$fdata = getBlockName($fdata);
				break;
			case 1:
			case 19:
			case 25:
				$arr = explode("-", $fdata);
				if (count($arr) > 1)
					$fdata = getBlockName($arr[0]) . " replaced by " . getBlockName($arr[1]);
				else $fdata = getBlockName($arr[0]);
				break;
			case 16:
				$arr = explode("-", $fdata);
				if (count($arr) > 0)
					$action = array_shift($arr);
				$action .= $entry->plugin . " - ";
				$fdata = join("-", $arr);
				break;
			case 28:
				$changeString = "";
				$i = 0;
				foreach (explode("@", $fdata) as $op) {
					$changes = array();
					foreach (explode("&", $op) as $change) {
						if ($change == "") break;
						$item = explode(",", $change);
						$changes[] = $item[1] . "x " . getBlockName($item[0]);
					}
					if ($i == 0 && count($changes) > 0) $changeString .= '<span style="color: green">+(' . trim(implode(", ", $changes)) . ')</span>';
					if ($i == 1 && count($changes) > 0) $changeString .= '<span style="color: red">-(' . trim(implode(", ", $changes)) . ')</span>';
					$i++;
				}
				$fdata = $changeString;
				break;
			case 2:
			case 29:
				if (strpos("@", $fdata)) {
					$fdata = str_replace("|", "<br />", $fdata);
					break;
				}
				$arr = explode("@", $fdata);
				if (count($arr) < 3) break;
				$lines = explode(",", $arr[2]);
				foreach ($lines as $key => $value)
					$lines[$key] = base64_decode($value);
				$fdata = implode("<br />", $lines);
				break;
		}
		
		$action = str_replace(array_reverse(array_keys($lang["actions"])), array_reverse($lang["actions"]), $action);
	
		//Add to output row
		array_push($row, $entry->data_id, $entry->date, $players[$entry->player_id], $action, $worlds[$entry->world_id], round($entry->x, 1).",".round($entry->y, 1).",".round($entry->z, 1), $fdata);
		array_push($output["data"], $row);
	}
	
	echo json_encode($output);
	
	/*
	// FUNCTION: getBlockName($string);
	// Gets block name of block
	*/
	function getBlockName($string) {
		global $items;
		$parts = explode(":", $string);
		foreach ($items as $i) {
			$item = explode(",", $i);
			if ($item[0] == $parts[0]) {
				if (count($parts) == 2)
					return $item[1] . ":" . $parts[1];
				else return $item[1];
			}
		}
		return $string;
	}
	
	/*
	// FUNCTION: error($message);
	// Displays an error box with the inputted text
	*/
	function error($message) {
		global $lang;
		$output["error"] = '<div class="ui-widget">
				<div class="ui-state-highlight ui-corner-all searchError"> 
					<p><span class="ui-icon ui-icon-alert"></span>
					<strong>' . $lang["messages"]["error"] . '</strong> ' . $message . '</p>
				</div>
			  </div>';
		echo json_encode($output);
	}

?>
