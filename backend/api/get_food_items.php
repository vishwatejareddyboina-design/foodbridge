<?php
include '../db_connect.php';

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_GET;
}

$hotel_id = $data['hotel_id'] ?? '';

if (empty($hotel_id)) {
    // Fetch all foods for user
    $sql = "
        SELECT 
            f.*, 
            h.hotel_name,
            COALESCE(
                (SUM(CASE WHEN r.is_hygienic = 1 THEN 1 ELSE 0 END) / 
                 NULLIF(SUM(CASE WHEN r.is_hygienic IS NOT NULL THEN 1 ELSE 0 END), 0)) * 100
            , -1) as hygiene_percentage
        FROM 
            food_items f 
        JOIN 
            hotels h ON f.hotel_id = h.id 
        LEFT JOIN 
            reviews r ON r.hotel_id = h.id
        GROUP BY 
            f.id
        ORDER BY 
            f.created_at DESC
    ";
} else {
    // Fetch only hotel's foods
    $hotel_id = $conn->real_escape_string($hotel_id);
    $sql = "SELECT * FROM food_items WHERE hotel_id = '$hotel_id' ORDER BY created_at DESC";
}

$result = $conn->query($sql);
$foods = [];

if ($result) {
    while($row = $result->fetch_assoc()) {
        $foods[] = $row;
    }
    sendJsonResponse("success", "Foods retrieved", $foods);
} else {
    sendJsonResponse("error", "Error: " . $conn->error);
}
?>
