package com.brianroper.tattome.util;

import android.content.Context;
import android.graphics.Bitmap;
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

/**
 * Created by brianroper on 5/28/16.
 */
public class FavoritesAdapter extends ArrayAdapter {

    private Context mContext;
    private int mId;
    private Bitmap[] mUrls;
    private LayoutInflater mLayoutInflater;
    private ImageView mImageView;

    public FavoritesAdapter(Context context, int id, Bitmap[] urls){

        super(context, R.layout.favorites_gridview, urls);

        this.mContext = context;
        this.mId = id;
        this.mUrls = urls;

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
        mImageView.setImageBitmap(mUrls[position]);

        return mImageView;
    }
}
