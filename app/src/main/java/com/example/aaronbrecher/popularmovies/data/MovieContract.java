package com.example.aaronbrecher.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aaronbrecher on 5/4/18.
 */

public class MovieContract {

    public static final String CONTRACT_AUTHORITY = "com.example.aaronbrecher.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTRACT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_RELEASE = "release";
        public static final String COLUMN_MOVIE_TITLE = "title";
    }
}
