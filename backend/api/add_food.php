<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$hotel_id = $data['hotel_id'] ?? '';
$item_name = $data['item_name'] ?? '';
$price = $data['price'] ?? '';
$image_base64 = $data['image_base64'] ?? '';

if (empty($hotel_id) || empty($item_name) || empty($price)) {
    sendJsonResponse("error", "Missing required fields.");
}

$hotel_id = $conn->real_escape_string($hotel_id);
$item_name = $conn->real_escape_string($item_name);
$price = $conn->real_escape_string($price);

$image_url = 'NULL';

if (!empty($image_base64)) {
    // Determine mime type and save
    $upload_dir = '../uploads/food/';
    if (!file_exists($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }
    
    $file_name = uniqid('food_') . '.jpg';
    $file_path = $upload_dir . $file_name;
    
    // Convert base64 back to image
    $img_data = base64_decode($image_base64);
    if (file_put_contents($file_path, $img_data)) {
        // Build full URL (assumes standard structure like http://10.0.2.2/foodbridgebackend/)
        // We will save relative path or full URL. Let's save a relative path that we can prepend base URL to later.
        $db_path = 'uploads/food/' . $file_name;
        $image_url = "'" . $conn->real_escape_string($db_path) . "'";
    }
}

$sql = "INSERT INTO food_items (hotel_id, item_name, original_price, discounted_price, image_url) VALUES ('$hotel_id', '$item_name', '$price', '$price', $image_url)";

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Food added successfully.");
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
