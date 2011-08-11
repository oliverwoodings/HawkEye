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
									  "exclude" => "Exclude Filter"),
					
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
									"exclude" => "(Optional) List of keywords to exclude from results separated by commas"),
					
					"actions" => array("Block Break",
									   "Block Place",
									   "Sign Place",
									   "Chat",
									   "Command",
									   "Join",
									   "Quit",
									   "Teleport",
									   "Lava Bucket",
									   "Water Bucket",
									   "Open Chest",
									   "Door Interact",
									   "PVP Death",
									   "Flint Steel",
									   "Lever",
									   "Button",
									   "Other",
									   "Explosion",
									   "Block Burn",
									   "Block Form",
									   "Leaf Decay",
									   "Mob Death",
									   "Other Death",
									   "Item Drop",
									   "Item Pickup",
									   "Block Fade",
									   "Lava Flow",
									   "Water Flow",
									   "Chest Transaction"),
					
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