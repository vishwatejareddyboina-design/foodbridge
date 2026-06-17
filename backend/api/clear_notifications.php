<?php
include '../db_connect.php';

$sql = "DELETE FROM notifications";
if ($conn->query($sql)) {
    sendJsonResponse("success", "Notifications cleared successfully");
} else {
    sendJsonResponse("error", "Error clearing notifications: " . $conn->error);
}
?>
