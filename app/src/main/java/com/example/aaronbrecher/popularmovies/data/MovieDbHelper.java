package com.example.aaronbrecher.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.aaronbrecher.popularmovies.data.MovieContract.FavoriteEntry;

/**
 * Created by aaronbrecher on 5/4/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movie-favorites.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID               + " INTEGER, " +
                FavoriteEntry.COLUMN_MOVIE_ID   + " TEXT PRIMARY KEY);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
