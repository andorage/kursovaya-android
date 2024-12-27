<?php
require 'db_connect.php';

if (isset($_POST['bookId']) && isset($_POST['title']) && isset($_POST['author']) && isset($_POST['bookType'])) {
    $bookId = intval($_POST['bookId']);
    $title = $_POST['title'];
    $author = $_POST['author'];
    $review = isset($_POST['review']) ? $_POST['review'] : '';
    $bookType = $_POST['bookType'];

    $db = new DB_CONNECT();
    $con = $db->con;
    if ($bookType === "read") {
        $result = $con->query("UPDATE read_books SET title = '$title', author = '$author', review = '$review' WHERE id = $bookId");
    } else {
        $result = $con->query("UPDATE expected_books SET title = '$title', author = '$author' WHERE id = $bookId");
    }
    if ($result) {
        echo json_encode(["success" => 1, "message" => "Данные успешно обновлены"], JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode(["success" => 0, "message" => "Ошибка обновления"], JSON_UNESCAPED_UNICODE);
    }
}
?>