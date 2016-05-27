package com.brianroper.tattome.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.util.DbBitmapUtil;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public FavoritesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        mRecyclerView = (RecyclerView)root.findViewById(R.id.favorites_list);


        return root;
    }
}
