<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$status = isset($data['status']) ? $data['status'] : null;
$delivery_partner_id = isset($data['delivery_partner_id']) ? $data['delivery_partner_id'] : null;

$sql = "
    SELECT 
        d.id, d.hotel_id, d.package_description, d.quantity, d.expiration_time, d.status, d.created_at,
        h.hotel_name, h.address, h.phone
    FROM donations d
    JOIN hotels h ON d.hotel_id = h.id
    WHERE 1=1
";

if ($status) {
    $sql .= " AND d.status = ?";
}
if ($delivery_partner_id) {
    $sql .= " AND d.delivery_partner_id = ?";
}
$sql .= " ORDER BY d.created_at DESC";

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

$donations = array();
while($row = $result->fetch_assoc()) {
    $donations[] = $row;
}

sendJsonResponse("success", "Donations retrieved", $donations);
?>
