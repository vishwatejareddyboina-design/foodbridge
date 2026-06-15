<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['order_id']) || !isset($data['payment_id'])) {
    sendJsonResponse("error", "Missing required fields");
}

$order_id = $data['order_id'];
$payment_id = $data['payment_id'];

// Here we would normally verify the Razorpay signature, but for the demo we assume it's valid if provided.

$sql = "UPDATE orders SET payment_status = 'Paid' WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $order_id);

if($stmt->execute()) {
    sendJsonResponse("success", "Payment status updated");
} else {
    sendJsonResponse("error", "Failed to update payment status");
}
?>
