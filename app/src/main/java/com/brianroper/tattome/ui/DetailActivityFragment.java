package com.brianroper.tattome.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.database.Favorites;
import com.brianroper.tattome.util.DbBitmapUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ImageView mFullImageView;
    private FloatingActionButton mFloatingActionButton;
    private String mTitle;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String mUrl= "";
    private TextView mTitleTextView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mFullImageView = (ImageView)root.findViewById(R.id.full_tattoo_imageview);
        mFloatingActionButton = (FloatingActionButton) root.findViewById(R.id.fav_fab);
        mTitleTextView = (TextView)root.findViewById(R.id.detail_textview);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String[] tokens = user.getEmail().split("@");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(tokens[0]);

        populateImageWithIntent();
        setFloatingActionButton();
        setDefaultFabImageResource();

        return root;
    }

    public void populateImageWithIntent(){

        byte[] bytes;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        Intent intent = getActivity().getIntent();

        if(intent.getStringExtra("url") != null){

            mUrl = intent.getStringExtra("url");

            Picasso.with(getActivity()).load(mUrl)
                    .placeholder(R.drawable.tattooplaceholder)
                    .into(mFullImageView);
        }

        String title = intent.getStringExtra("title");
        String bytesArray = sharedPreferences.getString("bytes", null);

        if(bytesArray != null){

            Log.i("Shared Pref: ", "true");

            String stringBytes = sharedPreferences.getString("bytes", null);
            bytes = Base64.decode(stringBytes, Base64.DEFAULT);

            Bitmap image = DbBitmapUtil.convertByteArrayToBitmap(bytes);

            if(image != null){

                mFullImageView.setImageBitmap(image);
            }
            else{

            }
        }

        mTitle = title;
        mTitleTextView.setText(mTitle);

    }

    public void setFloatingActionButton(){

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                        Favorites favorites = new Favorites();
                        favorites.setTattooUrl(mUrl);
                        mDatabaseReference.setValue(favorites);

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
