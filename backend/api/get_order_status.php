<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['order_id'])) {
    sendJsonResponse("error", "Missing order_id");
}

$order_id = $data['order_id'];
$sql = "SELECT status, payment_status, hotel_id FROM orders WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $order_id);
$stmt->execute();
$result = $stmt->get_result();

if($row = $result->fetch_assoc()) {
    sendJsonResponse("success", "Order status retrieved", $row);
} else {
    sendJsonResponse("error", "Order not found");
}
?>
