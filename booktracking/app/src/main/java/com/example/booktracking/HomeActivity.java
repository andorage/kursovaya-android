package com.example.booktracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getIntent().getIntExtra("userId", -1);
        Button btnReadBooks = findViewById(R.id.btn_read_books);
        Button btnPendingBooks = findViewById(R.id.btn_pending_books);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Выбор раздела");
        }

        btnReadBooks.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookListActivity.class);
            intent.putExtra("bookType", "read");
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnPendingBooks.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookListActivity.class);
            intent.putExtra("bookType", "expected");
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

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