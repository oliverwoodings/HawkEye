<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//         French Lang File by oliverw92         //
	///////////////////////////////////////////////////
	$lang = array(
					
					"pageTitle"  => "HawkEye Navigateur",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Options de filtrage",
									  "players" => "Joueurs",
									  "xyz" => "XYZ",
									  "range" => "Gamme",
									  "keys" => "Mots-clés",
									  "worlds" => "Mondes",
									  "dFrom" => "Date de début",
									  "dTo" => "Date de fin",
									  "block" => "Block",
									  "search" => "Rechercher",
									  "exclude" => "Exclure les filtres"),
					
					"tips" => array("hideFilter" => "Afficher / Masquer les options de filtre",
									"hideResults" => "Afficher / Masquer les résultats",
									"actions" => "Actions à rechercher. Vous devez en sélectionner au moins un",
									"password" => "Mot de passe pour utiliser le navigateur. Requis seulement si il a été défini",
									"players" => "(Facultatif) Liste des joueurs que vous souhaitez rechercher séparés par des virgules",
									"xyz" => "(Facultatif) Coordonnées auxquelles vous souhaitez rechercher",
									"range" => "(Facultatif) Etendue des coordonnées spécifiées ci-dessus pour rechercher",
									"keys" => "(Facultatif) Liste des mots-clés séparés par des virgules",
									"worlds" => "(Facultatif) Liste des mondes séparés par des virgules. Laissez vide pour tous les mondes",
									"dFrom" => "(Facultatif) Date et heure de début de la période de recherche",
									"dTo" => "(Facultatif) Date et heure de fin de la période de recherche",
									"block" => "(Facultatif) Blocs à rechercher dans les événements 'Block Break' et 'Block Place'",
									"reverse" => "Si elle est cochée, les résultats seront dans l'ordre chronologique inverse. Décochez la case pour afficher les journaux de conversation",
									"exclude" => "(Facultatif) liste des mots clés à exclure des résultats séparés par des virgules"),
						
					"actions" => array("Bloc cassé",
									   "Bloc posé",
									   "Panneau placé",
									   "Chat",
									   "Commande",
									   "Join",
									   "Quit",
									   "Téléport",
									   "Seau de Lave",
									   "Seau d'eau",
									   "Ouverture coffre",
									   "Interaction porte",
									   "Mort PVP",
									   "Briquet",
									   "Levier",
									   "Bouton",
									   "Autres",
									   "Explosions",
									   "Combustion de bloc",
									   "Formation de neige",
									   "Tombe de feuilles",
									   "Mort Mob",
									   "Mort Autre",
									   "Déposer l'Article",
									   "Ramassage Article"),
					
					"results" => array("title" => "Résultats",
									   "id" => "ID",
									   "date" => "Date",
									   "player" => "Joueur",
									   "action" => "Action",
									   "world" => "Monde",
									   "xyz" => "XYZ",
									   "data" => "Donéées"),
									   
					"login" => array("password" => "Mot de Passe: ",
									 "login" => "Login"),
					
					"messages" => array("clickTo" => "Cliquez sur Rechercher pour récupérer des données",
										"breakMe" => "Arrêtez d'essayer de me m\'arrêter!",
									    "invalidPass" => "Mot de passe invalide!",
									    "noActions" => "Vous devez sélectionner au moins 1 action à rechercher !",
									    "noResults" => "Aucun résultat correspondant à ces options",
									    "error" => "Erreur!",
									    "notLoggedIn" => "Vous n'êtes pas connecté!")
					
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>
