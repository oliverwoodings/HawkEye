<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//         French Lang File by oliverw92         //
	///////////////////////////////////////////////////
	$lang = array(
					
					"pageTitle"  => "HawkEye - Outil d'administration",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Options de filtrage",
									  "players" => "Joueurs",
									  "xyz" => "XYZ",
									  "range" => "Gamme",
									  "keys" => "Mots-clés",
									  "worlds" => "Mondes",
									  "dFrom" => "Date début",
									  "dTo" => "Date fin",
									  "block" => "Block",
									  "search" => "Rechercher",
									  "exclude" => "Exclure les filtres"),
					
					"tips" => array("hideFilter" => "Afficher / Masquer les options de filtrage",
									"hideResults" => "Afficher / Masquer les résultats",
									"actions" => "Actions à rechercher. Vous devez en sélectionner au moins une",
									"password" => "Mot de passe pour utiliser le navigateur. Requis seulement s'il a été défini",
									"players" => "(Facultatif) Liste des joueurs que vous souhaitez rechercher séparés par des virgules",
									"xyz" => "(Facultatif) Coordonnées auxquelles vous souhaitez rechercher",
									"range" => "(Facultatif) Etendue des coordonnées spécifiées ci-dessus pour rechercher",
									"keys" => "(Facultatif) Liste des mots-clés séparés par des virgules",
									"worlds" => "(Facultatif) Liste des mondes séparés par des virgules. Laissez vide pour tous les mondes",
									"dFrom" => "(Facultatif) Date et heure de début de la période de recherche",
									"dTo" => "(Facultatif) Date et heure de fin de la période de recherche",
									"block" => "(Facultatif) Blocs à rechercher dans les événements 'Bloc cassé' et 'Bloc posé'",
									"reverse" => "Si elle est cochée, les résultats seront dans l'ordre chronologique inverse. Décochez la case pour afficher les journaux de conversation",
									"exclude" => "(Facultatif) liste des mots clés à exclure des résultats séparés par des virgules"),
						
					"actions" => array("0" => "Bloc cassé",
									   "1" => "Bloc posé",
									   "2" => "Panneau placé",
									   "3" => "Chat",
									   "4" => "Commande",
									   "5" => "Connexion",
									   "6" => "Déconnexion",
									   "7" => "Téléportation",
									   "8" => "Seau de Lave",
									   "9" => "Seau d'eau",
									   "10" => "Ouverture coffre",
									   "11" => "Interaction porte",
									   "12" => "Mort PVP",
									   "13" => "Briquet",
									   "14" => "Levier",
									   "15" => "Bouton",
									   "16" => "Autres",
									   "17" => "Explosions",
									   "18" => "Combustion de bloc",
									   "19" => "Formation de bloc",
									   "20" => "Chute de feuilles",
									   "21" => "Mort Monstre",
									   "22" => "Mort Autre",
									   "23" => "Déposer l'Article",
									   "24" => "Ramassage Article",
									   "25" => "Bloc Fade",
									   "26" => "Ecoulement Lave",
									   "27" => "Ecoulement eau",
									   "28" => "Transaction coffre",
									   "29" => "Panneau posé",
									   "30" => "Peinture posée",
									   "31" => "Peinture cassée",
									   "32" => "Enderman Pickup",
									   "33" => "Enderman Place",
									   "34" => "Tree Grow",
									   "35" => "Mushroom Grow"),
					
					"results" => array("title" => "Résultats",
									   "id" => "ID",
									   "date" => "Date",
									   "player" => "Joueur",
									   "action" => "Action",
									   "world" => "Monde",
									   "xyz" => "XYZ",
									   "data" => "Données"),
									   
					"login" => array("password" => "Mot de Passe: ",
									 "login" => "Login"),
					
					"messages" => array("clickTo" => "Cliquez sur Rechercher pour récupérer des données",
										"breakMe" => "Arrêtez d'essayer de m\'arrêter !",
									    "invalidPass" => "Mot de passe invalide !",
									    "noActions" => "Vous devez sélectionner au moins 1 action à rechercher !",
									    "noResults" => "Aucun résultat correspondant à ces options",
									    "error" => "Erreur !",
									    "notLoggedIn" => "Vous n'êtes pas connecté !")
					
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>
