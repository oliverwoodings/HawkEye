<?php
	
	///////////////////////////////////////////////////
	//         DataLog Interface Search File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////

	//Include config, lang pack and MySQL connector
	include("config.php");
	include("langs/" . $config["langFile"]);
	
	if (!isset($_GET["data"]))
		return error($lang["messages"]["breakMe"]);
		
	$data = json_decode(stripslashes($_GET["data"]), true);
	
	if (strtolower($data["password"]) != strtolower($config["password"]) && $config["password"] != "")
		return error($lang["messages"]["invalidPass"]);
	
	$sql = "SELECT datalog.data_id, datalog.date, dl_players.player, datalog.action, dl_worlds.world, datalog.x, datalog.y, datalog.z, datalog.data, datalog.plugin FROM `datalog`, `dl_players`, `dl_worlds` WHERE ";
	$args = array();
	
	if ($data["dateFrom"] != "" && $data["dateFrom"] != " ")
		array_push($args, "`date` >= '" . $data["dateFrom"] . "'");
	if ($data["dateTo"] != "" && $data["dateTo"] != " ")
		array_push($args, "`date` <= '" . $data["dateTo"] . "'");
	if ($data["players"][0] != "") {
		foreach ($data["players"] as $key => $val)
			$data["players"][$key] = "'" . $val . "%'";
		array_push($args, "(LOWER(dl_players.player) LIKE " . join(" OR LOWER(dl_players.player) LIKE ", $data["players"]) . ")");
	}
	if ($data["worlds"][0] != "") {
		foreach ($data["worlds"] as $key => $val)
			$data["worlds"][$key] = "'" . $val . "%'";
		array_push($args, "(LOWER(dl_worlds.world) LIKE " . join(" OR LOWER(dl_worlds.world) LIKE ", $data["worlds"]) . ")");
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
	if ($data["keywords"][0] != "") {
		foreach ($data["keywords"] as $key => $val)
			$data["keywords"][$key] = "'%" . $val . "%'";
		array_push($args, "`data` LIKE " . join(" OR `data` LIKE ", $data["keywords"]));
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
	while ($entry = mysql_fetch_object($res)) {
		$fdata = $entry->data;
		if (strlen($entry->data) > 40)
			$fdata = substr($fdata, 0, 40) . "...";
		$action = $entry->action;
		if ($action == 0) {
			foreach ($items as $i) {
				$item = explode(",", $i);
				if ((int)$item[0] == (int)$fdata)
					$fdata = $item[1];
			}
		}
		if ($action == 1) {
			$arr = explode("-", $fdata);
			if (count($arr) == 1) {
				foreach ($items as $i) {
					$item = explode(",", $i);
					if ((int)$item[0] == (int)$arr[0])
						$fdata = $item[1] . " replaced by ";
				}
			}
			else {
				foreach ($items as $i) {
					$item = explode(",", $i);
					if ((int)$item[0] == (int)$arr[1])
						$fdata .= $item[1];
				}
			}
		}
		if ($action == 16) {
			$arr = explode("-", $fdata);
			if (count($arr) > 0)
				$action = array_shift($arr);
			$fdata = join("-", $arr);
		}
		$action = str_replace(array_reverse(array_keys($lang["actions"])), array_reverse($lang["actions"]), $action);
		echo '<tr><td>' . $entry->data_id . '</td><td width="155px">' . $entry->date . "</td><td>" . $entry->player . "</td><td>" . $action . "</td><td>" . $entry->world . "</td><td>" . round($entry->x, 1).",".round($entry->y, 1).",".round($entry->z, 1) . '</td><td id="dataEntry" title="' . $entry->data . '">' . $fdata . "</td></tr>";
	}
	echo "</table>";
		
	
	/*
	// FUNCTION: error($message);
	// Displays an error box with the inputted text
	*/
	function error($message) {
		echo '<div class="ui-widget">
				<div class="ui-state-highlight ui-corner-all searchError"> 
					<p><span class="ui-icon ui-icon-alert"></span>
					<strong>' . $lang["messages"]["error"] . '</strong> ' . $message . '</p>
				</div>
			  </div>';
	}

?>