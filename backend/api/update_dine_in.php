<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
$hotel_id = $data['hotel_id'] ?? '';
$total_places = $data['total_places'] ?? '';
$in_use = $data['in_use'] ?? '';
$max_clear_time = $data['max_clear_time'] ?? '';

if (empty($hotel_id) || $total_places === '' || $in_use === '' || $max_clear_time === '') {
    sendJsonResponse("error", "Missing required fields");
}

$hotel_id = $conn->real_escape_string($hotel_id);
$total_places = (int)$total_places;
$in_use = (int)$in_use;
$remaining = $total_places - $in_use;
$max_clear_time = (int)$max_clear_time;

// Check if exists
$check_sql = "SELECT id FROM dine_in_capacity WHERE hotel_id = '$hotel_id'";
$check_res = $conn->query($check_sql);

if ($check_res && $check_res->num_rows > 0) {
    // Update
    $sql = "UPDATE dine_in_capacity SET total_places = $total_places, in_use = $in_use, remaining = $remaining, max_clear_time_mins = $max_clear_time WHERE hotel_id = '$hotel_id'";
} else {
    // Insert
    $sql = "INSERT INTO dine_in_capacity (hotel_id, total_places, in_use, remaining, max_clear_time_mins) VALUES ('$hotel_id', $total_places, $in_use, $remaining, $max_clear_time)";
}

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Dine-in capacity updated");
} else {
    sendJsonResponse("error", "Error updating dine-in capacity: " . $conn->error);
}
?>
