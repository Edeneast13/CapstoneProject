package com.brianroper.tattome.service;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.util.DbBitmapUtil;

/**
 * Created by brianroper on 6/7/16.
 */
public class TattooFavoritesService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        return new RemoteViewsFactory() {

            private Cursor cursor;
            private String title;
            private Bitmap image;
            private byte[] imageBytes;
            private SQLiteDatabase db;
            private DbHandler dbHandler;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                final long callingIdentity = Binder.clearCallingIdentity();

                dbHandler = new DbHandler(getApplicationContext());

                db = dbHandler.getReadableDatabase();

                cursor = db.rawQuery("SELECT * FROM favorites", null);

                Binder.restoreCallingIdentity(callingIdentity);
            }

            @Override
            public void onDestroy() {

                cursor.close();
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_image_item);

                imageBytes = cursor.getBlob(cursor.getColumnIndex("image"));
                title = cursor.getString(cursor.getColumnIndex("title"));
                image = DbBitmapUtil.convertByteArrayToBitmap(imageBytes);
                remoteViews.setImageViewBitmap(R.layout.list_image_item, image);

                final Intent intent = new Intent();
                intent.putExtra("title", title);

                remoteViews.setOnClickFillInIntent(R.layout.list_image_item, intent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
