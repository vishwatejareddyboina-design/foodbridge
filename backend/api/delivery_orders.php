<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$status = isset($data['status']) ? $data['status'] : null;
$delivery_partner_id = isset($data['delivery_partner_id']) ? $data['delivery_partner_id'] : null;

$sql = "
    SELECT 
        o.id, o.hotel_id, o.user_id, o.total_amount, o.status, o.payment_method, o.payment_status, o.created_at, o.delivery_address,
        u.full_name as user_name, u.phone as user_phone,
        h.hotel_name, h.address as hotel_address, h.phone as hotel_phone
    FROM orders o
    JOIN users u ON o.user_id = u.id
    JOIN hotels h ON o.hotel_id = h.id
    WHERE 1=1
";

if ($status) {
    $sql .= " AND o.status = ?";
}
if ($delivery_partner_id) {
    $sql .= " AND o.delivery_partner_id = ?";
}
$sql .= " ORDER BY o.created_at DESC";

$stmt = $conn->prepare($sql);

if ($status && $delivery_partner_id) {
    $stmt->bind_param("si", $status, $delivery_partner_id);
} else if ($status) {
    $stmt->bind_param("s", $status);
} else if ($delivery_partner_id) {
    $stmt->bind_param("i", $delivery_partner_id);
}

$stmt->execute();
$result = $stmt->get_result();

$orders = array();
while($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

sendJsonResponse("success", "Delivery orders retrieved", $orders);
?>
