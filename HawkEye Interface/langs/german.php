<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//      German Lang File by untergrundbiber      //
	///////////////////////////////////////////////////	
	$lang = array(
					
					"pageTitle"  => "HawkEye Browser",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Filter-Optionen",
									  "players" => "Spieler",
									  "xyz" => "XYZ",
									  "range" => "Reichweite",
									  "keys" => "Stichwörter",
									  "worlds" => "Welten",
									  "dFrom" => "Von Datum",
									  "dTo" => "Bis Datum",
									  "block" => "Block",
									  "search" => "Suche",
									  "exclude" => "Ausschluss-Filter"),
					
					"tips" => array("hideFilter" => "Zeige / Verstecke Filter-Optionen",
									"hideResults" => "Zeige / Verstecke Ergebnisse",
									"actions" => "Aktionen die du suchen willst. Es muss mind. eine ausgewählt werden.",
									"password" => "Passwort um die Suche zu benutzen. Wird nur gebraucht wenn gesetzt.",
									"players" => "(Optional) Liste von Spieler nach denen gesucht werden soll, getrennt durch Kommas.",
									"xyz" => "(Optional) Koordinaten in dessen Umkreis du suchen willst",
									"range" => "(Optional) Suchreichweite um die Koordinaten",
									"keys" => "(Optional) Liste von Stichwörter, getrennt durch Kommas.",
									"worlds" => "(Optional) Liste der Welten, getrennt durch Kommas. Leeres Feld entspricht alle Welten",
									"dFrom" => "(Optional) Start Zeit und Datum für Suchzeitraum",
									"dTo" => "(Optional) Ende Zeit und Datum für Suchzeitraum",
									"block" => "(Optional) Block nach dem gesucht wird bei 'Block zerstört' und 'Block plaziert'",
									"reverse" => "Wenn diese Option aktiviert, wird der Log in chronologischer Reihenfolge angezeigt. Deaktiviere die Option zum Anzeigen von Chat-Protokollen",
									"exclude" => "(Optional) Liste der Stichwörte die aus der Suche ausgeschlossen werden sollen, getrennt durch Kommas."),

					"actions" => array("Block zerstört",
									   "Block plaziert",
									   "Schild platziert",
									   "Chat",
									   "Kommando",
									   "Login",
									   "Logout",
									   "Teleport",
									   "Lava-Eimer",
									   "Wasser-Eimer",
									   "Chest geöffnet",
									   "Tuer benutzt",
									   "Tod durch PVP",
									   "Feuerzeug benutzt",
									   "Hebel benutzt",
									   "Taste benutzt",
									   "Sonstiges",
									   "Explosion",
									   "Feuer",
									   "Block Form",
									   "Blätter-Zerfall",
									   "Tod durch Mob",
									   "Sonstiger Tod",
									   "Item gedroppt",
									   "Item aufgehoben",
									   "Block Fade",
									   "Lava Flow",
									   "Water Flow",
									   "Chest Transaction"),

					"results" => array("title" => "Ergebnisse",
									   "id" => "ID",
									   "date" => "Datum",
									   "player" => "Spieler",
									   "action" => "Aktion",
									   "world" => "Welt",
									   "xyz" => "XYZ",
									   "data" => "Daten"),
									   
					"login" => array("password" => "Passwort: ",
									 "login" => "Login"),

					"messages" => array("clickTo" => "Klicke auf Suche um Ergenisse zu erhalten",
										"breakMe" => "Mach mich nicht kaputt!",
									    "invalidPass" => "Falsches Passwort!",
									    "noActions" => "Du musst mind. eine Aktion auswählen nach der gesucht werden soll!",
									    "noResults" => "Keine Ergebnisse gefunden mit dieser Auswahl",
									    "error" => "Fehler!",
									    "notLoggedIn" => "Du bist nicht angemeldet!")
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>
