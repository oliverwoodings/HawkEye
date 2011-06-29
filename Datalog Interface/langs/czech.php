<?php

    ///////////////////////////////////////////////////
    //         DataLog Interface Lang File           //
    //                 by oliverw92                  //
    ///////////////////////////////////////////////////
	//           Czech Lang File by tombik           //
	///////////////////////////////////////////////////
    $lang = array(

                    "pageTitle"  => "DataLog prohlížeč",
                    "title" => "DataLog",

                    "filter" => array("title" => "Nastavení filtru",
                                      "players" => "Hráči",
                                      "xyz" => "XYZ",
                                      "range" => "Dosah od XYZ",
                                      "keys" => "Klíčová slova",
                                      "worlds" => "Světy",
                                      "dFrom" => "Od",
                                      "dTo" => "Do",
                                      "block" => "Blok",
                                      "search" => "Hledat",
                                      "reverse" => "Převrátit výsledek",
									  "exclude" => "Vyloučit Filtr"),

                    "tips" => array("hideFilter" => "Zobrazit/Skrýt nastavení filtru",
                                    "hideResults" => "Zobrazit/skrýt výsledky",
                                    "actions" => "Akce k vyhledávání. Musíte vybrat alespoň jednu",
                                    "password" => "Heslo k DataLogu. Vyžadováno pouze pokud je nastaveno",
                                    "players" => "(Volitelné) Seznam hráčů oddělený čárkami, které chcete vyhledat",
                                    "xyz" => "(Optional) Souřadnice okolo kterých chcete hledat",
                                    "range" => "(Optional) Dosah okolo XYZ, kde má být hledáno",
                                    "keys" => "(Optional) Seznam klíčových slov oddělené čárkami",
                                    "worlds" => "(Optional) Seznam světů oddělených čárkami. Ponechte prázdné pro všechny",
                                    "dFrom" => "(Optional) Čas a datum od kdy hledat",
                                    "dTo" => "(Optional) Čas a datum do kdy hledat",
                                    "block" => "(Optional) Bloky k vybrání při zvolení 'Block Break' nebo/a 'Block Place'",
                                    "reverse" => "Pokud toto zaškrnete, výsledky se chrnonologicky převrátí",
                                    "exclude" => "(Optional) Seznam klíčových slov, vyloučit z výsledků oddělené čárkami"),

                    "actions" => array("Blok Zničen",
                                       "Blok Položen",
                                       "Sign Položen",
                                       "Chat",
                                       "Příkaz",
                                       "Připojení",
                                       "Odpojení",
                                       "Teleport",
                                       "Lava Bucket",
                                       "Water Bucket",
                                       "Otevření Chest",
                                       "Pohyb Dveři",
                                       "PVP Smrt",
                                       "Flint Steel",
                                       "Lever",
                                       "Button",
                                       "Other",
                                       "Explosion",
                                       "Blok Spálen",
                                       "Snow Form",
                                       "Leaf Decay",
									   "Mob Smrti",
									   "Ostatní Smrti",
									   "Pokles Položky",
									   "Vyzvednout Položky"),

                    "results" => array("title" => "Výsledky",
                                       "id" => "ID",
                                       "date" => "Datum",
                                       "player" => "Hráči",
                                       "action" => "Akce",
                                       "world" => "Svět",
                                       "xyz" => "XYZ",
                                       "data" => "Data"),
									   
					"login" => array("password" => "Heslo: ",
									 "login" => "Login"),

                    "messages" => array("clickTo" => "Klikni na Hledat pro zjištění dat",
                                        "breakMe" => "Přestaňse snažit mě zničit!",
                                        "invalidPass" => "Chybné heslo!",
                                        "noActions" => "Musíte zvolit alespoň jednu akci!",
                                        "noResults" => "Mebyly nalezeny žádné výsledky při tomto nastavení",
                                        "error" => "Chyba!")
 
                    );


?>