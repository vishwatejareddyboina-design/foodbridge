<?php
include '../db_connect.php';

// Get POST data (JSON or Form Data)
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

$role = $data['role'] ?? '';
$password = $data['password'] ?? '';

if (empty($role) || empty($password)) {
    sendJsonResponse("error", "Role and Password are required.");
}

$hashed_password = password_hash($password, PASSWORD_DEFAULT);

if ($role === 'user') {
    $full_name = $conn->real_escape_string($data['full_name'] ?? '');
    $email = $conn->real_escape_string($data['email'] ?? '');
    $phone = $conn->real_escape_string($data['phone'] ?? '');

    $sql = "INSERT INTO users (full_name, email, phone, password) VALUES ('$full_name', '$email', '$phone', '$hashed_password')";
} elseif ($role === 'hotel') {
    $hotel_name = $conn->real_escape_string($data['hotel_name'] ?? '');
    $owner_name = $conn->real_escape_string($data['owner_name'] ?? '');
    $email = $conn->real_escape_string($data['email'] ?? '');
    $phone = $conn->real_escape_string($data['phone'] ?? '');
    $address = $conn->real_escape_string($data['address'] ?? '');

    $sql = "INSERT INTO hotels (hotel_name, owner_name, email, phone, address, password) VALUES ('$hotel_name', '$owner_name', '$email', '$phone', '$address', '$hashed_password')";
} elseif ($role === 'delivery') {
    $full_name = $conn->real_escape_string($data['full_name'] ?? '');
    $email = $conn->real_escape_string($data['email'] ?? '');
    $phone = $conn->real_escape_string($data['phone'] ?? '');
    $vehicle_details = $conn->real_escape_string($data['vehicle_details'] ?? '');

    $sql = "INSERT INTO delivery_partners (full_name, email, phone, vehicle_details, password) VALUES ('$full_name', '$email', '$phone', '$vehicle_details', '$hashed_password')";
} else {
    sendJsonResponse("error", "Invalid role specified.");
}

if ($conn->query($sql) === TRUE) {
    sendJsonResponse("success", "Registration successful.");
} else {
    // Check for duplicate entry
    if ($conn->errno == 1062) {
         sendJsonResponse("error", "Email already exists.");
    }
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
