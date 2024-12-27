package com.example.booktracking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> bookTitles;
    private final ArrayList<Integer> bookIds;
    private final OnBookDeleteListener deleteListener;
    private final String bookType;

    public BookListAdapter(Context context, ArrayList<String> bookTitles, ArrayList<Integer> bookIds, String bookType, OnBookDeleteListener deleteListener) {
        super(context, R.layout.list_item_book, bookTitles);
        this.context = context;
        this.bookType = bookType;
        this.bookTitles = bookTitles;
        this.bookIds = bookIds;
        this.deleteListener = deleteListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.book_title);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBookActivity.class);
            intent.putExtra("bookId", bookIds.get(position));
            intent.putExtra("bookType", bookType);
            context.startActivity(intent);
        });

        titleTextView.setText(bookTitles.get(position));
        deleteButton.setOnClickListener(v -> deleteListener.onDelete(position));

        return convertView;
    }

    public interface OnBookDeleteListener {
        void onDelete(int position);
    }
}
