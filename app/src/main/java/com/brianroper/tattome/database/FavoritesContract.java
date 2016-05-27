package com.brianroper.tattome.database;

import android.provider.BaseColumns;

/**
 * Created by brianroper on 5/27/16.
 */
public class FavoritesContract {

    FavoritesContract(){

    }

    public final static class FavoritesEntry implements BaseColumns{

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
    }

}
