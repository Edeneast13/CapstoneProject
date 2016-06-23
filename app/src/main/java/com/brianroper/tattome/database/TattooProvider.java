package com.brianroper.tattome.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by brianroper on 6/6/16.
 */
public class TattooProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHandler mDbHandler;

    //codes for UriMatcher
    private static final int TATTOO = 100;

    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TattooContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TattooContract.TattooEntries.TABLE_NAME, 100);

        return matcher;
    }

    @Override
    public boolean onCreate(){

        mDbHandler = new DbHandler(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri){

        final int match = sUriMatcher.match(uri);

        switch(match){

            case TATTOO:{

                return TattooContract.TattooEntries.CONTENT_DIR_TYPE;
            }
            default:{

                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        Cursor retCursor;

        switch(sUriMatcher.match(uri)){

            case TATTOO:{

                retCursor = mDbHandler.getReadableDatabase().query(
                        TattooContract.TattooEntries.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                return retCursor;
            }
            default:{

                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){

        final SQLiteDatabase db = mDbHandler.getWritableDatabase();
        Uri returnUri;

        switch(sUriMatcher.match(uri)){

            case TATTOO:{

                long _id = db.insert(TattooContract.TattooEntries.TABLE_NAME, null, values);

                if(_id > 0){

                    returnUri = TattooContract.TattooEntries.buildTattooUri(_id);
                }
                else{

                    throw new android.database.SQLException("Fauled to insert row into: " + uri);
                }
                break;
            }

            default:{

                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){

        final SQLiteDatabase db = mDbHandler.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;

        switch(match){

            case TATTOO:

                numDeleted = db.delete(TattooContract.TattooEntries.TABLE_NAME, selection, selectionArgs);

                //reset the id
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TattooContract.TattooEntries.TABLE_NAME + "'");

                break;

            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){

        final SQLiteDatabase db = mDbHandler.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match){

            case TATTOO:

                //allows for multiple transactions
                db.beginTransaction();

                //keep track of successful inserts
                int numInserted = 0;

                try{

                    for(ContentValues value : values){

                        if(value == null){

                            throw new IllegalArgumentException("Cannot have null content values");
                        }

                        long _id = -1;

                        try{

                            _id = db.insertOrThrow(TattooContract.TattooEntries.TABLE_NAME, null, value);
                        }
                        catch(SQLiteConstraintException e){

                            e.printStackTrace();
                        }

                        if(_id != -1){

                            numInserted++;
                        }
                    }

                    if(numInserted > 0){

                        db.setTransactionSuccessful();
                    }
                }
                finally{

                    db.endTransaction();
                }

                if(numInserted > 0){

                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return numInserted;

            default:

                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        final SQLiteDatabase db = mDbHandler.getWritableDatabase();
        int numUpdated = 0;
        final int match = sUriMatcher.match(uri);



        if(contentValues == null){

            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(match){

            case TATTOO: {

                numUpdated = db.update(TattooContract.TattooEntries.TABLE_NAME,
                        contentValues,
                        TattooContract.TattooEntries._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});

                break;
            }

            default:{

                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if(numUpdated > 0){

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}