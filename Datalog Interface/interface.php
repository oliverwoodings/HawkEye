<?php
	
	///////////////////////////////////////////////////
	//         DataLog Interface Search File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////

	//Include config and MySQL connector
	include("config.php");
	
	if (!isset($_GET["data"]))
		return error("Stop trying to break me!");
		
	$data = json_decode(stripslashes($_GET["data"]), true);
	
	if (strtolower($data["password"]) != strtolower($config["password"]) && $config["password"] != "")
		return error("Invalid password!");
	
	$sql = "SELECT * FROM `datalog` WHERE ";
	$args = array();
	
	if ($data["dateFrom"] != "" && $data["dateFrom"] != " ")
		array_push($args, "`date` >= '" . $data["dateFrom"] . "'");
	if ($data["dateTo"] != "" && $data["dateTo"] != " ")
		array_push($args, "`date` <= '" . $data["dateTo"] . "'");
	if ($data["players"][0] != "") {
		foreach ($data["players"] as $key => $val)
			$data["players"][$key] = "'" . $val . "'";
		array_push($args, "LOWER(`player`) LIKE " . join(" OR LOWER(`player`) LIKE ", $data["players"]));
	}
	
	if (count($data["actions"]) == 0)
		return error("You must select at least 1 action to search by!");
	else
		array_push($args, "`action` IN (" . join(",", $data["actions"]) . ")");
	
	$range = $config["radius"];
	if ($data["range"] != "")
		$range = $data["range"];
	if ($data["loc"][0] != "")
		array_push($args, "(`x` BETWEEN " . ($data["loc"][0] - range) . " AND " . ($data["loc"][0] + range) . ")");
	if ($data["loc"][1] != "")
		array_push($args, "(`y` BETWEEN " . ($data["loc"][1] - range) . " AND " . ($data["loc"][1] + range) . ")");
	if ($data["loc"][2] != "")
		array_push($args, "(`z` BETWEEN " . ($data["loc"][2] - range) . " AND " . ($data["loc"][2] + range) . ")");
	if ($data["worlds"][0] != "") {
		foreach ($data["worlds"] as $key => $val)
			$data["worlds"][$key] = "'" . $val . "'";
		array_push($args, "LOWER(`world`) = (" . join(" OR ", $data["worlds"]) . ")");
	}
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
		return error("No results found matching those options");
		
	//Echo table header
	echo "<table class=\"resultsTable\">
			<tr>
				<th>Date</th>
				<th>Player</th>
				<th>Action</th>
				<th>World</th>
				<th>XYZ</th>
				<th>Data</th>
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
		$action = str_replace(array(16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0), array("Other", "Button", "Lever", "Flint Steel", "PVP Death", "Door Interact", "Open Chest", "Water Bucket", "Lava Bucket", "Teleport", "Quit", "Join", "Command", "Chat", "Sign Place", "Block Place", "Block Break"), $action);
		echo '<tr><td width="155px">' . $entry->date . "</td><td>" . $entry->player . "</td><td>" . $action . "</td><td>" . $entry->world . "</td><td>" . round($entry->x, 1).",".round($entry->y, 1).",".round($entry->z, 1) . '</td><td id="dataEntry" title="' . $entry->data . '">' . $fdata . "</td></tr>";
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
					<strong>Error!</strong> ' . $message . '</p>
				</div>
			  </div>';
	}

?>