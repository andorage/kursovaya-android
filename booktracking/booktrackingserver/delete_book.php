<?php
$response = array();

if (isset($_GET['bookId']) && isset($_GET['bookType'])) {
    require 'db_connect.php';

    $db = new DB_CONNECT();
    $con = $db->con;

    $bookType = $_GET['bookType'];
    $book_id = $_GET['bookId'];
    if ($bookType === "read") {
        $result = $con->query("DELETE FROM read_books WHERE id = '$book_id'");
    } else {
        $result = $con->query("DELETE FROM expected_books WHERE id = '$book_id'");
    }

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Book successfully deleted.";
    } else {
        $response["success"] = 0;
        $response["message"] = "Failed to delete book.";
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing.";
}

echo json_encode($response);
?>