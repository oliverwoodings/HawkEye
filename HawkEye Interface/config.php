<?php
	///////////////////////////////////////////////////
	//         HawkEye Interface Config File         //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//     Edit the config array below with your     //
	//     details. Make sure all strings are        //
	//     escaped. If you can't work this out,      //
	//     ask in the thread on bukkit.org.          //
	///////////////////////////////////////////////////
	$config = array(
					//Enter your MySQL database information
					//Do not change 'dbTable'
					"dbHost"  => "localhost",
					"dbDbase" => "minecraft",
					"dbPort"  => 3306,
					"dbUser"  => "root",
					"dbPass"  => "",
					"dbTable" => "hawkeye",
					"dbPlayerTable" => "hawk_players",
					"dbWorldTable" => "hawk_worlds",
					
					//Set this to the password you want people to have to use to access the interface
					//Leave blank for no password
					"password" => "",
					
					//Default radius to search for if user supplies a location
					"radius" => 30,
					
					//Limit the maximum number of results that can be returned. Do not use quotes
					//Set to 0 for no limit
					"maxResults" => 0,
					
					//Language pack - default is english.php
					//You can create your own based off the english.php file,
					//then change the name below to your language file name
					"langFile" => "english.php",
					
					//Log queries to log.txt
					//Useful to keep track of who is querying what
					"logQueries" => true,
					
					);
	
	$con = mysql_connect($config["dbHost"] . ":" + $config["dbPort"], $config["dbUser"], $config["dbPass"]);
	if (!$con)
		return error("Could not connect to MySQL Database!");
	if (!mysql_select_db($config["dbDbase"], $con))
		return error(mysql_error());
		
	mysql_query("SET NAMES UTF8");
	
	function handleError($errno, $errstr, $errfile, $errline, array $errcontext) {
    	// error was suppressed with the @-operator
    	if (0 === error_reporting()) return false;
    	throw new ErrorException($errstr, 0, $errno, $errfile, $errline);
	}
		
?>