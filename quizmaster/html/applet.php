<?php
	$username=$HTTP_POST_VARS['username'];
?>

<html>
<body>
<APPLET CODEBASE="http://192.168.2.10/quizmaster/" CODE="client.ClientApplet.class" WIDTH=800 HEIGHT=600>
	<param name="nickname" value="<?=$username?>">
</APPLET>
</body>
</html>