package com.example.android.booklistingapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.attr.data;


public class BookListingApp extends AppCompatActivity implements LoaderManager.LoaderCallbacks<AsyncTaskResult<Book>>{

    private static Context context;

    /** log's tag **/
    public static final String LOG_TAG = BookListingApp.class.getName();

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    private BookAdapter mAdapter;

    private TextView mEmptyStateTextView;

    private ProgressBar mSpinner;

    private String mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // store context to be used by QueryUtils static methods
        BookListingApp.context = getApplicationContext();

        mSpinner = (ProgressBar)findViewById(R.id.progress_loading);

        //////////////////////
        /// ListView stuff ///
        //////////////////////

        ListView booksListView = (ListView)findViewById(R.id.list);

        // set the ListView's empty view
        mEmptyStateTextView = (TextView)findViewById(R.id.text_empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        // set the ListView's adapter
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(mAdapter);

        // check for loader to prevent from fetching data on fresh start
        if (getLoaderManager().getLoader(BOOK_LOADER_ID) != null)
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, BookListingApp.this);

        //////////////
        /// events ///
        //////////////

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Open website with book url
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });

        // set OnClickListener for the search button
        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide keyboard
                hideSoftKeyboard(BookListingApp.this);

                mQuery = getSearchTerm();
                if (TextUtils.isEmpty(mQuery)) {
                    mEmptyStateTextView.setText(R.string.empty_query);
                }
                else if (QueryUtils.isConnected(BookListingApp.this)) {
                    mSpinner.setVisibility(View.VISIBLE);
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.empty_string);

                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookListingApp.this);
                } else {
                    // display error and hide loading indicator so error message will be visible
                    mSpinner.setVisibility(View.GONE);
                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_connection);
                }
            }
        });
    }

    ////////////////////////////////
    /// Loader overridden methods ///
    ////////////////////////////////

    @Override
    public Loader<AsyncTaskResult<Book>> onCreateLoader(int i, Bundle bundle) {

        // build API query URI
        Uri apiUri = Uri.parse(getString(R.string.google_api_url));
        Uri.Builder uriBuilder = apiUri.buildUpon();
        uriBuilder.appendQueryParameter(getString(R.string.parameter_key), getString(R.string.google_api_key));
        uriBuilder.appendQueryParameter(getString(R.string.parameter_maxResults), getString(R.string.value_maxResults));
        uriBuilder.appendQueryParameter(getString(R.string.parameter_langRestrict), getString(R.string.english_code));
        uriBuilder.appendQueryParameter(getString(R.string.parameter_q), mQuery);

        return new BookLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<AsyncTaskResult<Book>> loader, AsyncTaskResult<Book> data) {
        mSpinner.setVisibility(View.GONE);

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data.getResult() != null && data.getResult().size() > 0) {
            mEmptyStateTextView.setText(R.string.empty_string);
            mAdapter.addAll(data.getResult());
        } else if (data.getResult() == null && data.getException() == null) {
            // Set empty state text to display "No books found."
            mEmptyStateTextView.setText(R.string.no_results);
        } else if (data.getException() != null){
            mEmptyStateTextView.setText(data.getException().getMessage());
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    /**
     * Get text inside the search box
     * @return
     */
    private String getSearchTerm() {
        EditText queryEditText = (EditText)findViewById(R.id.edit_search_terms);
        String result = queryEditText.getText().toString();
        return result;
    }

    /**
     * Hide keyboard
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Get application context
     * @return
     */
    public static Context getAppContext() {
        return BookListingApp.context;
    }

}
