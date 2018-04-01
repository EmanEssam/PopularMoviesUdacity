package com.example.eman.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eman on 2/20/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +

                        MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +

                        MoviesContract.MovieEntry.TITLE + " TEXT, " +

                        MoviesContract.MovieEntry.OVERVIEW + " TEXT, " +

                        MoviesContract.MovieEntry.IMAGE_PATH + " TEXT, " +
                        MoviesContract.MovieEntry.RATE + " TEXT, " +

                        MoviesContract.MovieEntry.RELEASE_DATE + " TEXT, " + MoviesContract.MovieEntry.BACKDROP_IMAGE + " TEXT, " + MoviesContract.MovieEntry.IS_FAVORITE + " INTEGER" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}