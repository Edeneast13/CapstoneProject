package com.brianroper.tattome.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by brianroper on 5/28/16.
 */
public class ByteArrayConvertTask extends AsyncTask<byte[], Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(byte[]... params) {

        Bitmap bitmap = DbBitmapUtil.convertByteArrayToBitmap(params[0]);

        return bitmap;
    }
}
