<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$food_id = $data['food_id'] ?? '';
$discounted_price = $data['discounted_price'] ?? '';
$hotel_id = $data['hotel_id'] ?? '';
$item_name = $data['item_name'] ?? '';

if (empty($food_id) || empty($discounted_price) || empty($hotel_id)) {
    sendJsonResponse("error", "Missing required fields.");
}

$food_id = $conn->real_escape_string($food_id);
$discounted_price = $conn->real_escape_string($discounted_price);
$hotel_id = $conn->real_escape_string($hotel_id);
$item_name = $conn->real_escape_string($item_name);

$sql = "UPDATE food_items SET discounted_price = '$discounted_price' WHERE id = '$food_id' AND hotel_id = '$hotel_id'";

if ($conn->query($sql) === TRUE) {
    // Notify users
    $msg = "Discount! $item_name is now available at $discounted_price.";
    $sql_notify = "INSERT INTO notifications (hotel_id, message, type) VALUES ('$hotel_id', '$msg', 'discount')";
    $conn->query($sql_notify);

    sendJsonResponse("success", "Discount applied and users notified.");
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
