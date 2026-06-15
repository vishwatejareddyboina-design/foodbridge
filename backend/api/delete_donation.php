<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
$donation_id = $data['donation_id'] ?? '';

if (empty($donation_id)) {
    sendJsonResponse("error", "Missing donation_id");
}

$donation_id = $conn->real_escape_string($donation_id);

$sql = "DELETE FROM donations WHERE id = '$donation_id'";

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Donation deleted successfully");
} else {
    sendJsonResponse("error", "Error deleting donation: " . $conn->error);
}
?>
