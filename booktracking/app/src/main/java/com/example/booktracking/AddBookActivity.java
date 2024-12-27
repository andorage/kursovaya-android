package com.example.booktracking;


import android.content.Intent;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddBookActivity extends AppCompatActivity {

    private BookListAdapter bookListAdapter;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText reviewEditText;
    private Button saveButton;
    private String bookType;
    private Integer userId;
    private View tvReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Добавление книги");
        }

        titleEditText = findViewById(R.id.title_edit_text);
        authorEditText = findViewById(R.id.author_edit_text);
        bookType = getIntent().getStringExtra("bookType");
        userId = getIntent().getIntExtra("userId", -1);
        Log.d("Booktype", bookType);
        if ("read".equals(bookType)) {
            reviewEditText = findViewById(R.id.review_edit_text);
            reviewEditText.setVisibility(View.VISIBLE);
            tvReview = findViewById(R.id.tv_review);
            tvReview.setVisibility(View.VISIBLE);
        }
        else {
            reviewEditText = findViewById(R.id.review_edit_text);
            reviewEditText.setVisibility(View.GONE);
            tvReview = findViewById(R.id.tv_review);
            tvReview.setVisibility(View.GONE);
        }
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(this::addBook);
    }

    private void addBook(View view) {
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String review;
        Log.d("booktype", bookType);
        if ("read".equals(bookType)) {
            review = reviewEditText.getText().toString().trim();
        } else {
            review = "";
        }
        Log.d("review", review);

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("read".equals(bookType) && review.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url;
                    if ("read".equals(bookType)) {
                        url = new URL(Constants.URL_ADD_READ_BOOK);
                    }
                    else {
                        url = new URL(Constants.URL_ADD_EXPECTED_BOOK);
                    }
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                    String postData = "title=" + URLEncoder.encode(title, "UTF-8") +
                            "&author=" + URLEncoder.encode(author, "UTF-8") +
                            "&user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8");
                    if ("read".equals(bookType)) {
                        postData = postData + "&review=" + URLEncoder.encode(review, "UTF-8");
                    }
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postData);
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream responseStream = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getInt("success") == 1) {
                            runOnUiThread(() -> {
                                Toast.makeText(AddBookActivity.this, "Книга добавлена", Toast.LENGTH_SHORT).show();
                                bookListAdapter.notifyDataSetChanged();
                                finish();
                            });
                        } else {
                            String message = jsonResponse.getString("message");
                            runOnUiThread(() -> Toast.makeText(AddBookActivity.this, message, Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(AddBookActivity.this, "Ошибка сервера", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(AddBookActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
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
}
