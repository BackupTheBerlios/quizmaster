<html>
<body>
$username = <?=$username?>
<APPLET CODE="client.ClientApplet.class" WIDTH=800 HEIGHT=600>
	<param name="nickname" value="<?=$username?>">
	<param name="host" value="localhost">
</APPLET>
</body>
</html>