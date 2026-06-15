<?php
include '../db_connect.php';

$sql = "SELECT d.*, h.hotel_name, h.address as hotel_address 
        FROM dine_in_capacity d 
        JOIN hotels h ON d.hotel_id = h.id 
        ORDER BY d.updated_at DESC";

$result = $conn->query($sql);
$data = [];

if ($result) {
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
    sendJsonResponse("success", "Dine-in capacities retrieved", $data);
} else {
    sendJsonResponse("error", "Error fetching data: " . $conn->error);
}
?>
