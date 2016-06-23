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

        String query = "CREATE TABLE "+ TattooContract.TattooEntries.TABLE_NAME + "(" +
                TattooContract.TattooEntries._ID + " INTEGER PRIMARY KEY, " +
                TattooContract.TattooEntries.COLUMN_TITLE + " TEXT, " +
                TattooContract.TattooEntries.COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
