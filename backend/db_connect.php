<?php
// db_connect.php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

$servername = "localhost";
$username = "root";      // Default XAMPP username
$password = "";          // Default XAMPP password is empty
$dbname = "food_bridge";

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]));
}

// Select Database
$db_selected = $conn->select_db($dbname);

if (!$db_selected) {
    // If database doesn't exist yet, we handle gracefully.
    // In production, you would return an error.
    die(json_encode(["status" => "error", "message" => "Database 'food_bridge' not found. Please run the SQL script in phpMyAdmin first."]));
}

// Optional helper function to return JSON response
function sendJsonResponse($status, $message, $data = null) {
    $response = [
        "status" => $status,
        "message" => $message
    ];
    if ($data !== null) {
        $response["data"] = $data;
    }
    echo json_encode($response);
    exit();
}
?>
