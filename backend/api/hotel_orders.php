<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['hotel_id'])) {
    sendJsonResponse("error", "Missing hotel_id");
}

$hotel_id = $data['hotel_id'];
$status = isset($data['status']) ? $data['status'] : null;

$sql = "
    SELECT 
        o.id, o.total_amount, o.status, o.payment_method, o.payment_status, o.created_at, o.delivery_address,
        u.full_name as user_name, u.phone as user_phone
    FROM orders o
    JOIN users u ON o.user_id = u.id
    WHERE o.hotel_id = ?
";
if ($status) {
    $sql .= " AND o.status = ?";
}
$sql .= " ORDER BY o.created_at DESC";

$stmt = $conn->prepare($sql);
if ($status) {
    $stmt->bind_param("is", $hotel_id, $status);
} else {
    $stmt->bind_param("i", $hotel_id);
}

$stmt->execute();
$result = $stmt->get_result();

$orders = array();
while($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

sendJsonResponse("success", "Orders retrieved", $orders);
?>
