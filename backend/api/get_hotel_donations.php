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

$sql = "SELECT id, package_description, quantity, expiration_time, status FROM donations WHERE hotel_id = '$hotel_id' ORDER BY created_at DESC";

$result = $conn->query($sql);
$donations = [];

if ($result) {
    while($row = $result->fetch_assoc()) {
        $donations[] = $row;
    }
    sendJsonResponse("success", "Donations retrieved", $donations);
} else {
    sendJsonResponse("error", "Error fetching donations: " . $conn->error);
}
?>
