<?
include("portscan.php");
$val = scanport("localhost", 1099);
?>

<html>
<head><title>Quizmaster</title></head>
<link rel="stylesheet" href="style.css">
<?
if($val==0)
{
echo "<form action=\"applet.php\" method=\"POST\">";
echo "Username<br>";
echo "<input name='username'>";
echo "<input type='submit' value='Start'>";
echo "</form>";
}
else
{
	echo "Can't connect...<br>";
	echo "QuizMaster server isn't running!<br>";
}
?>
<br>
<?
		include("highscore.php");
?>
</body>
</html>
