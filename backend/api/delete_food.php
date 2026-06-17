<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$food_id = $data['food_id'] ?? '';
$hotel_id = $data['hotel_id'] ?? '';

if (empty($food_id) || empty($hotel_id)) {
    sendJsonResponse("error", "Missing required fields.");
}

$food_id = $conn->real_escape_string($food_id);
$hotel_id = $conn->real_escape_string($hotel_id);

// Check if it belongs to hotel
$check_sql = "SELECT image_url FROM food_items WHERE id = '$food_id' AND hotel_id = '$hotel_id'";
$check_result = $conn->query($check_sql);

if ($check_result && $check_result->num_rows > 0) {
    $row = $check_result->fetch_assoc();
    $image_url = $row['image_url'];

    // Delete from DB
    $sql = "DELETE FROM food_items WHERE id = '$food_id' AND hotel_id = '$hotel_id'";
    if ($conn->query($sql) === TRUE) {
        // Optionally delete the image file from server to save space
        if (!empty($image_url) && $image_url != 'NULL') {
            $file_path = '../' . trim($image_url, "'");
            if (file_exists($file_path)) {
                unlink($file_path);
            }
        }
        sendJsonResponse("success", "Food item deleted successfully.");
    } else {
        sendJsonResponse("error", "Error deleting food item: " . $conn->error);
    }
} else {
    sendJsonResponse("error", "Food item not found or unauthorized.");
}
?>
