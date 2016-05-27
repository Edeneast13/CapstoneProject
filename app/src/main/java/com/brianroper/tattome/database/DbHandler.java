package com.brianroper.tattome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brianroper on 5/27/16.
 */
public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favorites.db";

    public DbHandler(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "+ FavoritesContract.FavoritesEntry.TABLE_NAME + "(" +
                FavoritesContract.FavoritesEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FavoritesContract.FavoritesEntry.COLUMN_TITLE + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
