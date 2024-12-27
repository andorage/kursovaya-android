package com.example.booktracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class BookListActivity extends AppCompatActivity {

    private ListView bookListView;
    private BookListAdapter bookListAdapter;
    private ArrayList<String> bookTitles = new ArrayList<>();
    private ArrayList<Integer> bookIds = new ArrayList<>();
    private String bookType;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        bookListView = findViewById(R.id.book_list_view);
        bookType = getIntent().getStringExtra("bookType");

        if (getSupportActionBar() != null) {
            if ("read".equals(bookType))
                getSupportActionBar().setTitle("Список прочтённых книг");
            else
                getSupportActionBar().setTitle("Список планируемых книг");
        }
        userId = getIntent().getIntExtra("userId", -1);
        Log.d("USERID", String.valueOf(userId));
        bookListAdapter = new BookListAdapter(this, bookTitles, bookIds, bookType, this::deleteBook);
        bookListView.setAdapter(bookListAdapter);
        bookListAdapter.setNotifyOnChange(true);
        loadBooksFromDatabase();

        bookListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
            int bookId = bookIds.get(position);
            intent.putExtra("bookId", bookId);
            intent.putExtra("bookType", bookType);
            startActivity(intent);
        });
    }

    // Загрузка списка книг
    private void loadBooksFromDatabase() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.URL_GET_BOOKS + "?bookType=" + bookType + "&userId=" + userId);
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
                    JSONArray books = new JSONArray(response.toString());
                    runOnUiThread(() -> {
                        for (int i = 0; i < books.length(); i++) {
                            try {
                                JSONObject book = books.getJSONObject(i);
                                String title = book.getString("title");
                                int bookId = book.getInt("id");
                                bookIds.add(bookId);
                                bookTitles.add(title);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        bookListAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Удаление книги
    private void deleteBook(int position) {
        int bookId = bookIds.get(position);

        new Thread(() -> {
            try {
                URL url = new URL(Constants.URL_DELETE_BOOK + "?bookId=" + bookId + "&bookType=" + bookType);
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
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        try {
                            if (jsonResponse.getInt("success") == 1) {
                                Toast.makeText(this, "Книга успешно удалена", Toast.LENGTH_SHORT).show();
                                bookIds.remove(position);
                                bookTitles.remove(position);
                                bookListAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(this, "Ошибка при удалении книги: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка при подключении к серверу", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Ошибка при удалении книги", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_book) {
            Intent addBookIntent = new Intent(this, AddBookActivity.class);
            addBookIntent.putExtra("bookType", bookType);
            addBookIntent.putExtra("userId", userId);
            startActivity(addBookIntent);
            return true;
        } else if (item.getItemId() == R.id.action_back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
