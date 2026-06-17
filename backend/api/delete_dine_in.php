<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
$hotel_id = $data['hotel_id'] ?? '';

if (empty($hotel_id)) {
    sendJsonResponse("error", "Missing hotel_id");
}

$hotel_id = $conn->real_escape_string($hotel_id);

$sql = "DELETE FROM dine_in_capacity WHERE hotel_id = '$hotel_id'";

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Dine-in tracking stopped");
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
