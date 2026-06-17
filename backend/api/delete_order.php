<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
$order_id = $data['order_id'] ?? '';

if (empty($order_id)) {
    sendJsonResponse("error", "Missing order_id");
}

$order_id = $conn->real_escape_string($order_id);

$sql = "DELETE FROM orders WHERE id = '$order_id'";

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Order deleted successfully");
} else {
    sendJsonResponse("error", "Error deleting order: " . $conn->error);
}
?>
