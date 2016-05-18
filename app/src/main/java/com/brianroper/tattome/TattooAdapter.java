package com.brianroper.tattome;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by brianroper on 5/18/16.
 */
public class TattooAdapter extends RecyclerView.Adapter<TattooAdapter.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<String> mUrlList = new ArrayList<String>();

    public TattooAdapter(Context context, ArrayList<String> list) {

        mContext = context;
        mUrlList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public TattooAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView cardView = (CardView)mLayoutInflater.inflate(
                R.layout.list_image_item,parent, false);

        TattooAdapter.ViewHolder viewHolder = new ViewHolder(cardView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       holder.setImageView(mContext, mUrlList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }

    /*View Holder */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public CardView mCardView;
        public TattooAdapter mRecyclerView;
        public Context mContext;

        public ViewHolder(CardView cardView) {
            super(cardView);

            mCardView = cardView;
            mImageView = (ImageView)mCardView.findViewById(R.id.card_imageview);
        }

        public void setImageView(Context context, String url){
            Picasso.with(context).load(url).fit().into(mImageView);
        }
    }
}
