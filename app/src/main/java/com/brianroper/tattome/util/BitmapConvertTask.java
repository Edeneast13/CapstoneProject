package com.brianroper.tattome.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by brianroper on 5/28/16.
 */
public class BitmapConvertTask extends AsyncTask<Bitmap, Void, byte[]> {
    @Override
    protected byte[] doInBackground(Bitmap... params) {

        byte[] bytes = DbBitmapUtil.convertBitmapToByteArray(params[0]);

        return bytes;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }
}
