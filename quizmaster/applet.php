<?php
	$username=$HTTP_POST_VARS['username'];
?>

<html>
<body>
<APPLET CODEBASE="http://localhost/quizmaster/" CODE="client.ClientApplet.class" WIDTH=800 HEIGHT=600>
	<param name="nickname" value=<?php echo "\"".$username."\""; ?>">
</APPLET>
</body>
</html>