<?php
ini_set('display_errors', '1');

error_reporting(E_ALL);
// Connect to the Database

$dsn 	  = 'mysql:host=cssgate.insttech.washington.edu;dbname=doseon';
$username = 'doseon';
$password = 'Getex~';

if (!isset($_POST['user']) || empty($_POST['user'])) {
	$result = array(
            "code" => 100,
            "message" => "Username not defined."
        );
	echo json_encode($result);
	exit();
}
if (!isset($_POST['pin']) || empty($_POST['pin'])) {
	$result = array(
            "code" => 101,
            "message" => "pin not defined."
        );
	echo json_encode($result);
	exit();
} 
$userin = $_POST['user'];
$pin = $_POST['pin'];

try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "SELECT pin FROM registration WHERE username = '$userin'";
    #make a query object
    $user_query = $db->query($select_sql);
	
	$pins      = $user_query->fetchAll(PDO::FETCH_ASSOC);
    #check to see if the db returned any values

	if ($pins) {
                $correctpin = $pins[0]['pin'];
                if ($correctpin === $pin) {
						$remove_sql = "DELETE FROM registration WHERE username = '$userin'";
						$db->query($remove_sql);						
						
                        $result = array("code"=>300, "message"=>"correct pin");
                } else {
                        $result = array("code"=>201, "message"=>"incorrect pin.");
                }
        }else {
		$result = array("code"=>200, "message"=>"no pin found for that email.");
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