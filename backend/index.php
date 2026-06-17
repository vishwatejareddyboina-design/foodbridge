<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$response = [
    "status" => "success",
    "message" => "Welcome to the Food Bridge API!"
];

echo json_encode($response);
?>
