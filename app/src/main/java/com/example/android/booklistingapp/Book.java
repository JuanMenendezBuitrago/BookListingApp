package com.example.android.booklistingapp;

import android.text.TextUtils;
import java.util.ArrayList;

/**
 * The Book instantiated from the data retrieved via API
 */

public class Book {
    /** book title **/
    private String mTitle;

    /** book authors **/
    private ArrayList<String> mAuthors;

    /** link to bok url **/
    private String mUrl;

    public Book(String mTitle, ArrayList<String> mAuthors, String mUrl) {
        this.mTitle = mTitle;
        this.mAuthors = mAuthors;
        this.mUrl = mUrl;
    }

    /**
     * @return the book's title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the book's list of authors
     */
    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    /**
     * @return the link to the book in the Google Book's site
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the string with the authors separated by commas
     */
    public String  getAuthorsAsString() {
        int length = mAuthors.size();
        if (length == 0) {
            return BookListingApp.getAppContext().getResources().getString(R.string.unknown);
        }

        String result = "";
        for(int i = 0; i < length; i++) {
            if (length > 1 && i == length-1) {
                result += " " + BookListingApp.getAppContext().getResources().getString(R.string.and) + " " + mAuthors.get(i);
            }
            else if (i == 0){
                result += mAuthors.get(i);
            } else {
                result += ", " + mAuthors.get(i);
            }
        }
        return result;
    }
}
