<?
/*tools.php
 *
 * Some tools needed for the php-pages
 */

/*
 *Format a mysql style date to a german style date
 */
function formatDate($date, $withTime)
{
	if($withTime==false)
	{
		ereg ("([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})", $date, $regs);
		return $regs[3].".".$regs[2].".".$regs[1];
	}
	else
	{
		ereg("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})", $date, $regs);
		return $regs[3].".".$regs[2].".".$regs[1]." um ".$regs[4].":".$regs[5]." Uhr";
	}
	return "Fehler";
}
?>