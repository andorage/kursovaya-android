<?php
require 'db_connect.php';

if (isset($_GET['bookId']) && isset($_GET['bookType'])) {
    $bookId = intval($_GET['bookId']);
    $bookType = strval($_GET['bookType']);

    $db = new DB_CONNECT();
    $con = $db->con;

    $con->set_charset("utf8");
    if ($bookType === "read") {
        $result = $con->query("SELECT id, title, author, review FROM read_books WHERE id = $bookId");
    } else {
        $result = $con->query("SELECT id, title, author FROM expected_books WHERE id = $bookId");
    }
    if ($result && $result->num_rows > 0) {
        $book = $result->fetch_assoc();
        echo json_encode($book, JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode([]);
    }
}
?>