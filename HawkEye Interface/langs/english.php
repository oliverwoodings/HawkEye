<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//     Use this file as a template for           //
	//     creating other language files. To use     //
	//     a language file, change the setting in    //
	//     config.php to point to the new file       //
	///////////////////////////////////////////////////
	$lang = array(
					
					"pageTitle"  => "HawkEye Browser",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Filter Options",
									  "players" => "Players",
									  "xyz" => "XYZ",
									  "range" => "Range",
									  "keys" => "Keywords",
									  "worlds" => "Worlds",
									  "dFrom" => "Date From",
									  "dTo" => "Date To",
									  "block" => "Block",
									  "search" => "Search",
									  "exclude" => "Exclude Filter",
									  "selectall" => "Select All"),
					
					"tips" => array("hideFilter" => "Show/Hide Filter Options",
									"hideResults" => "Show/Hide Results",
									"actions" => "Actions to search for. You must select at least one",
									"password" => "Password to use the browser. Required only if set",
									"players" => "(Optional) List of players you wish to search for separated by commas",
									"xyz" => "(Optional) Coordinates you wish to search around",
									"range" => "(Optional) Range around the coords above to search for",
									"keys" => "(Optional) List of keywords seperated by commas",
									"worlds" => "(Optional) List of worlds seperated by commas. Leave blank for all worlds",
									"dFrom" => "(Optional) Time and date to start searching from",
									"dTo" => "(Optional) Time and date to search up to",
									"block" => "(Optional) Block to search for in 'Block Break' and 'Block Place' events",
									"reverse" => "If checked, order of logs will be in reverse-chronological order. Uncheck for viewing chat logs",
									"exclude" => "(Optional) List of keywords to exclude from results separated by commas",
									"selectall" => "Click to select all actions, click again to deselect all"),
					
					"actions" => array("0" => "Block Break",
									   "1" => "Block Place",
									   "2" => "Sign Place",
									   "3" => "Chat",
									   "4" => "Command",
									   "5" => "Join",
									   "6" => "Quit",
									   "7" => "Teleport",
									   "8" => "Lava Bucket",
									   "9" => "Water Bucket",
									   "10" => "Open Chest",
									   "11" => "Door Interact",
									   "12" => "PVP Death",
									   "13" => "Flint Steel",
									   "14" => "Lever",
									   "15" => "Button",
									   "16" => "Other",
									   "17" => "Explosion",
									   "18" => "Block Burn",
									   "19" => "Block Form",
									   "20" => "Leaf Decay",
									   "21" => "Mob Death",
									   "22" => "Other Death",
									   "23" => "Item Drop",
									   "24" => "Item Pickup",
									   "25" => "Block Fade",
									   "26" => "Lava Flow",
									   "27" => "Water Flow",
									   "28" => "Chest Transaction",
									   "29" => "Sign Break",
									   "30" => "Painting Break",
									   "31" => "Painting Break",
									   "32" => "Enderman Pickup",
									   "33" => "Enderman Place",
									   "34" => "Tree Grow",
									   "35" => "Mushroom Grow"),
					
					"results" => array("title" => "Results",
									   "id" => "ID",
									   "date" => "Date",
									   "player" => "Player",
									   "action" => "Action",
									   "world" => "World",
									   "xyz" => "XYZ",
									   "data" => "Data"),
									   
					"login" => array("password" => "Password: ",
									 "login" => "Login"),
					
					"messages" => array("clickTo" => "Click Search to Retrieve Data",
										"breakMe" => "Stop trying to break me!",
									    "invalidPass" => "Invalid Password!",
									    "noActions" => "You must select at least 1 action to search by!",
									    "noResults" => "No results found matching those options",
									    "error" => "Error!",
									    "notLoggedIn" => "You are not logged in!")
									  
					
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>