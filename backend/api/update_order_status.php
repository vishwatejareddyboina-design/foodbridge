<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['order_id']) || !isset($data['status'])) {
    sendJsonResponse("error", "Missing required fields");
}

$order_id = $data['order_id'];
$status = $data['status'];
$delivery_partner_id = isset($data['delivery_partner_id']) ? $data['delivery_partner_id'] : null;

$sql = "UPDATE orders SET status = ?";
$types = "s";
$params = [$status];

if ($delivery_partner_id !== null) {
    $sql .= ", delivery_partner_id = ?";
    $types .= "i";
    $params[] = $delivery_partner_id;
}

$sql .= " WHERE id = ?";
$types .= "i";
$params[] = $order_id;

$stmt = $conn->prepare($sql);
$stmt->bind_param($types, ...$params);

if($stmt->execute()) {
    sendJsonResponse("success", "Order status updated");
} else {
    sendJsonResponse("error", "Failed to update order status");
}
?>
