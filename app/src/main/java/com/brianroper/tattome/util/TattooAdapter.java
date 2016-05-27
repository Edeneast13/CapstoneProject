package com.brianroper.tattome.util;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brianroper.tattome.R;
import com.brianroper.tattome.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by brianroper on 5/18/16.
 */
public class TattooAdapter extends RecyclerView.Adapter<TattooAdapter.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<String> mUrlList = new ArrayList<String>();
    private ArrayList<String> mTitleList = new ArrayList<String>();

    public TattooAdapter(Context context, ArrayList<String> urlList, ArrayList<String> titleList) {

        mContext = context;
        mUrlList = urlList;
        mTitleList = titleList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public TattooAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View cardView = mLayoutInflater.inflate(
                R.layout.list_image_item,parent, false);

        final ViewHolder viewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                int position = viewHolder.getAdapterPosition();
                String url = mUrlList.get(position);
                intent.putExtra("url", url);
                Log.i("StringUrl: ", url);
                String title = mUrlList.get(position);
                intent.putExtra("title", title);
                Log.i("StringTitle: ", title);
                mContext.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       holder.setImageView(mContext, mUrlList.get(position));
        Log.i("POSITION: ", mUrlList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }

    /*View Holder */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public CardView mCardView;

        public ViewHolder(View cardView) {
            super(cardView);

            mCardView = (CardView)cardView.findViewById(R.id.card_view);
            mImageView = (ImageView)cardView.findViewById(R.id.card_imageview);
        }

        public void setImageView(Context context, String url){

            Picasso.with(context).load(url)
                    .placeholder(R.drawable.tattooplaceholder)
                    .fit()
                    .centerCrop()
                    .into(mImageView);
        }
    }
}
