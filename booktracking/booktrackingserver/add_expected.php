<?php
$response = array();

if (isset($_POST['user_id']) && isset($_POST['title']) && isset($_POST['author'])) {
    require 'db_connect.php';

    $db = new DB_CONNECT();
    $con = $db->con;

    $user_id = $_POST['user_id'];
    $title = $_POST['title'];
    $author = $_POST['author'];

    $result = $con->query("INSERT INTO expected_books (user_id, title, author) VALUES ('$user_id', '$title', '$author')");

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Book added to expected list.";
    } else {
        $response["success"] = 0;
        $response["message"] = "Failed to add book.";
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing.";
}

echo json_encode($response);
?>