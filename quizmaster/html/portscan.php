<?
/* portscan.php
 *
 * Portscanning methods
 */ 

/* Checks if a service is running at a specific port
 * at the specified target host
 */
function scanport($target, $port)
{
	ini_set("error_reporting", "E_ALL & ~E_NOTICE & ~E_WARNING");
	fsockopen("localhost", 1099, &$errno, &$errstr);
	ini_set("error_reporting", "E_ALL & ~E_NOTICE");
	
	return $errno;
}

/* Checks if services are running in the given portrange
 * at the specified target host
 */
function scanportrange($target, $start, $end)
{
	$counter=0;
	
	for($i=$start; $i<=$end; $i++)
	{
		$errno = scanport($target, $i);
		
		if($errno==0)
		{
			$openports[counter]=$i;
			$counter++;
		}
	}
	
	return $openports;
}

/* Checks if services are running at the specified target host
 */
function scantarget($target)
{
	$openports = scanportrange($target, 0, 65535);
	return $openports;
}
?>