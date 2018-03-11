<?php
ini_set('display_errors', '1');

error_reporting(E_ALL);
// Connect to the Database

$dsn 	  = 'mysql:host=cssgate.insttech.washington.edu;dbname=doseon';
$username = 'doseon';
$password = 'Getex~';

if (!isset($_POST['name']) || empty($_POST['name'])) {
	$result = array(
            "code" => 101,
            "message" => "name not defined."
        );
	echo json_encode($result);
	exit();
} 

if (!isset($_POST['base_coin']) || empty($_POST['base_coin'])) {
	$result = array(
            "code" => 102,
            "message" => "base_coin not defined."
        );
	echo json_encode($result);
	exit();
} 

if (!isset($_POST['alt_coin']) || empty($_POST['alt_coin'])) {
	$result = array(
            "code" => 103,
            "message" => "alt_coin not defined."
        );
	echo json_encode($result);
	exit();
} 

$market_name = $_POST['name'];
$base_coin = $_POST['base_coin'];
$alt_coin = $_POST['alt_coin'];

try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "INSERT INTO Market (market_name, base_coin, alt_coin) VALUES ('$market_name', '$base_coin', '$alt_coin');";
	$select_sql.= "ALTER TABLE Wallet ADD COLUMN IF NOT EXISTS $alt_coin decimal(25, 10) NOT NULL DEFAULT 0;";
    #make a query object
    if ($user_query = $db->query($select_sql)) {
		//success
		$result = array(
            "code" => 300,
            "message" => "Successfully Added Market."
        );
	} else {
		//failed
		$result = array(
            "code" => 200,
            "message" => "Failed to Add Market."
        );
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