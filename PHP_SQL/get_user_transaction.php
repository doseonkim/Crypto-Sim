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
	
	$select_sql = "SELECT record FROM Transaction WHERE username = '$user_name'";
    #make a query object
    $transaction_query = $db->query($select_sql);
	
	$transactions      = $transaction_query->fetchAll(PDO::FETCH_ASSOC);
    #check to see if the db returned any values

	if ($transactions) {
		#start an array to hold the results
		$result = array("code"=>100, "size"=>count($transactions));
		$transaction_array = array();
		#iterate through the results

		for ($i = 0; $i < count($transactions); $i++) {
			$record = $transactions[$i]['record'];
				
			$transaction_array[$i] = 
			array("record"=>$record);
		}
		$result["transaction_data"] = $transaction_array;	
	} else {
		$result = array("code"=>200, "message"=>"No Transactions found.");
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