package com.brianroper.tattome.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by brianroper on 5/27/16.
 */
public class TattooContract {

    public static final String CONTENT_AUTHORITY = "com.brianroper.tattome.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    TattooContract(){

    }

    /* Inner class defines the contents of the content table */
    public final static class TattooEntries implements BaseColumns{

        public static final String TABLE_NAME = "tattoo";
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";

        //create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        //create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        //for building URI's on insertion
        public static Uri buildTattooUri(long id){

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
