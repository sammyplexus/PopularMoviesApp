package com.example.android.popularmoviesapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Agbede Samuel D on 5/9/2017.
 */

public class SQLHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pma.db";
    private static final int DATABASE_VERSION = 1;

    public SQLHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_PMA_TABLE = "CREATE TABLE "+ PMAContract.PMAEntry.TABLE_NAME + " ( " +
                PMAContract.PMAEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PMAContract.PMAEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                PMAContract.PMAEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                PMAContract.PMAEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                PMAContract.PMAEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                PMAContract.PMAEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, "+
                PMAContract.PMAEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                " UNIQUE (" + PMAContract.PMAEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(CREATE_PMA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String UPGRADE_PMA_TABLE = "DROP TABLE IF EXISTS "+ PMAContract.PMAEntry.TABLE_NAME;
        db.execSQL(UPGRADE_PMA_TABLE);
        onCreate(db);
    }
}
