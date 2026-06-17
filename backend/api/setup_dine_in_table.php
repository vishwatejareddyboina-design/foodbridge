<?php
include '../db_connect.php';

$sql = "CREATE TABLE IF NOT EXISTS dine_in_capacity (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL,
    total_places INT NOT NULL,
    in_use INT NOT NULL,
    remaining INT NOT NULL,
    max_clear_time_mins INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES users(id) ON DELETE CASCADE
)";

if ($conn->query($sql) === TRUE) {
    echo "Table dine_in_capacity created successfully";
} else {
    echo "Error creating table: " . $conn->error;
}
?>
