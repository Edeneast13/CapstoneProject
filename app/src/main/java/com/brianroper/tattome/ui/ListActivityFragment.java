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
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.util.NetworkTest;
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

        if(NetworkTest.activeNetworkCheck(getActivity()) == true){

            splitPage();

            TattooAdapter adapter = new TattooAdapter(getActivity(), mUrlList, mTitleList);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setHasFixedSize(true);

            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(sglm);
        }else{

            Toast.makeText(getActivity(), getResources().getString(R.string.no_network),
                    Toast.LENGTH_LONG).show();
        }
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

            }
            else{

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

        Pattern titlePattern = Pattern.compile("alt=\"(.*?)\"");
        Matcher titleMatcher = titlePattern.matcher(splitData[1]);

        while(titleMatcher.find()){

            mTitleList.add(titleMatcher.group(1));
        }
    }
}
