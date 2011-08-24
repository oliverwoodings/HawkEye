<?php
	require($hawkConfig["forumDir"] . "/SSI.php");
	if ($context['allow_admin'])
	{
		$isAuth = true;
	}
	else
	{
		$isAuth = false;
	}
?>