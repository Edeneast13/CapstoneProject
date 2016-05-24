package com.brianroper.tattome;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TattooAdapter mAdapter;
    final String BASE_TATTOO_URL = "www.tattooideas247.com";
    final String URL_SCHEME = "http";
    private ArrayList<String> mUrlList = new ArrayList<String>();

    public ListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.tattoo_list);

        splitPage();

        TattooAdapter adapter = new TattooAdapter(getActivity(), mUrlList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    public String getTattooUrls(){

        String searchParam ="";

        searchParam = getActivity().getIntent().getStringExtra("category");

        String result = "";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME);
        builder.authority(BASE_TATTOO_URL);

        if(!(searchParam.equals(null))){

            if(searchParam.equals("featured")){


            }
            else{

                Log.i("Intent Extra: ", searchParam);
                //builder.appendPath(searchParam);
            }
        }

        String url = builder.build().toString();

        TattooTask tattooTask = new TattooTask();
        try {

            result = tattooTask.execute(url).get();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null){

            return result;

        }
        Log.i("Url: ", result);
        return null;
    }

    public void splitPage(){

        String data = getTattooUrls();

        String[] splitData = data.split("<ul class=\"mcol\">");

        Pattern tattooPattern = Pattern.compile("src=\"(.*?)\"");
        Matcher tattooMatcher = tattooPattern.matcher(splitData[1]);

        while(tattooMatcher.find()){

            mUrlList.add(tattooMatcher.group(1));
        }

        for (int i = 0; i < mUrlList.size(); i++) {

            Log.i("URL: ", mUrlList.get(i));
        }
    }
}
