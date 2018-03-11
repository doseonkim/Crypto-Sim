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

if (!isset($_POST['market']) || empty($_POST['market'])) {
	$result = array(
            "code" => 104,
            "message" => "market not defined."
        );
	echo json_encode($result);
	exit();
} 

if (!isset($_POST['type']) || empty($_POST['type'])) {
	$result = array(
            "code" => 105,
            "message" => "type not defined."
        );
	echo json_encode($result);
	exit();
} 

if (!isset($_POST['quantity']) || empty($_POST['quantity'])) {
	$result = array(
            "code" => 106,
            "message" => "quantity not defined."
        );
	echo json_encode($result);
	exit();
} 


$email = $_POST['name'];
$market_name = $_POST['market'];
$quantity = $_POST['quantity'];
$type = $_POST['type'];

try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
	$select_sql = "SELECT base_coin, alt_coin FROM Market WHERE market_name = '$market_name'";
	$user_query = $db->query($select_sql);
	$coins = $user_query->fetchAll(PDO::FETCH_ASSOC);
	$base_coin;
	$alt_coin;
	if ($coins) {
		$base_coin = $coins[0]['base_coin'];
		$alt_coin = $coins[0]['alt_coin'];
	} else {
		exit();
	}
		
	$select_sql = "SELECT $base_coin, $alt_coin, price FROM Wallet JOIN Market ON Market.market_name = '$market_name' WHERE username = '$email'";
	$user_query = $db->query($select_sql);
	$values = $user_query->fetchAll(PDO::FETCH_ASSOC);
	$price = $values[0]['price'];
	$total_price = ($price * $quantity);
	$success = 0;
	$record = "";
	if (strcasecmp($type, "buy") == 0) {	
		if ($values) {
			$user_amount = $values[0][$base_coin];
			if ($user_amount >= $total_price) {
				$select_sql = "UPDATE Wallet SET $base_coin = $base_coin - $total_price, $alt_coin = $alt_coin + $quantity WHERE username = '$email'";
				if ($user_query = $db->query($select_sql)) {
					//success
					$success = 1;
					$record = "Purchased $quantity $alt_coin for $total_price $base_coin.";
					$result = array(
						"code" => 300,
						"message" => "Successfully Purchased $quantity $alt_coin for $total_price $base_coin."
					);
				} else {
					$result = array(
						"code" => 200,
						"message" => "Failed purchase, Query error, please try again later."
					);
				}
			} else {
				$result = array(
						"code" => 500,
						"message" => "Insufficient funds to make the purchase. You have $user_amount $base_coin when you need $total_price $base_coin."
					);
			}
		}
	} else {
		if ($values) {
			$user_amount = $values[0][$alt_coin];
			
			if ($user_amount >= $quantity) {
				$select_sql = "UPDATE Wallet SET $alt_coin = $alt_coin - $quantity, 
				$base_coin = $base_coin + $total_price WHERE username = '$email'";
				if ($user_query = $db->query($select_sql)) {
					//success
					$success = 1;
					$record = "Sold $quantity $alt_coin for $total_price $base_coin.";
					$result = array(
						"code" => 300,
						"message" => "Successfully Sold $quantity $alt_coin for $total_price $base_coin."
					);
				} else {
					$result = array(
						"code" => 200,
						"message" => "Failed selling, Query error, please try again later."
					);
				}
			} else {
				$result = array(
						"code" => 500,
						"message" => "Insufficient funds to sell. You have $user_amount $alt_coin when you need $quantity $alt_coin."
					);
			}
		}
	}
	if ($success == 1) {
		$url = 'http://cssgate.insttech.washington.edu/~doseon/CryptoSim/add_transaction.php';
		$data = array('name' => $email, 'record' => $record);

		$options = array(
			'http' => array(
				'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
				'method'  => 'POST',
				'content' => http_build_query($data)
			)
		);
		$context  = stream_context_create($options);
		$result_post = file_get_contents($url, false, $context);
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