package com.example.eman.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.eman.popularmovies.data.MoviesContract.MovieEntry.CONTENT_URI;
import static java.lang.Boolean.FALSE;

/**
 * Created by Eman on 2/20/2018.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.eman.popularmovies.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String IMAGE_PATH = "image";
        public static final String RELEASE_DATE = "date";
        public static final String OVERVIEW = "overview";
        public static final String RATE = "rate";
        public static final String TITLE = "title";
        public static final String IS_FAVORITE = "favorite";
        public static final String BACKDROP_IMAGE = "backdrop_image";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }

    public static Uri buildFlavorsUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

}
