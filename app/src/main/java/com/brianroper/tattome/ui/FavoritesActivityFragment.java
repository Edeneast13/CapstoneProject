package com.brianroper.tattome.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.database.Favorites;
import com.brianroper.tattome.util.BitmapConvertTask;
import com.brianroper.tattome.util.ByteArrayConvertTask;
import com.brianroper.tattome.util.DbBitmapUtil;
import com.brianroper.tattome.util.FavoritesAdapter;
import com.brianroper.tattome.util.NetworkTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private ArrayList<Uri> mDownloadUriList = new ArrayList<Uri>();
    private ArrayList<Favorites> mFavoritesList = new ArrayList<Favorites>();

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

        Boolean syncFavs = mSharedPreferences.getBoolean("firebaseCheckbox", false);

        boolean dbTest = populateViewFromDb(syncFavs);

        if(dbTest == false && NetworkTest.activeNetworkCheck(getActivity()) == true){

           if(mFavoritesList == null){

               getFavoritesFromFirebaseDatabase();
               populateViewWithFirebaseStorage(mFavoritesList);
           }
            else{

               populateViewWithFirebaseStorage(mFavoritesList);
           }
        }

        return root;
    }

    public boolean populateViewFromDb(Boolean syncFavs){

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

            Bitmap[] bitmaps = new Bitmap[mBitmapsFromDb.size()];

            bitmaps = mBitmapsFromDb.toArray(bitmaps);

            FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), getId(), bitmaps, null);

            mGridView.setAdapter(adapter);

            final Bitmap[] finalImageArray = bitmaps;

            Bitmap bitmap = finalImageArray[0];

            byte[] bytes = convertBitmapToByteArrayAsync(bitmap);

            gridviewClickListener();
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.favorites_null),
                    Toast.LENGTH_LONG).show();

            return false;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return true;
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

    public void storeFavoriteInFirebaseStorage(String user, byte[] image, final String title){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(FIREBASE_BUCKET);

        StorageReference userReference = storageReference.child(user);

        StorageReference imageReference = userReference.child(title);

        UploadTask uploadTask = imageReference.putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                Log.i("Upload: ", "failed");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUri = taskSnapshot.getDownloadUrl();
                Log.i("Upload: ", "success");
                Log.i("URI: ", downloadUri.toString());

                Favorites favorite = new Favorites();
                favorite.setTitle(title);
                favorite.setTattooUrl(downloadUri);

                mFavoritesList.add(favorite);
            }
        });

        setFavoritesToFirebaseDatabase();
    }

    public void populateViewWithFirebaseStorage(ArrayList<Favorites> favorites){

        for (int i = 0; i < mFavoritesList.size(); i++) {

            Uri url = mFavoritesList.get(i).getTattooUrl();
            mDownloadUriList.add(url);
        }

        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), getId(), null, mDownloadUriList);

        mGridView.setAdapter(adapter);

        gridviewClickListener();
    }

    public void getFavoritesFromFirebaseDatabase(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference(userRoot[0]);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Favorites favorites = dataSnapshot.getValue(Favorites.class);
                mFavoritesList.add(favorites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setFavoritesToFirebaseDatabase(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference(userRoot[0]);

        for (int i = 0; i < mFavoritesList.size(); i++) {

            dbRef.setValue(mFavoritesList.get(i));
        }
    }
}
