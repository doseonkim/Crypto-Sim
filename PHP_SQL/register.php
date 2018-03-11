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
if (!isset($_POST['pass']) || empty($_POST['pass'])) {
	$result = array(
            "code" => 101,
            "message" => "password not defined."
        );
	echo json_encode($result);
	exit();
} 

if (!isset($_POST['name']) || empty($_POST['name'])) {
	$result = array(
            "code" => 102,
            "message" => "name not defined."
        );
	echo json_encode($result);
	exit();
} 

$userin = $_POST['user'];
$passin = $_POST['pass'];
$namein = $_POST['name'];


try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "INSERT INTO User_Sim (username, pwd, name) VALUES ('$userin','$passin', '$namein');";
    #make a query object
    if ($user_query = $db->query($select_sql)) {
		//success
		$result = array(
            "code" => 300,
            "message" => "Successfully registered."
        );
						$url = 'http://cssgate.insttech.washington.edu/~doseon/CryptoSim/add_transaction.php';
						$data = array('name' => $userin, 'record' => 'You have been granted 1 Bitcoin.');

						$options = array(
							'http' => array(
								'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
								'method'  => 'POST',
								'content' => http_build_query($data)
							)
						);
						$context  = stream_context_create($options);
						$result_post = file_get_contents($url, false, $context);
						
						$url = 'http://cssgate.insttech.washington.edu/~doseon/CryptoSim/add_wallet.php';
						$data = array('name' => $userin);

						$options = array(
							'http' => array(
								'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
								'method'  => 'POST',
								'content' => http_build_query($data)
							)
						);
						$context  = stream_context_create($options);
						$result_post = file_get_contents($url, false, $context);
	} else {
		//failed
		$result = array(
            "code" => 200,
            "message" => "Username already exists."
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