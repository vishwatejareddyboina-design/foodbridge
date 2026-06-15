<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);
$query = isset($data['query']) ? $data['query'] : '';

$sql = "
    SELECT 
        f.*, 
        h.hotel_name,
        COALESCE(AVG(r.rating), 0) as avg_rating,
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
    WHERE 
        f.item_name LIKE ? OR h.hotel_name LIKE ?
    GROUP BY 
        f.id
    ORDER BY 
        f.discounted_price ASC, 
        avg_rating DESC
";

$stmt = $conn->prepare($sql);
$searchTerm = "%" . $query . "%";
$stmt->bind_param("ss", $searchTerm, $searchTerm);
$stmt->execute();
$result = $stmt->get_result();

$foods = array();
while($row = $result->fetch_assoc()) {
    $foods[] = $row;
}

sendJsonResponse("success", "Search results retrieved", $foods);
?>
