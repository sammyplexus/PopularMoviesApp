package com.example.android.popularmoviesapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Agbede Samuel D on 5/9/2017.
 */

public class PMAContract {
    public final static String CONTENT_AUTHORITY = "com.example.android.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static class PMAEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "PMA_MOVIES";
        public static final String COLUMN_MOVIE_ID = "MOVIE_ID";
        public static final String COLUMN_MOVIE_TITLE = "MOVIE_TITLE";
        public static final String COLUMN_MOVIE_POSTER = "MOVIE_POSTER";
        public static final String COLUMN_MOVIE_SYNOPSIS = "MOVIE_SYNOPSIS";
        public static final String COLUMN_MOVIE_RATING = "MOVIE_RATING";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "MOVIE_RELEASE_DATE";

        public static Uri buildMovieUriWithID(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id))
                    .build();
        }
    }
}
