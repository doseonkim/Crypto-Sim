<?php
ini_set('display_errors', '1');

error_reporting(E_ALL);
// Connect to the Database

$dsn 	  = 'mysql:host=cssgate.insttech.washington.edu;dbname=doseon';
$username = 'doseon';
$password = 'Getex~';

if (!isset($_GET['name']) || empty($_GET['name'])) {
	$result = array(
            "code" => 100,
            "message" => "name not defined."
        );
	echo json_encode($result);
	exit();
} 
$user_name = $_GET['name'];

try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
	
	$q = $db->query("DESCRIBE Wallet"); 
	$table_fields = $q->fetchAll(PDO::FETCH_COLUMN);
	
	$select_sql = "SELECT * FROM Wallet WHERE username = '$user_name'";
    #make a query object
    $wallet_query = $db->query($select_sql);
	
	$wallet      = $wallet_query->fetchAll(PDO::FETCH_ASSOC);
    #check to see if the db returned any values

	if ($wallet) {
		#start an array to hold the results
		$result = array("code"=>100, "size"=>count($table_fields) - 1);
		$wallet_array = array();
		#iterate through the results
		if ($table_fields) {
			for ($i = 1; $i < count($table_fields); $i++) {
				$coin_name = $table_fields[$i];
				$coin_amount = $wallet[0][$coin_name];
				
				$wallet_array[$i-1] = 
				array("coin_name"=>$coin_name, 
				"coin_amount"=>$coin_amount);
			}
			$result["wallet_data"] = $wallet_array;
		}	
	} else {
		$result = array("code"=>200, "message"=>"No wallet found.");
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