package com.example.android.popularmoviesapp.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Agbede Samuel D on 5/9/2017.
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    SQLHandler sqlHandler;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PMAContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PMAContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(authority, PMAContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }


    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        throw new UnsupportedOperationException("Use insert. Bulk insert is not supported yet");
    }

    @Override
    public boolean onCreate() {
        sqlHandler = new SQLHandler(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match){
            case CODE_MOVIE:
                cursor = sqlHandler.getReadableDatabase().query(PMAContract.PMAEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_MOVIE_WITH_ID:
                String movie_id = uri.getLastPathSegment();
                String[] mSelectionArgs = new String[]{movie_id};
                cursor = sqlHandler.getReadableDatabase().query(PMAContract.PMAEntry.TABLE_NAME, projection, PMAContract.PMAEntry.COLUMN_MOVIE_ID + "=?", mSelectionArgs, null, null, sortOrder);
                break;
            default:throw new UnsupportedOperationException("This URI is not supported : "+ uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("This is not implemented for now");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        long _id;
        int rows = 0;
        switch (match){
            case CODE_MOVIE:
               _id = sqlHandler.getWritableDatabase().insert(PMAContract.PMAEntry.TABLE_NAME, null, values);
                break;
            case CODE_MOVIE_WITH_ID:
                throw new UnsupportedOperationException("Use .update() instead");
            default:throw new UnsupportedOperationException("Not supported");
        }
        if (_id != -1){
            getContext().getContentResolver().notifyChange(uri, null);
            rows++;
        }


        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int rows_deleted = 0;
        switch (match){
            case CODE_MOVIE:
                rows_deleted = sqlHandler.getWritableDatabase().delete(PMAContract.PMAEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                String [] mSelection = new String[]{id};
                rows_deleted = sqlHandler.getWritableDatabase().delete(PMAContract.PMAEntry.TABLE_NAME, PMAContract.PMAEntry.COLUMN_MOVIE_ID + "=?", mSelection);
                break;
        }
        if (rows_deleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
            return rows_deleted;
        }
        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int number_of_rows_updated = 0;
        switch (match){
            case CODE_MOVIE:
                throw new UnsupportedOperationException("You can not update the entire table. Use an ID");
            case CODE_MOVIE_WITH_ID:
                String last_path = uri.getLastPathSegment();
                String[] mSelection = new String[]{last_path};
                number_of_rows_updated = sqlHandler.getWritableDatabase().update(PMAContract.PMAEntry.TABLE_NAME, values, PMAContract.PMAEntry.COLUMN_MOVIE_ID +"=?", mSelection);
                break;
        }
        if (number_of_rows_updated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
            return number_of_rows_updated;
        }
        return -1;
    }
}
