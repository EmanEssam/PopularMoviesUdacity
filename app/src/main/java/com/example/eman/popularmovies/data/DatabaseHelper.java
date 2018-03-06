package com.example.eman.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.method.NumberKeyListener;
import android.util.Log;

import com.example.eman.popularmovies.model.Movie;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eman on 2/20/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;
    private static final String[] COLUMNS = {MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TITLE, MoviesContract.MovieEntry.OVERVIEW, MoviesContract.MovieEntry.IMAGE_PATH, MoviesContract.MovieEntry.RATE, MoviesContract.MovieEntry.RELEASE_DATE,
            MoviesContract.MovieEntry.BACKDROP_IMAGE, MoviesContract.MovieEntry.IS_FAVORITE
    };

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

    public MovieData getMovieById(String id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        // SELECT ID, TITLE, AUTHOR FROM BOOK WHERE ID = id"
        Cursor cursor =
                db.query(MoviesContract.MovieEntry.TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        MoviesContract.MovieEntry._ID + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        MovieData movie = new MovieData();
        movie.setId(cursor.getString(0));
        movie.setTitle(cursor.getString(1));
        movie.setOverview(cursor.getString(2));
        movie.setPosterPath(cursor.getString(3));
        movie.setVoteAverage(cursor.getString(4));
        movie.setReleaseDate(cursor.getString(5));
        movie.setBackdrop_path(cursor.getString(6));
//        movie.setFavorite(cursor.getString(6));


        // 5. return book
        // log
        return movie;
    }

    public List<MovieData> getAllMovies() {
        List<MovieData> movies = new LinkedList<MovieData>();

        // 1. build the query
        String query = "SELECT  * FROM " + MoviesContract.MovieEntry.TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        MovieData movieData = null;
        if (cursor.moveToFirst()) {
            do {
                movieData = new MovieData();
                movieData.setId(cursor.getString(0));
                movieData.setTitle(cursor.getString(1));
                movieData.setOverview(cursor.getString(2));
                movieData.setPosterPath(cursor.getString(3));
                movieData.setVoteAverage(cursor.getString(4));
                movieData.setReleaseDate(cursor.getString(5));
                movieData.setBackdrop_path(cursor.getString(6));

                // Add book to books
                movies.add(movieData);
            } while (cursor.moveToNext());
        }

        //     log
        Log.d("getAllBooks", movies.toString());

        // return books
        return movies;
    }

    public void insertIntoDb(String title, String id, String overview, String image_path, String date, String rate, String backdrop, Boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry._ID, id);
        values.put(MoviesContract.MovieEntry.TITLE, title);
        values.put(MoviesContract.MovieEntry.OVERVIEW, overview);
        values.put(MoviesContract.MovieEntry.IMAGE_PATH, image_path);
        values.put(MoviesContract.MovieEntry.RATE, rate);
        values.put(MoviesContract.MovieEntry.RELEASE_DATE, date);
        values.put(MoviesContract.MovieEntry.BACKDROP_IMAGE, backdrop);
        values.put(MoviesContract.MovieEntry.IS_FAVORITE, isFavorite);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
    }

    public void deleteMovie(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        // "DELETE BOOK WHERE ID = book.id"
        db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                MoviesContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(id)});

        // 3. close
        db.close();

        // log

    }


}