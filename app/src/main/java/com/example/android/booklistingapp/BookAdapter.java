package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Book adapter for the ListView
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static class ViewHolder{
        TextView bookTitle;
        TextView bookAuthors;
    }

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Book currentBook = getItem(position);
        if (convertView == null) {
            // inflate view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_layout, parent, false);

            // create view holder to store id's
            viewHolder = new ViewHolder();
            viewHolder.bookTitle = convertView.findViewById(R.id.text_book_title);
            viewHolder.bookAuthors = convertView.findViewById(R.id.text_book_authors);
            // attach it to the view
            convertView.setTag(viewHolder);
        } else {
            // extract holder from the view
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // set the name
        TextView bookTitleTV = viewHolder.bookTitle;
        bookTitleTV.setText(currentBook.getTitle());

        // set the authors
        TextView bookAuthorsTV = viewHolder.bookAuthors;
        bookAuthorsTV.setText(currentBook.getAuthorsAsString());

        return convertView;
    }
}
