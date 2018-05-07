package com.example.aaronbrecher.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.aaronbrecher.popularmovies.data.MovieContract.FavoriteEntry.TABLE_NAME;

/**
 * Created by aaronbrecher on 5/4/18.
 */

public class MovieContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private MovieDbHelper mMovieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTRACT_AUTHORITY, MovieContract.PATH_FAVORITES, MOVIES);
        uriMatcher.addURI(MovieContract.CONTRACT_AUTHORITY, MovieContract.PATH_FAVORITES + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case MOVIES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                retCursor = db.query(TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) returnUri = ContentUris.withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int movieDeleted = 0;
        switch (match){
            case MOVIES_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                movieDeleted = db.delete(TABLE_NAME,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID +"=?", new String[]{movieId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(movieDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
