package com.brianroper.tattome.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.util.BitmapConvertTask;
import com.brianroper.tattome.util.ByteArrayConvertTask;
import com.brianroper.tattome.util.DbBitmapUtil;
import com.brianroper.tattome.util.FavoritesAdapter;
import com.brianroper.tattome.util.NetworkTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment {

    private GridView mGridView;
    private ArrayList<Bitmap> mBitmapsFromDb = new ArrayList<Bitmap>();
    private ArrayList<String> mTitleList = new ArrayList<String>();
    private ArrayList<byte[]> mByteFromDb = new ArrayList<byte[]>();
    private String mTitle;
    private SharedPreferences mSharedPreferences;
    private String[] userRoot;
    private final String FIREBASE_BUCKET = "gs://tattoo-b7ce6.appspot.com";

    public FavoritesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.favorites_gridview, container, false);

        mGridView = (GridView)root.findViewById(R.id.favorites_grid);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRoot = user.getEmail().split("@");

        populateViewFromDb();

        return root;
    }

    public void populateViewFromDb(){

        String title;
        Bitmap imageBitmap;

        DbHandler dbHandler = new DbHandler(getContext());

        SQLiteDatabase sqLiteDatabase = dbHandler.getReadableDatabase();

        try {

            Cursor imageCursor = sqLiteDatabase.rawQuery("SELECT * FROM favorites", null);

            int imageIndex = imageCursor.getColumnIndex("image");
            imageCursor.moveToFirst();

            for (imageCursor.moveToFirst(); !imageCursor.isAfterLast(); imageCursor.moveToNext()) {

                byte[] imageBytes = imageCursor.getBlob(imageIndex);
                imageBitmap = convertByteArrayToBitmapAsync(imageBytes);

                mBitmapsFromDb.add(imageBitmap);
                mByteFromDb.add(imageBytes);
            }

            imageCursor.close();

            Cursor titleCursor = sqLiteDatabase.rawQuery("SELECT * FROM favorites", null);

            int titleIndex = titleCursor.getColumnIndex("title");
            titleCursor.moveToFirst();

            for (titleCursor.moveToFirst(); !titleCursor.isAfterLast(); titleCursor.moveToNext()) {

                title = titleCursor.getString(titleIndex);
                mTitleList.add(title);
            }

            titleCursor.close();
            sqLiteDatabase.close();

            Boolean syncFavs = mSharedPreferences.getBoolean("firebaseCheckbox", false);

            if(NetworkTest.activeNetworkCheck(getActivity()) == true){

                if(syncFavs == true){

                    for (int i = 0; i < mByteFromDb.size(); i++) {

                        byte[] image = mByteFromDb.get(i);
                        String imageTitle = mTitleList.get(i);

                        storeFavoriteInFirebaseStorage(userRoot[0], image, imageTitle);
                    }
                }
                else{

                    Log.i("FirebaseStorage: ", "Not Active");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            Bitmap[] bitmaps = new Bitmap[mBitmapsFromDb.size()];

            bitmaps = mBitmapsFromDb.toArray(bitmaps);

            FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), getId(), bitmaps);

            mGridView.setAdapter(adapter);

            final Bitmap[] finalImageArray = bitmaps;

            Bitmap bitmap = finalImageArray[0];

            byte[] bytes = convertBitmapToByteArrayAsync(bitmap);

            gridviewClickListener();
        }
    }

    public void gridviewClickListener(){

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mTitle = mTitleList.get(position);

                Bitmap bitmap = mBitmapsFromDb.get(position);

                byte[] bytes = convertBitmapToByteArrayAsync(bitmap);

                String stringBytes = Base64.encodeToString(bytes, Base64.DEFAULT);

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                intent.putExtra("title", mTitle);
                mSharedPreferences.edit().putString("bytes", stringBytes).commit();

                startActivity(intent);
            }
        });
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

    public void storeFavoriteInFirebaseStorage(String user, byte[] image, String title){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(FIREBASE_BUCKET);

        StorageReference userReference = storageReference.child(user);

        StorageReference imageReference = userReference.child(title);

        UploadTask uploadTask = imageReference.putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                //TODO: Implement snackbar to notify user of failure
                //TODO: Allow for the user to retry

                Log.i("Upload: ", "failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUri = taskSnapshot.getDownloadUrl();
                Log.i("Upload: ", "success");
            }
        });
    }
}
