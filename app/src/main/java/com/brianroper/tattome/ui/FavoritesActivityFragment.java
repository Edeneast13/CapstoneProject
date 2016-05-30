package com.brianroper.tattome.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment {

    private GridView mGridView;
    private ArrayList<Bitmap> mBitmapsFromDb = new ArrayList<Bitmap>();
    private ArrayList<String> mTitleList = new ArrayList<String>();
    private String mTitle;
    private String mImageBytes;

    public FavoritesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.favorites_gridview, container, false);

        mGridView = (GridView)root.findViewById(R.id.favorites_grid);

        populateViewFromDb();

        return root;
    }

    public void populateViewFromDb(){

        String title;
        Bitmap imageBitmap;

        DbHandler dbHandler = new DbHandler(getContext());

        SQLiteDatabase sqLiteDatabase = dbHandler.getReadableDatabase();

        try {

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM favorites", null);

            int titleIndex = cursor.getColumnIndex("title");
            cursor.moveToFirst();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                title = cursor.getString(titleIndex);
                mTitleList.add(title);
            }

            int imageIndex = cursor.getColumnIndex("image");
            cursor.moveToFirst();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                 imageBitmap = convertByteArrayToBitmapAsync(cursor.getBlob(imageIndex));

                 mBitmapsFromDb.add(imageBitmap);
            }

            cursor.close();
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
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());

                Bitmap bitmap = mBitmapsFromDb.get(position);

                byte[] bytes = convertBitmapToByteArrayAsync(bitmap);

                String stringBytes = Base64.encodeToString(bytes, Base64.DEFAULT);

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                intent.putExtra("title", mTitle);
                sharedPreferences.edit().putString("bytes", stringBytes).commit();

                startActivity(intent);
            }
        });
    }

    public byte[] convertBitmapToByteArrayAsync(Bitmap bitmap){

        BitmapConvertTask task = new BitmapConvertTask();

        byte[] bytes = new byte[0];

        try {
            bytes = task.execute(bitmap).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
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
