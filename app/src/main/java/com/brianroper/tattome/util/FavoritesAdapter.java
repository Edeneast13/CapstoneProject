package com.brianroper.tattome.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brianroper.tattome.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by brianroper on 5/28/16.
 */
public class FavoritesAdapter extends ArrayAdapter {

    private Context mContext;
    private int mId;
    private Bitmap[] mUrls;
    private LayoutInflater mLayoutInflater;
    private ImageView mImageView;
    private ArrayList<Uri> mUriArrayList = new ArrayList<Uri>();

    public FavoritesAdapter(Context context, int id, Bitmap[] urls, ArrayList<Uri> uriList){

        super(context, R.layout.favorites_gridview, urls);

        this.mContext = context;
        this.mId = id;
        this.mUrls = urls;
        this.mUriArrayList = uriList;

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){

            convertView = mLayoutInflater.inflate(R.layout.gridview_item, parent, false);
            mImageView = new ImageView(mContext);
        }
        else{
            mImageView = (ImageView)convertView;
        }

        /*GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        mImageView.setLayoutParams(layoutParams);*/

        mImageView.setAdjustViewBounds(true);

        if(mUrls != null){

            mImageView.setImageBitmap(mUrls[position]);
        }
        if(mUriArrayList != null && NetworkTest.activeNetworkCheck(mContext)){

            for (int i = 0; i < mUriArrayList.size(); i++) {

                Picasso.with(mContext)
                        .load(mUriArrayList.get(i))
                        .placeholder(R.drawable.tattooplaceholder)
                        .fit()
                        .centerCrop()
                        .into(mImageView);
            }
        }

        return mImageView;
    }
}
