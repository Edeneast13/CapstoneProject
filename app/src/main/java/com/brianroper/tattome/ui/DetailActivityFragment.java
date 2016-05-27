package com.brianroper.tattome.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.util.DbBitmapUtil;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ImageView mFullImageView;
    private FloatingActionButton mFloatingActionButton;
    private String mTitle;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mFullImageView = (ImageView)root.findViewById(R.id.full_tattoo_imageview);
        mFloatingActionButton = (FloatingActionButton) root.findViewById(R.id.fav_fab);

        populateImageWithIntent();
        setFloatingActionButton();
        setDefaultFabImageResource();

        return root;
    }

    public void populateImageWithIntent(){

        Intent intent = getActivity().getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");

        mTitle = title;

        Picasso.with(getActivity()).load(url)
                .placeholder(R.drawable.tattooplaceholder)
                .into(mFullImageView);
    }

    public void setFloatingActionButton(){

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("FAB", "clicked");

                DbHandler dbHandler = new DbHandler(getContext());
                SQLiteDatabase sqLiteDatabase;

                try {

                    sqLiteDatabase = dbHandler.getReadableDatabase();

                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM favorites WHERE title = \"" + mTitle + "\"", null);
                    c.moveToFirst();
                    int titleIndex = c.getColumnIndex("title");
                    String title = c.getString(titleIndex);

                    if (title.equals(mTitle)) {

                        mFloatingActionButton.setImageResource(R.drawable.starempty);

                        sqLiteDatabase = dbHandler.getWritableDatabase();

                        sqLiteDatabase.delete("favorites", "title == " + "\"" + mTitle + "\"", null);

                        Toast.makeText(getActivity(),
                                mTitle + " " +
                                        getString(R.string.favorites_remove_toast),
                                Toast.LENGTH_LONG).show();
                    } else if (!(title.equals(mTitle))) {

                        mFloatingActionButton.setImageResource(R.drawable.starfull);

                        sqLiteDatabase = dbHandler.getWritableDatabase();

                        ImageView mPosterRef = mFullImageView;
                        Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                        byte[] posterByteArray = DbBitmapUtil.convertBitmapToByteArray(posterBitmap);

                        ContentValues values = new ContentValues();
                        values.put("title", mTitle);
                        values.put("image", posterByteArray);
                        Log.i("POSTER BYTE ARRAY: ", posterByteArray.toString());

                        sqLiteDatabase.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        Toast.makeText(getActivity(),
                                mTitle + " " +
                                        getResources().getString(R.string.favorites_saved),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    sqLiteDatabase = dbHandler.getWritableDatabase();

                    ImageView mPosterRef = mFullImageView;
                    Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                    byte[] posterByteArray = DbBitmapUtil.convertBitmapToByteArray(posterBitmap);

                    ContentValues values = new ContentValues();
                    values.put("title", mTitle);
                    values.put("image", posterByteArray);
                    Log.i("POSTER BYTE ARRAY: ", posterByteArray.toString());

                    sqLiteDatabase.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    mFloatingActionButton.setImageResource(R.drawable.starfull);

                    Toast.makeText(getActivity(),
                            mTitle + " " +
                                    getResources().getString(R.string.favorites_saved),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setDefaultFabImageResource(){

        DbHandler dbHandler = new DbHandler(getContext());

        SQLiteDatabase db;

        db = dbHandler.getReadableDatabase();

        try{

        Cursor c = db.rawQuery("SELECT * FROM favorites WHERE title = \"" + mTitle + "\"", null);
        c.moveToFirst();
        int titleIndex = c.getColumnIndex("title");
        String title = c.getString(titleIndex);

        if (title.equals(mTitle)) {

            mFloatingActionButton.setImageResource(R.drawable.starfull);
        } else {

            mFloatingActionButton.setImageResource(R.drawable.starempty);
        }
    }
    catch(CursorIndexOutOfBoundsException e){
        e.printStackTrace();

        mFloatingActionButton.setImageResource(R.drawable.starempty);
    }
    }
}
