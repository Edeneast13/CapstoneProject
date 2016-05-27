package com.brianroper.tattome.rest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by brianroper on 5/18/16.
 */
public class TattooTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {

        HttpURLConnection urlConnection = null;
        String result = null;
        BufferedReader bufferedReader = null;

        try {
            //"https://api.themoviedb.org/3/movie/550?api_key=a0a454fc960bf4f69fa0adf5e13161cf"

            URL url = new URL(urls[0]);//themoviedb.org/movie

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(inputStream);

            bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while((line = bufferedReader.readLine())!= null){

                stringBuffer.append(line + "/n");
            }

            while(stringBuffer.length() == 0){

                //empty string no reason to parse
                Log.i("StringBuffer", "Empty");
                return null;
            }

            result = stringBuffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            if (urlConnection != null) {

                urlConnection.disconnect();
            }
            if (bufferedReader != null) {

                try {
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try{
            return result;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(String s){

    }
}
