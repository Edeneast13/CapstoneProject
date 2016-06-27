package com.brianroper.tattome.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.TattooContract;

import java.util.concurrent.ExecutionException;

/**
 * Created by brianroper on 6/23/16.
 */
public class TattooCursorAdapter extends CursorAdapter {

    private Context mContext;
    private static int sLoaderID;

    public static class ViewHolder{

        public final ImageView imageview;
        public final CardView cardview;

        public ViewHolder(View view){

            imageview = (ImageView) view.findViewById(R.id.card_imageview);
            cardview = (CardView) view.findViewById(R.id.card_view);
        }
    }

    public TattooCursorAdapter(Context context, Cursor c, int flags, int loaderID){

        super(context, c, flags);
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){

        int layoutId = R.layout.list_image_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int imageIndex = cursor.getColumnIndex(TattooContract.TattooEntries.COLUMN_IMAGE);
        final byte[] imageBytes = cursor.getBlob(imageIndex);

        Bitmap imageBitmap = convertByteArrayToBitmapAsync(imageBytes);

        viewHolder.imageview.setImageBitmap(imageBitmap);
    }

    public Bitmap convertByteArrayToBitmapAsync(byte[] bytes){

        ByteArrayConvertTask task = new ByteArrayConvertTask();

        Bitmap bitmap = null;

        try{
            bitmap = task.execute(bytes).get();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }

        return bitmap;
    }
}
