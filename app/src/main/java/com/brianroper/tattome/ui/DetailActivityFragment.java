package com.brianroper.tattome.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.brianroper.tattome.util.ImageviewConvertTask;
import com.brianroper.tattome.util.NetworkTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        setHasOptionsMenu(true);
        setFloatingActionButtonListener();

        if(NetworkTest.activeNetworkCheck(getActivity()) == true) {

            populateImageWithIntent();
            setDefaultFabImageResource();
        }else{

            Toast.makeText(getActivity(), getResources().getString(R.string.no_network),
                    Toast.LENGTH_LONG).show();
        }
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_share:

                shareContentWithIntent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    public void setFloatingActionButtonListener(){

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveTattooToDb();
            }
        });
    }

    public void saveTattooToDb(){

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

    public void shareContentWithIntent(){

        ImageviewConvertTask shareTask = new ImageviewConvertTask();
        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] imageviewBytes = null;
            Bitmap imageviewBitmap = shareTask.execute(mFullImageView).get();
            imageviewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byteArrayOutputStream.flush();
            imageviewBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            String imageviewString = imageviewBytes.toString();
            Uri imageviewUri = Uri.parse(imageviewString);

            Log.i("URI: ", imageviewUri.toString());

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, imageviewUri);
            sendIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share)));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
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
