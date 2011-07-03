<?php

	///////////////////////////////////////////////////
	//         DataLog Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//          Dutch Lang File by h0us3cat          //
	///////////////////////////////////////////////////	
	$lang = array(
					
					"pageTitle"  => "DataLog Browser",
					"title" => "DataLog",
					
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
									  "exclude" => "OMgekeerde volgorde"),
					
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

					"actions" => array("Blok breek",
									   "Blok plaats",
									   "Bord plaats",
									   "Chat",
									   "Commando",
									   "join",
									   "Verlaten",
									   "Teleport",
									   "Lava emmer",
									   "Water emmer",
									   "Open kist",
									   "Deur",
									   "PVP dood",
									   "Flint Steel",
									   "Lever (Hendel)",
									   "Button (Knop)",
									   "Other (Andere)",
									   "Explosie",
									   "Blok brand",
									   "sneeuw vorm",
									   "blad verval",
									   "Mob dood",
									   "Andere dood",
									   "Item drop",
									   "Item pickup"),

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