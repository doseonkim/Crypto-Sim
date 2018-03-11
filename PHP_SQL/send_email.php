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

if (!isset($_POST['email']) || empty($_POST['email'])) {
	$result = array(
            "code" => 102,
            "message" => "email not defined."
        );
	echo json_encode($result);
	exit();
} 

	$pin = sprintf("%06d", mt_rand(1, 999999));
	$name = $_POST['name'];
	$to = $_POST['email'];

	$subject = "Farm Fresh email verification!";
	$message = "Hello $name,\n\nYour required pin code is : $pin";
	$from = "no-reply-CryptoSim@uw.edu";
	$headers = "From: $from";
	
	try {
    #make a new DB object to interact with
    $db = new PDO($dsn, $username, $password);
    #build a SQL statement to query the DB
    
    $select_sql = "INSERT INTO registration VALUES ('$to','$pin');";
    #make a query object
    if ($db->query($select_sql)) {
		//success
		mail($to,$subject,$message,$headers);
		$result = array(
            "code" => 300,
            "message" => $pin
        );
	} else {
		//failed
		$update_sql = "UPDATE registration SET pin = '$pin' WHERE username = '$to';";
		if ($db->query($update_sql)) {
			mail($to,$subject,$message,$headers);
			$result = array(
				"code" => 300,
				"message" => $pin
			);
		} else {
			$result = array(
				"code" => 201,
				"message" => "Failed to update pin."
			);
		}
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