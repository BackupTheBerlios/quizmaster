
<?
include("db_connect.php");
echo "<span class='subheadl'>Highscore</span><br>";
echo "<table cellpadding='0' class='body'>";
		
$sql = db_connect("select * from highscore order by score desc");

while($row = mysql_fetch_object($sql)){
	echo "<tr><td>$row->nick</td><td>$row->score</td></tr>";
}

echo "</table>";
?>
</body>
</html>