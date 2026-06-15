<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$hotel_id = $data['hotel_id'] ?? '';
$hotel_name = $data['hotel_name'] ?? '';

if (empty($hotel_id) || empty($hotel_name)) {
    sendJsonResponse("error", "Missing required fields.");
}

$hotel_id = $conn->real_escape_string($hotel_id);
$hotel_name = $conn->real_escape_string($hotel_name);

$msg = "Great news! The rush is now reduced at $hotel_name. Grab your food now!";
$sql = "INSERT INTO notifications (hotel_id, message, type) VALUES ('$hotel_id', '$msg', 'rush')";

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Rush notification sent successfully.");
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
