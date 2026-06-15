<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['user_id']) || !isset($data['hotel_id']) || !isset($data['total_amount']) || !isset($data['items'])) {
    sendJsonResponse("error", "Missing required fields");
}

$user_id = $data['user_id'];
$hotel_id = $data['hotel_id'];
$total_amount = $data['total_amount'];
$items = $data['items']; // Array of {food_item_id, quantity, price}
$payment_method = isset($data['payment_method']) ? $data['payment_method'] : 'Offline';
$delivery_address = isset($data['delivery_address']) ? $data['delivery_address'] : '';

$conn->begin_transaction();

try {
    $sql = "INSERT INTO orders (user_id, hotel_id, total_amount, payment_method, delivery_address, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iidss", $user_id, $hotel_id, $total_amount, $payment_method, $delivery_address);
    $stmt->execute();
    
    $order_id = $conn->insert_id;
    
    $sql_item = "INSERT INTO order_items (order_id, food_item_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
    $stmt_item = $conn->prepare($sql_item);
    
    foreach($items as $item) {
        $stmt_item->bind_param("iiid", $order_id, $item['food_item_id'], $item['quantity'], $item['price']);
        $stmt_item->execute();
    }
    
    $conn->commit();
    sendJsonResponse("success", "Order placed successfully", array("order_id" => $order_id));
} catch(Exception $e) {
    $conn->rollback();
    sendJsonResponse("error", "Failed to place order: " . $e->getMessage());
}
?>
