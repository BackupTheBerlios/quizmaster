<?
//include "logger.class.php";
function db_connect($query, $db=0) {
    //local
	$db_server = "127.0.0.1";
    $db_name = "quizmaster";
    $db_user = "quizmaster";
    $db_passwd = "quizpass";
    
    //backin.de
//    $db_server = "localhost";
//    $db_name = "usr_web217_1";
//    $db_user = "web217";
//    $db_passwd = "dgiidD";
    
    $db_link = mysql_connect($db_server,$db_user,$db_passwd) or die("<h3>Message from db_connect.php</h3><h1>ERROR while connecting to database server: " . mysql_error() . "<h1>");
    $db_select = mysql_select_db($db_name) or die("<h3>Message from db_connect.php</h3><h1>ERROR while selecting database: " . mysql_error() . "</h1>");
    if($db_query = mysql_query($query)){
    	return $db_query;
    }else{
    	        	 die("<h3>Message from db_connect.php</h3><h6>ERROR couldn't execute my query ($query): " . mysql_error() . "<br>".$_SERVER["PHP_SELF"]."</h6>");
//        	echo mysql_error();
    //	Logger::debug(mysql_error(), trace());
    }
}

function db_get($query, $db=0){
	return mysql_fetch_array(db_connect($query, $db));
}
?>
