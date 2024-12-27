<?php
require 'db_connect.php';

$response = array();

if (isset($_GET['bookType']) && isset($_GET['userId'])) {
    $bookType = $_GET['bookType'];
    $userId = $_GET['userId'];  // Получаем userId из запроса

    $db = new DB_CONNECT();
    $con = $db->con;

    // Вставляем userId в запрос
    if ($bookType === "read") {
        $result = $con->query("SELECT id, title, author, review FROM read_books WHERE user_id = '$userId'");
    } else {
        $result = $con->query("SELECT id, title, author FROM expected_books WHERE user_id = '$userId'");
    }

    if ($result) {
        $books = array();
        while ($row = $result->fetch_assoc()) {
            $books[] = $row;
        }
        echo json_encode($books, JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode([]);
    }
}
?>