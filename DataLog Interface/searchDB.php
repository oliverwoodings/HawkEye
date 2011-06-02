<?php

	///////////////////////////////////////////////////
	//         DataLog Interface Search File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////

	ini_set('display_errors', 1); error_reporting(E_ALL);
	
	//Grab data from POST headers
	$password = sanit($_POST["password"]);
	$world    = sanit($_POST["world"]);
	$username = sanit(strtolower($_POST["name"]));
	$x        = sanit($_POST["x"]);
	$z        = sanit($_POST["z"]);
	$range    = sanit($_POST["range"]);
	$filter   = sanit($_POST["filter"]);
	$itemID   = sanit($_POST["item"]);
	$actions  = substr(sanit($_POST["actions"]), 8, -1);
	
	//Connect to MySQL and load config
	include("config.php");
	
	//Check password
	if ($password != $config["password"] && $config["password"] != "") {
		echo "Incorrect password!";
		return;
	}
	
	//Set up SQL statement
	$sql = "SELECT * FROM " . $config["dbTable"] . " WHERE ";
	$whereArr = array();
	
	if ($username)
		array_push($whereArr, "player='".$username."'");
	if ($x)
		array_push($whereArr, '(x BETWEEN ' . ($x - $range) . " AND " . ($x + $range) . ")");
	if ($z)
		array_push($whereArr, '(z BETWEEN ' . ($z - $range) . " AND " . ($z + $range) . ")");
	if ($filter)
		array_push($whereArr, "data LIKE '%" . $filter . "%'");
	if ($world)
		array_push($whereArr, "world='" . $world . "'");

	//Loop through actions
	$actions = explode("@", $actions);
	$actArr  = array();
	foreach ($actions as $i)
		array_push($actArr, "action=".$i);
	if (count($actArr) > 1)
		array_push($whereArr, '(' . implode(" OR ", $actArr) . ')');
	if (count($actArr) == 1)
		array_push($whereArr, $actArr[0]);
	
	//Finish statement and query
	$sql .= implode(" AND ", $whereArr) . " ORDER BY dataid DESC";
	$result = mysql_query($sql);
	if (!$result)
		die($sql.mysql_error());
	echo "<table class='resTable' cellpadding='0'>";
	echo '<tr class="headRow"><td>Date</td><td>Player</td><td style="width: 80px">Action</td><td>x</td><td>y</td><td>z</td><td>Block/Text/Command</td></tr>';
	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		
		//Get data from row
		$date    = $row["date"];
		$action  = str_replace(array(12,11,10,9,8,7,6,5,4,3,2,1,0), array("PVP Death", "Door Interact", "Open Chest", "Water Bucket", "Lava Bucket", "Teleport", "Disconnect", "Login", "Command", "Chat", "Sign Placed", "Block Placed", "Block Broken"), $row["action"]);
		$x       = $row["x"];
		$y       = $row["y"];
		$z       = $row["z"];
		$data    = $row["data"];
		$usrname = $row["player"];
		
		//Turn type into block name
		if ($action == 0 || $action == 1) {
			if ($itemID > 0 && $data != $itemID)
				continue;
			$blocks = file_get_contents("list.txt");
			$blocks = explode("</option>", str_replace('<option value="', "", $blocks));
			foreach ($blocks as $i) {
				$block = explode('">', $i);
				if ($block[0] == $data)
					$data = $block[1];
			}
		}
		
		echo "<tr><td style='width: 150px;'>".$date."</td><td>".$usrname."</td><td>".$action."</td><td>".$x."</td><td>".$y."</td><td>".$z."</td><td >".$data."</td></tr>";
	}
	echo "</table>";
	
	mysql_close($con);
	
	function sanit($string) {
		return mysql_escape_string($string);
	}

?>