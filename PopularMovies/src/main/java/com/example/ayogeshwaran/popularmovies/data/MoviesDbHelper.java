package com.example.ayogeshwaran.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ayogeshwaran.popularmovies.data.MoviesContract.MoviesEntry;

/**
 * Created by ayogeshwaran on 02/01/18.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                        MoviesEntry._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesEntry.COLUMN_ADULT    + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_OVERVIEW      + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_POPULARITY    + " INTEGER, "       +
                        MoviesEntry.COLUMN_POSTER_PATH   + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_TITLE         + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_VIDEO         + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_VOTE_AVERAGE  + " TEXT NOT NULL, " +
                        MoviesEntry.COLUMN_VOTE_COUNT    + " INTEGER, " +

                        " UNIQUE (" + MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
