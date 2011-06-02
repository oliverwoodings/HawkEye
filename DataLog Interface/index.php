<?php
	ini_set('display_errors', 1); error_reporting(E_ALL);
	
	//Connect to MySQL
	include("config.php");
	
	$itemList = file_get_contents("list.txt");
	
	//Get worlds and make list of options
	$sql = "SELECT DISTINCT world FROM `" . $config["dbTable"] . "`";
	$res = mysql_query($sql);
	$worlds = "";
	if ($res)
		while ($row = mysql_fetch_object($res))
			$worlds .= '<option value="' . $row->world . '">' . $row->world . '</option>';
	
?>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>DataLog Browser</title>
        <link href="styles.css" type="text/css" rel="stylesheet" />
        
        <script type="text/javascript">
		
			function searchdb() {
				
				//Get state of checkboxes
				actions = "actions=";
				for (i = 0; i < 13; i++) {
					if (document.searchDB.check[i].checked)
						actions = actions + document.searchDB.check[i].value + "@";
				}
				
				//Get info
				password = document.searchDB.password.value;
				name     = document.searchDB.username.value;
				x        = document.searchDB.x.value;
				z        = document.searchDB.z.value;
				range    = document.searchDB.range.value;
				filter   = document.searchDB.filter.value;
				itemID   = document.searchDB.itemSelect.options[document.searchDB.itemSelect.selectedIndex].value;
				world    = document.searchDB.worldSelect.options[document.searchDB.worldSelect.selectedIndex].value;
				
				//Compile into POST data
				postData = "password=" + password + "&name=" + name + "&x=" + x + "&z=" + z + "&range=" + range + "&filter=" + filter + "&item=" + itemID + "&actions=" + actions + "&world=" + world;
				
				//Set up AJAX
				if (window.XMLHttpRequest)
					xmlhttp = new XMLHttpRequest(); //Good Browsers
				else
					xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); //Shit like IE5 and 6
					
				//Call function
				xmlhttp.onreadystatechange = function() {
					if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
						document.getElementById("searchResults").innerHTML = xmlhttp.responseText;
					}
				};
				 
				//Open page and send POST data
				xmlhttp.open("POST" ,"searchDB.php", true);
				xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
				xmlhttp.send(postData);
				document.getElementById("searchResults").innerHTML = "Searching...";
					
			}
		
		</script>
        
    </head>
    
    <body>
    
    	<div class="page">
        	<div class="leftBorder">
            </div>
            <div class="mainFrame">
            	<div class="header">
                	DataLog Browser
                </div>
                <hr />
                
                <div class="content">
                
                    <form name="searchDB" class="searchDB">
                    
                   		<div <?php if ($config["password"] == "") echo 'style="display: none;"'; ?>><input type="password" name="password" class="passwordInput" /> Password <br /></div>
                    
                    	<select name="worldSelect" class="itemSelect">
                        	<?php echo $worlds; ?>
                        </select> World<br />
                        
                    	<input type="text" name="username" class="nameInput" /> Username (optional) <br />
                        <input type="text" name="x" class="coordInput" /> x (optional)<br />
                    	<input type="text" name="z" class="coordInput" /> z (optional)<br />
                        <input type="text" name="range" class="coordInput" value="20" /> Range (optional)<br />
                        <input type="text" name="filter" class="coordInput" /> Data column filter (optional)<br />
                        
                        <select name="itemSelect" class="itemSelect">
                        	<?php echo $itemList; ?>
                        </select> Item (optional)<br />
                        
                        <div class="checkboxContainer">
                            <div class="checkboxDiv">
                                <input type="checkbox" name="check" value="0" checked> Block Broken<br />
                                <input type="checkbox" name="check" value="1"> Block Placed<br />
                                <input type="checkbox" name="check" value="2"> Sign Placed<br />
                                <input type="checkbox" name="check" value="3"> Chat<br />
                            </div>
                            <div class="checkboxDiv">
                                <input type="checkbox" name="check" value="4"> Command<br />
                                <input type="checkbox" name="check" value="5"> Login<br />
                                <input type="checkbox" name="check" value="6"> Disconnect<br />
                                <input type="checkbox" name="check" value="7"> Teleport<br />
                            </div>
                            <div class="checkboxDiv">
                                <input type="checkbox" name="check" value="8"> Lava Bucket<br />
                                <input type="checkbox" name="check" value="9"> Water Bucket<br />
                                <input type="checkbox" name="check" value="10"> Open Chest<br />
                                <input type="checkbox" name="check" value="11"> Door Interact<br />
                                <input type="checkbox" name="check" value="12"> PVP Death<br />
                            </div>
                        </div>
                        
                        <input type="button" value="Search" onClick="searchdb();" /> 
                        
                    </form>
                    
                    <hr />
                    
                    <div class="searchResults" id="searchResults">                    
                    </div>
                
                </div>
            </div>
            <div class="rightBorder">
            </div>
            
        </div>
    	
    </body>
    
</html>
