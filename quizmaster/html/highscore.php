<?
include('tools.php');
include("db_connect.php");
echo "<span class='subheadl'>Highscore</span><br>";
echo "<table cellpadding='0' class='body'>";
		
$sql = db_connect("select * from highscore order by score desc");

while($row = mysql_fetch_object($sql)){
	echo "<tr><td width=80>$row->nick</td><td width=80>Score: $row->score</td><td width=80>".formatDate($row->date, false)."</td></tr>";
}

echo "</table>";
?>
</body>
</html>