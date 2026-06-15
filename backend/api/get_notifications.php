<?php
include '../db_connect.php';

$sql = "SELECT n.*, h.hotel_name FROM notifications n JOIN hotels h ON n.hotel_id = h.id ORDER BY n.created_at DESC LIMIT 50";
$result = $conn->query($sql);

$notifications = [];
if ($result) {
    while($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }
    sendJsonResponse("success", "Notifications retrieved", $notifications);
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
