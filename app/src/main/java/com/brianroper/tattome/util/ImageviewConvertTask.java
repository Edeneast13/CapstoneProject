package com.brianroper.tattome.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by brianroper on 6/2/16.
 */
public class ImageviewConvertTask extends AsyncTask<ImageView, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(ImageView... params) {

        Bitmap bitmap = DbBitmapUtil.convertImageViewToBitmap(params[0]);

        return bitmap;
    }
}
