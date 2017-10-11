package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Book loader uses QueryUtils to fetch the books based on
 * an API request URL.
 */

public class BookLoader extends AsyncTaskLoader<AsyncTaskResult<Book>> {
    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    /** Url for the query **/
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public AsyncTaskResult<Book> loadInBackground() {
        if (mUrl == null)
            return null;

        try {
            ArrayList books = QueryUtils.fetchBooksData(mUrl);
            return new AsyncTaskResult<>(books,null);
        } catch (IOException | JSONException e) {
            return new AsyncTaskResult<>(null, e);
        }
    }
}
