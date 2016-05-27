package com.brianroper.tattome.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by brianroper on 5/27/16.
 */
public class DbBitmapUtil {

    public static byte[] convertBitmapToByteArray(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

        return stream.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] image){

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        return bitmap;
    }

    public static Bitmap convertImageViewToBitmap(ImageView image){

        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = image.getDrawingCache();

        return bitmap;
    }
}
