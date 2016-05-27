package com.brianroper.tattome.ui;

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

import com.brianroper.tattome.R;
import com.brianroper.tattome.util.TattooAdapter;
import com.brianroper.tattome.rest.TattooTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<String> mTitleList = new ArrayList<String>();

    public ListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.tattoo_list);

        splitPage();

        TattooAdapter adapter = new TattooAdapter(getActivity(), mUrlList, mTitleList);
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

        if(searchParam != null){

            if(searchParam.equals("featured")){

                Log.i("Intent Extra: ", searchParam);
            }
            else{

                Log.i("Intent Extra: ", searchParam);
                builder.appendPath(searchParam);
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

        String[] splitData = data.split("id=\"post-0\">");

        Pattern tattooPattern = Pattern.compile("src=\"(.*?)\"");
        Matcher tattooMatcher = tattooPattern.matcher(splitData[1]);

        while(tattooMatcher.find()){

            mUrlList.add(tattooMatcher.group(1));
        }

        for (int i = 0; i < mUrlList.size(); i++) {

            Log.i("URL: ", mUrlList.get(i));
        }

        Pattern titlePattern = Pattern.compile("title=\"(.*?)\"");
        tattooMatcher = titlePattern.matcher(splitData[1]);

        while(tattooMatcher.find()){

            mTitleList.add(tattooMatcher.group(1));
        }

        for (int i = 0; i < mTitleList.size(); i++) {
            Log.i("TITLE: ", mTitleList.get(i));
        }
    }
}