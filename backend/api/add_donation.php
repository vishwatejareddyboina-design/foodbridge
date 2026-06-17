<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['hotel_id']) || !isset($data['package_description']) || !isset($data['quantity']) || !isset($data['expiration_time'])) {
    sendJsonResponse("error", "Missing required fields");
}

$hotel_id = $data['hotel_id'];
$desc = $data['package_description'];
$quantity = $data['quantity'];
$exp_time = $data['expiration_time']; // format YYYY-MM-DD HH:MM:SS

$sql = "INSERT INTO donations (hotel_id, package_description, quantity, expiration_time, status) VALUES (?, ?, ?, ?, 'Pending')";
$stmt = $conn->prepare($sql);
$stmt->bind_param("isis", $hotel_id, $desc, $quantity, $exp_time);

if($stmt->execute()) {
    sendJsonResponse("success", "Donation added successfully");
} else {
    sendJsonResponse("error", "Failed to add donation");
}
?>
