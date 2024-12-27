package com.example.booktracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tvAuthor, tvTitle, tvReview;
    private int bookId;
    private String bookType;
    private String title, author, review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Данные о книге");
        }

        tvAuthor = findViewById(R.id.tv_author);
        tvTitle = findViewById(R.id.tv_title);
        tvReview = findViewById(R.id.tv_review);
        Button btnEdit = findViewById(R.id.btn_edit);

        bookId = getIntent().getIntExtra("bookId", -1);
        bookType = String.valueOf(getIntent().getStringExtra("bookType"));

        loadBookDetails();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("author", author);
            intent.putExtra("title", title);
            intent.putExtra("review", review);
            intent.putExtra("bookType", bookType);
            startActivity(intent);
        });
    }

    private void loadBookDetails() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.URL_GET_BOOK_DETAILS + "?bookId=" + bookId + "&bookType=" + bookType);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("bookId", String.valueOf(bookId)); // Логируем код ответа
                Log.d("Response Code", String.valueOf(responseCode)); // Логируем код ответа

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    Log.d("Response", response.toString()); // Логируем ответ сервера

                    JSONObject book = new JSONObject(response.toString());
                    title = book.getString("title");
                    author = book.getString("author");
                    review = book.optString("review", "Отзыв отсутствует");

                    runOnUiThread(() -> {
                        tvTitle.setText(title);
                        tvAuthor.setText(author);
                        if (!"read".equals(bookType)) tvReview.setVisibility(TextView.GONE);
                        else tvReview.setText(review);
                    });
                } else {
                    Log.e("Error", "Response code: " + responseCode); // Логируем ошибку
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
