package com.brianroper.tattome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brianroper on 5/27/16.
 */
public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tattoo.db";

    public DbHandler(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TATTOO_TABLE = "CREATE TABLE "+ TattooContract.TattooEntries.TABLE_NAME + "(" +
                TattooContract.TattooEntries._ID + " INTEGER PRIMARY KEY, " +
                TattooContract.TattooEntries.COLUMN_TITLE + " TEXT, " +
                TattooContract.TattooEntries.COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(SQL_CREATE_TATTOO_TABLE);
    }

    //upgrade database when version is changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TattooContract.TattooEntries.TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TattooContract.TattooEntries.TABLE_NAME + "'");

        //re-create database
        onCreate(db);
    }
}
