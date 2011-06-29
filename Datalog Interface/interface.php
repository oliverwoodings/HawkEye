<?php
	
	///////////////////////////////////////////////////
	//         DataLog Interface Search File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	
	session_start();
	
	//Include config, lang pack and MySQL connector
	include("config.php");
	include("langs/" . $config["langFile"]);
	
	//If not logged in, throw an error
	if (!isset($_SESSION["loggedin"]) && $config["password"] != "")
		return error($lang["messages"]["notLoggedIn"]);
	
	if (!isset($_GET["data"]))
		return error($lang["messages"]["breakMe"]);
		
	$data = json_decode(stripslashes($_GET["data"]), true);
		
	//Get players
	$players = array();
	$res = mysql_query("SELECT * FROM dl_players");
	if (!$res)
		return error(mysql_error());
	if (mysql_num_rows($res) == 0)
		return error($lang["messages"]["noResults"]);
	while ($player = mysql_fetch_object($res))
		$players[$player->player_id] = $player->player;
	
	//Get worlds
	$worlds = array();
	$res = mysql_query("SELECT * FROM dl_worlds");
	if (!$res)
		return error(mysql_error());
	if (mysql_num_rows($res) == 0)
		return error($lang["messages"]["noResults"]);
	while ($world = mysql_fetch_object($res))
		$worlds[$world->world_id] = $world->world;
	
	$sql = "SELECT * FROM `datalog` WHERE ";
	$args = array();
	
	if ($data["players"][0] != "") {
		$pids = array();
		foreach ($data["players"] as $key => $val)
			foreach ($players as $key2 => $val2)
				if (strstr($val2, $val))
					array_push($pids, $key2);
		if (count($pids) > 0)
			array_push($args, "player_id IN (" . join(",", $pids) . ")");
	}
	if ($data["worlds"][0] != "") {
		$wids = array();
		foreach ($data["worlds"] as $key => $val)
			foreach ($worlds as $key2 => $val2)
				if (strstr($val2, $val))
					array_push($wids, $key2);
		if (count($wids) > 0)
			array_push($args, "world_id IN (" . join(",", $wids) . ")");
	}
	if (count($data["actions"]) == 0)
		return error($lang["messages"]["noActions"]);
	else
		array_push($args, "`action` IN (" . join(",", $data["actions"]) . ")");
	
	$range = $config["radius"];
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
	
	$sql .= join(" AND ", $args);
	if ($config["maxResults"] > 0)
		$sql .= " LIMIT " . $config["maxResults"];
	$res = mysql_query($sql);
	
	if (!$res)
		return error(mysql_error());
	
	if (mysql_num_rows($res) == 0)
		return error($lang["messages"]["noResults"]);
	
	//Echo table header
	echo "<table class=\"resultsTable\">
			<tr>
				<th>" . $lang["results"]["id"] . "</th>
				<th>" . $lang["results"]["date"] . "</th>
				<th>" . $lang["results"]["player"] . "</th>
				<th>" . $lang["results"]["action"] . "</th>
				<th>" . $lang["results"]["world"] . "</th>
				<th>" . $lang["results"]["xyz"] . "</th>
				<th>" . $lang["results"]["data"] . "</th>
			</tr>";
			
	//Loop through results
	$items = explode("\n", file_get_contents("items.txt"));
	$results = array();
	while ($entry = mysql_fetch_object($res))
		array_push($results, $entry);
	if ($data["reverse"] == TRUE)
		$results = array_reverse($results);
	foreach ($results as $entry) {
		$fdata = $entry->data;
		if (strlen($entry->data) > 40)
			$fdata = substr($fdata, 0, 40) . "...";
		$action = $entry->action;
		if ($action == 0) {
			$fdata = getBlockName($fdata);
		}
		if ($action == 1) {
			$arr = explode("-", $fdata);
			if (count($arr) > 1)
				$fdata = getBlockName($arr[0]) . " replaced by " . getBlockName($arr[1]);
			else $fdata = getBlockName($arr[0]);
		}
		if ($action == 16) {
			$arr = explode("-", $fdata);
			if (count($arr) > 0)
				$action = array_shift($arr);
			$fdata = join("-", $arr);
		}
		if ($action == 2) {
			$fdata = str_replace("|", "<br />", $fdata);
		}
		$action = str_replace(array_reverse(array_keys($lang["actions"])), array_reverse($lang["actions"]), $action);
		echo '<tr><td>' . $entry->data_id . '</td><td width="155px">' . $entry->date . "</td><td>" . $players[$entry->player_id] . "</td><td>" . $action . "</td><td>" . $worlds[$entry->world_id] . "</td><td>" . round($entry->x, 1).",".round($entry->y, 1).",".round($entry->z, 1) . '</td><td id="dataEntry" title="' . $entry->data . '">' . $fdata . "</td></tr>";
	}
	echo "</table>";
	
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
		echo '<div class="ui-widget">
				<div class="ui-state-highlight ui-corner-all searchError"> 
					<p><span class="ui-icon ui-icon-alert"></span>
					<strong>' . $lang["messages"]["error"] . '</strong> ' . $message . '</p>
				</div>
			  </div>';
	}

?>