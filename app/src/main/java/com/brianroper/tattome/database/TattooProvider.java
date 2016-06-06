package com.brianroper.tattome.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by brianroper on 6/6/16.
 */
public class TattooProvider extends ContentProvider {

    private DbHandler mDbHandler;
    private static final String AUTHORITY = "com.brianroper.tattome.TattooProvider";
    private static final String FAVORITES_BASE_PATH = "favorites";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + FAVORITES_BASE_PATH);
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int FAVORITES = 100;
    public static final int FAVORITES_TITLE = 101;
    public static final int FAVORITES_IMAGE = 102;


    static{

        sUriMatcher.addURI(AUTHORITY, FAVORITES_BASE_PATH, FAVORITES);
        sUriMatcher.addURI(AUTHORITY, FAVORITES_BASE_PATH + "/#", FAVORITES_TITLE);
        sUriMatcher.addURI(AUTHORITY, FAVORITES_BASE_PATH + "/#", FAVORITES_IMAGE);

    }

    @Override
    public boolean onCreate() {

        mDbHandler = new DbHandler(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FavoritesContract.FavoritesEntry.TABLE_NAME);

        int uriType = sUriMatcher.match(uri);

        switch(uriType){

            case FAVORITES_TITLE:

                builder.appendWhere(FavoritesContract.FavoritesEntry.COLUMN_TITLE + "="
                        + uri.getLastPathSegment());
                break;

            case FAVORITES_IMAGE:

                builder.appendWhere(FavoritesContract.FavoritesEntry.COLUMN_IMAGE + "="
                        + uri.getLastPathSegment());
                break;

            case FAVORITES:
                break;

            default: throw new IllegalArgumentException("Unknown Uri");
        }

        Cursor cursor = builder.query(mDbHandler.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sUriMatcher.match(uri);

        SQLiteDatabase db = mDbHandler.getWritableDatabase();

        long id = 0;

        switch (uriType){

            case FAVORITES:

                id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);

                break;

            default: throw new IllegalArgumentException();
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(FavoritesContract.FavoritesEntry.TABLE_NAME
                + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sUriMatcher.match(uri);

        SQLiteDatabase db = mDbHandler.getWritableDatabase();

        int rowsUpdated = 0;

        switch (uriType){

            case FAVORITES:

                rowsUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case FAVORITES_TITLE:

                rowsUpdated = db.update(FavoritesContract.FavoritesEntry.COLUMN_TITLE,
                        values,
                        selection,
                        selectionArgs);
                break;

            case FAVORITES_IMAGE:

                rowsUpdated = db.update(FavoritesContract.FavoritesEntry.COLUMN_IMAGE,
                        values,
                        selection,
                        selectionArgs);
                break;

            default: throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
