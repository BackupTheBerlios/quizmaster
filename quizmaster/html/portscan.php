<?
/* portscan.php
 * 
 * Checks if a service is running at a specific port
 *
 */ 

function portscan($target, $port)
{
	ini_set("error_reporting", "E_ALL & ~E_NOTICE & ~E_WARNING");
	fsockopen("localhost", 1099, &$errno, &$errstr);
	ini_set("error_reporting", "E_ALL & ~E_NOTICE");
	
	return $errno;
}
?>