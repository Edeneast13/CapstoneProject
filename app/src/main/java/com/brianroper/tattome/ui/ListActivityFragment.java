package com.brianroper.tattome.ui;

import android.content.ContentValues;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.Tattoo;
import com.brianroper.tattome.database.TattooContract;
import com.brianroper.tattome.util.NetworkTest;
import com.brianroper.tattome.util.TattooAdapter;
import com.brianroper.tattome.rest.TattooTask;
import com.brianroper.tattome.util.TattooCursorAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    final String BASE_TATTOO_URL = "www.tattooideas247.com";
    final String URL_SCHEME = "http";
    private ArrayList<String> mUrlList = new ArrayList<String>();
    private ArrayList<String> mTitleList = new ArrayList<String>();

    private static final int CURSOR_LOADER_ID = 0;
    private Tattoo[] tattoos;
    private TattooCursorAdapter mTattooCursorAdapter;

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

    /* Splits the page to remove useless code from url */
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

        /*Removes unwanted images from last two positions of the list */

        int listSize = mUrlList.size();

        if(listSize >= 15){

            mUrlList.remove(16);
            mUrlList.remove(15);
        }

        if(listSize < 15){

            int removeListLast = listSize - 1;
            int removeSecondLast = listSize - 2;
            mUrlList.remove(removeListLast);
            mUrlList.remove(removeSecondLast);
        }
    }

    public void insertData(){

        ContentValues[] tattooArray = new ContentValues[tattoos.length];
        //Loop through static array of Tattoos, add each to an instance on ContentValues
        //in the array ContentValues

        for(int i = 0; i < tattoos.length; i++){

            tattooArray[i] = new ContentValues();
            tattooArray[i].put(TattooContract.TattooEntries.COLUMN_IMAGE, tattoos[i].getTattooUrl().toString());

            tattooArray[i].put(TattooContract.TattooEntries.COLUMN_TITLE, tattoos[i].getTitle());
        }

        //bulkInsert our ContentValues array
        getActivity().getContentResolver().bulkInsert(TattooContract.TattooEntries.CONTENT_URI, tattooArray);
    }

    /*Loader methods */

    //attach loader to our tattoo database query
    //run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), TattooContract
                .TattooEntries.CONTENT_URI,
                null, null, null ,null);
    }

    //set the cursor in out CursorAdapter once the cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(mTattooCursorAdapter != null){

            mTattooCursorAdapter.swapCursor(data);
        }
    }

    //reset CursorAdapter on Loader reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if(mTattooCursorAdapter != null){

            mTattooCursorAdapter.swapCursor(null);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Cursor c = getActivity().getContentResolver()
                .query(TattooContract.TattooEntries.CONTENT_URI,
                        new String[]{TattooContract.TattooEntries._ID},
                        null,
                        null,
                        null);

        if(c.getCount() == 0){

            insertData();
        }

        //initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
