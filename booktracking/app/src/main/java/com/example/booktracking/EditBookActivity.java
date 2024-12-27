package com.example.booktracking;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditBookActivity extends AppCompatActivity {

    private EditText etAuthor, etTitle, etReview;
    private Button btnSave;
    private int bookId;
    private String bookType, review, title, author;
    private BookListAdapter bookListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Редактирование");
        }

        bookType = String.valueOf(getIntent().getStringExtra("bookType"));
        Log.d("BOOKTYPEEDIT", bookType);
        etAuthor = findViewById(R.id.et_author);
        etTitle = findViewById(R.id.et_title);
        etReview = findViewById(R.id.et_review);
        btnSave = findViewById(R.id.btn_save);
        if (!"read".equals(bookType)) {
            etReview.setVisibility(View.GONE);
            View tvReview = findViewById(R.id.tv_review);
            tvReview.setVisibility(View.GONE);
        }
        bookId = getIntent().getIntExtra("bookId", -1);
        loadBookDetails();
        etAuthor.setText(author);
        etTitle.setText(title);
        etReview.setText(review);
        btnSave.setOnClickListener(v -> saveBookDetails());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBookDetails() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.URL_GET_BOOK_DETAILS + "?bookId=" + bookId + "&bookType=" + bookType);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    JSONObject book = new JSONObject(response.toString());
                    title = book.getString("title");
                    author = book.getString("author");
                    review = book.optString("review", "");
                    runOnUiThread(() -> {
                        etTitle.setText(title);
                        etAuthor.setText(author);
                        etReview.setText(review);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void saveBookDetails() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String review = etReview.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Поля 'Название' и 'Автор' обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(Constants.URL_UPDATE_BOOK);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "bookId=" + bookId +
                        "&title=" + URLEncoder.encode(title, "UTF-8") +
                        "&author=" + URLEncoder.encode(author, "UTF-8") +
                        "&review=" + URLEncoder.encode(review, "UTF-8") +
                        "&bookType=" + URLEncoder.encode(bookType, "UTF-8");
                Log.d("BOOKTYPE", bookType);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
//                        notify();
                        bookListAdapter.notifyDataSetChanged();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка подключения", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}


