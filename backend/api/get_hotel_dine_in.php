<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_GET;
}

$hotel_id = $data['hotel_id'] ?? '';

if (empty($hotel_id)) {
    sendJsonResponse("error", "Missing hotel_id");
}

$hotel_id = $conn->real_escape_string($hotel_id);
$sql = "SELECT * FROM dine_in_capacity WHERE hotel_id = '$hotel_id'";
$result = $conn->query($sql);

if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    sendJsonResponse("success", "Dine-in capacity retrieved", $row);
} else {
    sendJsonResponse("error", "No dine-in data found");
}
?>
