<?php
ini_set('display_errors', '1');

error_reporting(E_ALL);
// Connect to the Database

$dsn 	  = 'mysql:host=cssgate.insttech.washington.edu;dbname=doseon';
$username = 'doseon';
$password = 'Getex~';

if (!isset($_GET['user']) || empty($_GET['user'])) {
	$result = array(
            "code" => 100,
            "message" => "Username not defined."
        );
	echo json_encode($result);
	exit();
}
 
$userin = $_GET['user'];


try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "SELECT username FROM User_Sim WHERE username = '$userin'";
    #make a query object
    $user_query = $db->query($select_sql);
	
	$pin      = $user_query->fetchAll(PDO::FETCH_ASSOC);
    #check to see if the db returned any values

	if ($pin) {
		$result = array("code"=>200, "message"=>"exists");
	} else {
		$result = array("code"=>300, "message"=>"good");
	}
		
    echo json_encode($result);
    $db = null;
}
catch (PDOException $e) {
    $error_message = $e->getMessage();
    $result        = array(
        "code" => 400,
        "message" => "There was an error connecting to
the database: $error_message"
    );
    echo json_encode($result);
    exit();
}
?>