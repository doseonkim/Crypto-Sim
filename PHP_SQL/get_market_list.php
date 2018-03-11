<?php
ini_set('display_errors', '1');

error_reporting(E_ALL);
// Connect to the Database

$dsn 	  = 'mysql:host=cssgate.insttech.washington.edu;dbname=doseon';
$username = 'doseon';
$password = 'Getex~';

try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "SELECT * FROM Market";
    #make a query object
    $user_query = $db->query($select_sql);
	
	$coins      = $user_query->fetchAll(PDO::FETCH_ASSOC);
    #check to see if the db returned any values

	if ($coins) {
		#start an array to hold the results
		$result = array("code"=>100, "size"=>count($coins));
		$coin_array = array();
		#iterate through the results
		for ($i = 0; $i < count($coins); $i++) {
			$market_name = $coins[$i]['market_name'];
			$base_coin = $coins[$i]['base_coin'];
			$alt_coin = $coins[$i]['alt_coin'];
			$price = $coins[$i]['price'];
			$tradeable = $coins[$i]['tradeable'];
		
			$coin_array[$i] = 
			array("market_name"=>$market_name, 
			"base_coin"=>$base_coin,
			"alt_coin"=>$alt_coin,
			"price"=>$price,
			"tradeable"=>$tradeable);
		}
		$result["coins_data"] = $coin_array;
	} else {
		$result = array("code"=>200, "message"=>"No coins found.");
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