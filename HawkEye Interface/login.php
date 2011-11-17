<?php 

	session_start();
	
	//Include config and lang pack
	include("config.php");
	include("langs/" . $hawkConfig["langFile"]);
	
	if ($hawkConfig["forumAuth"])
	{
		//The user wants to use forum authentication.  Let's check if the user's authenticated.
		if ($isAuth)
		{
			//They're authenticated! Let's log them in and tie them to a session
			$_SESSION["loggedIn"] = true;
			$_SESSION["forumAuth"] = true;
			header("Location: index.php");
		}
		else
		{
			//Okay, they don't have authentication did they previously?
			if(isset($_SESSION["forumAuth"]))
			{
				//They did, we need to log them out for security's sake.
				unset($_SESSION["forumAuth"]);
				unset($_SESSION["loggedIn"]);
			}
		}
	}

	if (isset($_SESSION["loggedIn"]))
		header("Location: index.php");
		
	if (isset($_GET["page"]) && $_GET["page"] == "logout") {
        unset($_SESSION["loggedIn"]);
        header("Location: index.php");
	}
	
	if (isset($_GET["page"]) && $_GET["page"] == "login") {
	    if (isset($_POST["pass"]) && $_POST["pass"] == $hawkConfig["password"]) {
	    	$_SESSION["loggedIn"] = true;
	    	header("Location: index.php");
	    }
	}
	
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    	<script type="text/javascript" src="js/jquery-1.5.1.min.js"></script>
        <script type="text/javascript" src="js/jquery-ui-1.8.13.custom.min.js"></script>
        <script type="text/javascript" src="js/scripts.js"></script>
        <script type="text/javascript" src="js/jquery.uniform.js"></script>

        <script type="text/javascript" src="http://cdn.jquerytools.org/1.2.5/tiny/jquery.tools.min.js"></script>
        <link rel="stylesheet" type="text/css" href="css/styles.css" />
        <link rel="stylesheet" type="text/css" href="css/custom-theme/jquery-ui-1.8.13.custom.css" />
		<link rel="stylesheet" type="text/css" href="css/uniform.default.css" />
        <link rel="icon" type="image/png" href="images/favicon.ico" />
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title><?php echo $lang["pageTitle"]; ?></title>
    </head>

    
    <body>
    
        <div class="headerLogin">
        	<div class="innerHeader">
            	<p><?php echo $lang["title"]; ?></p>
            </div>
        </div>
        
        <div class="container">
        	<div class="innerContainer">
        	
        	<?php
        	
        		if (isset($_POST["pass"]) && $_POST["pass"] != $hawkConfig["password"]) {
	        		echo '<div class="ui-widget">
							<div class="ui-state-highlight ui-corner-all searchError"> 
							<p><span class="ui-icon ui-icon-alert"></span>
							<strong>Error: </strong>Incorrect password!</p>
						</div>
				  		</div>';
        		}
        	?>
        		
        		<form action="login.php?page=login" method="post">
	        		<div class="password"><?php echo $lang["login"]["password"]; ?><input name="pass" type="password" /></div>
	        		<div class="loginb"><input type="submit" value="<?php echo $lang["login"]["login"]; ?>" class="loginButton" /></div>
        		</form>
        		
            </div>
        </div>
        
        <div class="footer">
        	<p>&copy; Oliver Woodings 2011</p>
        </div>
    
    </body>
</html>
