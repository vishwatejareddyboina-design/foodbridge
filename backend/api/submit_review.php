<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['order_id']) || !isset($data['user_id']) || !isset($data['hotel_id']) || !isset($data['rating'])) {
    sendJsonResponse("error", "Missing required fields");
}

$order_id = $data['order_id'];
$user_id = $data['user_id'];
$hotel_id = $data['hotel_id'];
$rating = $data['rating'];
$comment = isset($data['comment']) ? $data['comment'] : '';
$is_hygienic = isset($data['is_hygienic']) ? (int)$data['is_hygienic'] : null;

// Check if review already exists for this order
$check_sql = "SELECT id FROM reviews WHERE order_id = ?";
$stmt_check = $conn->prepare($check_sql);
$stmt_check->bind_param("i", $order_id);
$stmt_check->execute();
if($stmt_check->get_result()->num_rows > 0) {
    sendJsonResponse("error", "Review already submitted for this order");
}

if ($is_hygienic !== null) {
    $sql = "INSERT INTO reviews (order_id, user_id, hotel_id, rating, comment, is_hygienic) VALUES (?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iiidsi", $order_id, $user_id, $hotel_id, $rating, $comment, $is_hygienic);
} else {
    $sql = "INSERT INTO reviews (order_id, user_id, hotel_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iiids", $order_id, $user_id, $hotel_id, $rating, $comment);
}

if($stmt->execute()) {
    sendJsonResponse("success", "Review submitted successfully");
} else {
    sendJsonResponse("error", "Failed to submit review");
}
?>
