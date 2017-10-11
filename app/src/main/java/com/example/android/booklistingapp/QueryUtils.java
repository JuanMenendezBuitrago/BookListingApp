package com.example.android.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * QueryUtils holds static methods that are used to make API queries and process the result.
 */
public final class QueryUtils {

    /** log tag for debugging **/
    private static final String LOG_TAG = QueryUtils.class.getName();


    /**
     * private constructor.
     */
    private QueryUtils() {
    }


    /**
     * Query the Google books API and return the list of books.
     */
    public static ArrayList<Book> fetchBooksData(String requestUrl) throws JSONException, IOException {

        // Create URL object
        URL url = new URL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = makeHttpRequest(url);

        // Extract books from JSON
        ArrayList<Book> books = extractBooksFromJson(jsonResponse);
        return books;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        Context context = BookListingApp.getAppContext();
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod(context.getResources().getString(R.string.request_method_get));
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, context.getResources().getString(R.string.error_response_code) + urlConnection.getResponseCode());
                Log.e(LOG_TAG, urlConnection.getResponseMessage());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getResources().getString(R.string.problem_retrieving_book_results), e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        Context context = BookListingApp.getAppContext();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(context.getResources().getString(R.string.charset_utf_8)));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Book> extractBooksFromJson(String resultJSON) throws JSONException{

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();
        Context context = BookListingApp.getAppContext();

        JSONObject reader = new JSONObject(resultJSON);
        if (Integer.valueOf(reader.getString("totalItems")) == 0)
            return null;

        JSONArray booksJSON = reader.getJSONArray(context.getResources().getString(R.string.api_json_items));
        for (int i=0; i<booksJSON.length(); i++) {
            // grab the book JSON object
            JSONObject book = booksJSON.getJSONObject(i);

            // get title
            String title = book
                    .getJSONObject(context.getResources().getString(R.string.api_json_volume_info))
                    .optString(context.getResources().getString(R.string.api_json_title), context.getResources().getString(R.string.no_title));

            // get link URL
            String url = book
                    .getJSONObject(context.getResources().getString(R.string.api_json_volume_info))
                    .optString(context.getResources().getString(R.string.api_json_preview_link), context.getResources().getString(R.string.no_self_link));

            // get authors
            ArrayList<String> authors = new ArrayList<>();
            JSONArray authorsJSON = book
                    .getJSONObject(context.getResources().getString(R.string.api_json_volume_info))
                    .optJSONArray(context.getResources().getString(R.string.api_json_authors));
            try {
                for (int j = 0; j < authorsJSON.length(); j++) {
                    authors.add(authorsJSON.optString(j));
                }
            } catch (NullPointerException e ) {
                authors = new ArrayList<>();
            }

            // finally, add the book object to the list
            books.add(new Book(title, authors, url));
        }


        // Return the list of books
        return books;
    }

}