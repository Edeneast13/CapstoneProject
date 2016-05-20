package com.brianroper.tattome;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ImageView mFullImageView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mFullImageView = (ImageView)root.findViewById(R.id.full_tattoo_imageview);

        populateImageWithIntent();

        return root;
    }

    public void populateImageWithIntent(){

        Intent intent = getActivity().getIntent();
        String url = intent.getStringExtra("url");

        Picasso.with(getActivity()).load(url)
                .placeholder(R.drawable.tattooplaceholder)
                .into(mFullImageView);
    }
}
