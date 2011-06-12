<?php

	///////////////////////////////////////////////////
	//         DataLog Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//         French Lang File by oliverw92         //
	///////////////////////////////////////////////////
	$lang = array(
					
					"pageTitle"  => "DataLog Navigateur",
					"title" => "DataLog",
					
					"filter" => array("title" => "Options de filtrage",
									  "password" => "Mot de passe",
									  "players" => "Joueurs",
									  "xyz" => "XYZ",
									  "range" => "Gamme",
									  "keys" => "Mots-clés",
									  "worlds" => "Mondes",
									  "dFrom" => "Date From",
									  "dTo" => "Date To",
									  "block" => "Block",
									  "search" => "Rechercher"),
					
					"tips" => array("hideFilter" => "Afficher / Masquer les options de filtre",
									"hideResults" => "Afficher / Masquer les résultats",
									"actions" => "Actions à rechercher. Vous devez sélectionner au moins un",
									"password" => "Mot de passe pour utiliser le navigateur. Requis seulement si elle est définie",
									"players" => "(Facultatif) Liste des joueurs que vous souhaitez rechercher séparés par des virgules",
									"xyz" => "(Facultatif) Coordonnées vous souhaitez rechercher dans",
									"range" => "(Facultatif) Gamme autour de la coords ci-dessus pour rechercher",
									"keys" => "(Facultatif) Liste des mots-clés séparés par des virgules",
									"worlds" => "(Facultatif) Liste des mondes séparés par des virgules. Laissez vide pour tous les mondes",
									"dFrom" => "(Facultatif) Heure et date pour commencer la recherche de",
									"dTo" => "(Facultatif) Date et heure de chercher à",
									"block" => "(Facultatif) Block à rechercher dans 'Block Break' et 'Block Place' événements"),
					
					"actions" => array("Bloc de Pause",
									   "Bloc de Place",
									   "Sign Place",
									   "Chat",
									   "Commande",
									   "Arrivée",
									   "Quitter",
									   "Téléport",
									   "Seau de Lava",
									   "Seau d'eau",
									   "Ouvrir la poitrine",
									   "Interact Porte",
									   "PVP mort",
									   "Flint et de l'acier",
									   "Levier",
									   "Bouton",
									   "Autres",
									   "Explosion",
									   "Bloc de Combustion",
									   "Formulaire de neige",
									   "Feuille Decay"),
					
					"results" => array("title" => "Résultats",
									   "id" => "ID",
									   "date" => "Date",
									   "player" => "Joueur",
									   "action" => "Action",
									   "world" => "Monde",
									   "xyz" => "XYZ",
									   "data" => "Data"),
					
					"messages" => array("clickTo" => "Cliquez sur Rechercher pour récupérer des données",
										"breakMe" => "Arrêtez d'essayer de me briser!",
									    "invalidPass" => "Mot de passe invalide!",
									    "noActions" => "Vous devez sélectionner au moins 1 action de recherche en!",
									    "noResults" => "Aucun résultat correspondant à ces options",
									    "error" => "Erreur!")
									  
					
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>