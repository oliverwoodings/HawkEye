<?php
	define('IN_PHPBB', true);
	$phpbb_root_path = (defined('PHPBB_ROOT_PATH')) ? PHPBB_ROOT_PATH : $hawkConfig["forumDir"];
	$phpEx = substr(strrchr(__FILE__, '.'), 1);
	include($phpbb_root_path . 'common.' . $phpEx);
	$user->session_begin();
	$auth->acl($user->data);
	if($auth->acl_get('a_'))
	{
		$isAuth = true;
	}
	else
	{
		$isAuth = false;
	}
?>