<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$role = $data['role'] ?? '';
$email = $conn->real_escape_string($data['email'] ?? '');
$password = $data['password'] ?? '';

if (empty($role) || empty($email) || empty($password)) {
    sendJsonResponse("error", "Role, Email, and Password are required.");
}

$table = '';
$name_field = '';

if ($role === 'user') {
    $table = 'users';
    $name_field = 'full_name';
} elseif ($role === 'hotel') {
    $table = 'hotels';
    $name_field = 'hotel_name'; // We will display Hotel Name on the dashboard
} elseif ($role === 'delivery') {
    $table = 'delivery_partners';
    $name_field = 'full_name';
} else {
    sendJsonResponse("error", "Invalid role specified.");
}

$sql = "SELECT * FROM $table WHERE email = '$email'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if (password_verify($password, $row['password'])) {
        // Build return data
        $userData = [
            "id" => $row['id'],
            "name" => $row[$name_field],
            "email" => $row['email'],
            "role" => $role
        ];
        sendJsonResponse("success", "Login successful.", $userData);
    } else {
        sendJsonResponse("error", "Invalid password.");
    }
} else {
    sendJsonResponse("error", "User not found with this email.");
}
?>
