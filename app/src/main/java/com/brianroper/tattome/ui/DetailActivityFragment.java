package com.brianroper.tattome.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.brianroper.tattome.util.BitmapConvertTask;
import com.brianroper.tattome.util.ByteArrayConvertTask;
import com.brianroper.tattome.util.DbBitmapUtil;
import com.brianroper.tattome.util.NetworkTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ImageView mFullImageView;
    private FloatingActionButton mFloatingActionButton;
    private String mTitle;
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

        if(NetworkTest.activeNetworkCheck(getActivity()) == true) {

            populateImageWithIntent();
            setFloatingActionButton();
            setDefaultFabImageResource();
        }else{

            Toast.makeText(getActivity(), getResources().getString(R.string.no_network),
                    Toast.LENGTH_LONG).show();
        }

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
        else{

            String stringBytes = sharedPreferences.getString("bytes", null);
            bytes = Base64.decode(stringBytes, Base64.DEFAULT);

            Bitmap image = convertByteArrayToBitmapAsync(bytes);

            if(image != null){

                mFullImageView.setImageBitmap(image);
            }
            else{

            }
        }

        String title = intent.getStringExtra("title");
        String bytesArray = sharedPreferences.getString("bytes", null);

        if(title.contains("&amp;")){

            title = title.replace("&amp;", "and");
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

                    c.close();

                    if (title.equals(mTitle)) {

                        mFloatingActionButton.setImageResource(R.drawable.starempty);

                        sqLiteDatabase = dbHandler.getWritableDatabase();

                        sqLiteDatabase.delete("favorites", "title == " + "\"" + mTitle + "\"", null);

                        sqLiteDatabase.close();

                        Toast.makeText(getActivity(),
                                mTitle + " " +
                                        getString(R.string.favorites_remove_toast),
                                Toast.LENGTH_LONG).show();
                    } else if (!(title.equals(mTitle))) {

                        mFloatingActionButton.setImageResource(R.drawable.starfull);

                        sqLiteDatabase = dbHandler.getWritableDatabase();

                        ImageView mPosterRef = mFullImageView;
                        Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                        byte[] posterByteArray = convertBitmapToByteArrayAsync(posterBitmap);

                        ContentValues values = new ContentValues();
                        values.put("title", mTitle);
                        values.put("image", posterByteArray);

                        sqLiteDatabase.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                        sqLiteDatabase.close();

                        Toast.makeText(getActivity(),
                                mTitle + " " +
                                        getResources().getString(R.string.favorites_saved),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    sqLiteDatabase = dbHandler.getWritableDatabase();

                    ImageView mPosterRef = mFullImageView;
                    Bitmap posterBitmap = DbBitmapUtil.convertImageViewToBitmap(mPosterRef);
                    byte[] posterByteArray = convertBitmapToByteArrayAsync(posterBitmap);

                    ContentValues values = new ContentValues();
                    values.put("title", mTitle);
                    values.put("image", posterByteArray);

                    sqLiteDatabase.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    sqLiteDatabase.close();

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
        String title ="";

        if(c.moveToFirst()!=false){

            c.moveToFirst();
            int titleIndex = c.getColumnIndex("title");
            title = c.getString(titleIndex);
            c.close();
            db.close();
        }

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

    public byte[] convertBitmapToByteArrayAsync(Bitmap bitmap){

        BitmapConvertTask task = new BitmapConvertTask();

        byte[] bytes = new byte[0];

        try {
            bytes = task.execute(bitmap).get();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public Bitmap convertByteArrayToBitmapAsync(byte[] bytes){

        ByteArrayConvertTask task = new ByteArrayConvertTask();

        Bitmap bitmap = null;

        try{
            bitmap = task.execute(bytes).get();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }

        return bitmap;
    }
}
