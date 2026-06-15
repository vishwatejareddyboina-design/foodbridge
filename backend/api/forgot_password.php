<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$role = $data['role'] ?? '';
$email = $conn->real_escape_string($data['email'] ?? '');
$new_password = $data['new_password'] ?? '';

if (empty($role) || empty($email) || empty($new_password)) {
    sendJsonResponse("error", "Role, Email, and New Password are required.");
}

$table = '';
if ($role === 'user') { $table = 'users'; }
elseif ($role === 'hotel') { $table = 'hotels'; }
elseif ($role === 'delivery') { $table = 'delivery_partners'; }
else { sendJsonResponse("error", "Invalid role."); }

// Check if user exists
$check_sql = "SELECT id FROM $table WHERE email = '$email'";
$result = $conn->query($check_sql);

if ($result->num_rows > 0) {
    $hashed_password = password_hash($new_password, PASSWORD_DEFAULT);
    $update_sql = "UPDATE $table SET password = '$hashed_password' WHERE email = '$email'";
    
    if ($conn->query($update_sql) === TRUE) {
        sendJsonResponse("success", "Password updated successfully.");
    } else {
        sendJsonResponse("error", "Failed to update password.");
    }
} else {
    sendJsonResponse("error", "User not found with this email.");
}
?>
