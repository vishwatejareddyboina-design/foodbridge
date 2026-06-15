<?php
require_once 'db_connect.php';

$sql = "ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_address TEXT NULL AFTER total_amount";
if ($conn->query($sql)) {
    echo "Success";
} else {
    echo "Error: " . $conn->error;
}
?>
