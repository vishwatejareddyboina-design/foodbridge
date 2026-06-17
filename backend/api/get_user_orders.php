<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_GET;
}

$user_id = $data['user_id'] ?? '';

if (empty($user_id)) {
    sendJsonResponse("error", "Missing user_id");
}

$user_id = $conn->real_escape_string($user_id);

$sql = "
    SELECT 
        o.id as order_id,
        o.total_amount,
        o.status,
        o.created_at,
        o.delivery_address,
        o.payment_method,
        o.payment_status,
        h.hotel_name
    FROM 
        orders o
    JOIN 
        hotels h ON o.hotel_id = h.id
    WHERE 
        o.user_id = '$user_id'
    ORDER BY 
        o.created_at DESC
";

$result = $conn->query($sql);
$orders = [];

if ($result) {
    while($row = $result->fetch_assoc()) {
        $orders[] = $row;
    }
    sendJsonResponse("success", "Orders retrieved", $orders);
} else {
    sendJsonResponse("error", "Error fetching orders: " . $conn->error);
}
?>
