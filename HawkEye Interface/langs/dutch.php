<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//          Dutch Lang File by h0us3cat          //
	///////////////////////////////////////////////////	
	$lang = array(
					
					"pageTitle"  => "HawkEye Browser",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Filter-opties",
									  "players" => "Spelers",
									  "xyz" => "XYZ",
									  "range" => "Radius",
									  "keys" => "Trefwoorden",
									  "worlds" => "Werelden",
									  "dFrom" => "Start datum",
									  "dTo" => "Eind datum",
									  "block" => "Blok",
									  "search" => "Zoek",
									  "exclude" => "Omgekeerde"),
					
					"tips" => array("hideFilter" => "Toon / Verberg filter opties",
									"hideResults" => "Toon / Verberg Resultaten",
									"actions" => "Acties om naar te zoeken. U moet tenminste één selecteren",
									"password" => "Wachtwoord om de browser te gebruiken. Alleen vereist als ingesteld",
									"players" => "(Optioneel) Lijst van spelers die je wilt zoeken, gescheiden door komma's",
									"xyz" => "(Optioneel) Coördinaten waar u wilt rond zoeken",
									"range" => "(Optioneel) Radius rond de bovenstaande coördinaten om naar te zoeken",
									"keys" => "(Optioneel) Lijst van trefwoorden gescheiden door een komma",
									"worlds" => "(Optioneel) Lijst van de werelden, gescheiden door komma's. Leeg laten voor alle werelden",
									"dFrom" => "(Optioneel) Begindatum/tijd van zoekperiode",
									"dTo" => "(Optioneel) Einddatum/tijd van zoekperiode",
									"block" => "(Optioneel) blok om naar te zoeken tijdens blok plaats/breek event",
									"reverse" => "Indien aangevinkt, zal volgorde van de logs zijn in omgekeerde chronologische volgorde. Haal het vinkje weg voor het bekijken van chatlogs",
									"exclude" => "(Optioneel) Lijst van trefwoorden om uit te sluiten van de resultaten gescheiden door komma's"),

					"actions" => array("0" => "Blok breek",
									   "1" => "Blok plaats",
									   "2" => "Bord plaats",
									   "3" => "Chat",
									   "4" => "Commando",
									   "5" => "join",
									   "6" => "Verlaten",
									   "7" => "Teleport",
									   "8" => "Lava emmer",
									   "9" => "Water emmer",
									   "10" => "Open kist",
									   "11" => "Deur",
									   "12" => "PVP dood",
									   "13" => "Flint Steel",
									   "14" => "Lever (Hendel)",
									   "15" => "Button (Knop)",
									   "16" => "Other (Andere)",
									   "17" => "Explosie",
									   "18" => "Blok brand",
									   "19" => "Blok vorm",
									   "20" => "blad verval",
									   "21" => "Mob dood",
									   "22" => "Andere dood",
									   "23" => "Item drop",
									   "24" => "Item pickup",
									   "25" => "Blok Fade",
									   "26" => "Lava Flow",
									   "27" => "Water Flow",
									   "28" => "Chest Transaction",
									   "29" => "Sign Break",
									   "30" => "Painting Break",
									   "31" => "Painting Place",
									   "32" => "Enderman Pickup",
									   "33" => "Enderman Place",
									   "34" => "Tree Grow",
									   "35" => "Mushroom Grow"),

					"results" => array("title" => "Resultaten",
									   "id" => "ID",
									   "date" => "Datum",
									   "player" => "Speler",
									   "action" => "Actie",
									   "world" => "Wereld",
									   "xyz" => "XYZ",
									   "data" => "Data"),
									   
					"login" => array("password" => "Wachtwoord: ",
									 "login" => "Login"),

					"messages" => array("clickTo" => "Klik op zoeken om gegevens op te halen",
										"breakMe" => "Stop met het proberen me te breken!",
										"invalidPass" => "Ongeldig wachtwoord!",
										"noActions" => "U moet minstens één actie selecteren om te zoeken!",
										"noResults" => "Er zijn geen resultaten gevonden die voldoen aan deze opties",
										"error" => "Error!",
										"notLoggedIn" => "U bent niet ingelogd!")
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>