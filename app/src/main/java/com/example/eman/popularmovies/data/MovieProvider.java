package com.example.eman.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Eman on 3/5/2018.
 */

public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_NAME, MOVIE);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // All Flavors selected
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case MOVIE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                return MoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MoviesContract.buildFlavorsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case MOVIE:
                numDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MoviesContract.MovieEntry.TABLE_NAME + "'");
                break;
            case MOVIE_WITH_ID:
                numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MoviesContract.MovieEntry.TABLE_NAME + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
