<?php
require_once '../db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

if(!isset($data['hotel_id'])) {
    sendJsonResponse("error", "Missing hotel_id");
}

$hotel_id = $data['hotel_id'];

// Get reviews with user names
$sql = "
    SELECT r.*, u.full_name as user_name 
    FROM reviews r 
    JOIN users u ON r.user_id = u.id 
    WHERE r.hotel_id = ? 
    ORDER BY r.created_at DESC
";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $hotel_id);
$stmt->execute();
$result = $stmt->get_result();

$reviews = array();
$total_rating = 0;
$count = 0;
$hygiene_count = 0;
$hygienic = 0;

while($row = $result->fetch_assoc()) {
    $reviews[] = $row;
    $total_rating += $row['rating'];
    $count++;
    
    if($row['is_hygienic'] !== null) {
        $hygiene_count++;
        if($row['is_hygienic'] == 1) {
            $hygienic++;
        }
    }
}

$average_rating = $count > 0 ? round($total_rating / $count, 1) : 0;
$hygiene_percentage = $hygiene_count > 0 ? round(($hygienic / $hygiene_count) * 100) : null;

sendJsonResponse("success", "Reviews retrieved", array(
    "reviews" => $reviews,
    "average_rating" => $average_rating,
    "total_reviews" => $count,
    "hygiene_percentage" => $hygiene_percentage
));
?>
