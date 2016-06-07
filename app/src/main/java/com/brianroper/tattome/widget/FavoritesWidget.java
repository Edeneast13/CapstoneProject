package com.brianroper.tattome.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.GridView;
import android.widget.RemoteViews;

import com.brianroper.tattome.R;
import com.brianroper.tattome.service.TattooFavoritesService;
import com.brianroper.tattome.ui.DetailActivity;
import com.brianroper.tattome.ui.ListActivity;

/**
 * Created by brianroper on 6/5/16.
 */
public class FavoritesWidget extends AppWidgetProvider {

      static void updateAppWidget(Context context, AppWidgetManager manager,
                                  int appWidgetId){

          CharSequence widgetText = context.getString(R.string.appwidget_text);

          //Construct the remoteviews object

          RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.favorites_widget);
          remoteViews.setTextViewText(R.id.appwidget_title, widgetText);
          remoteViews.setRemoteAdapter(R.id.favorites_widget_grid, new Intent(context, TattooFavoritesService.class));

          int currentApiVersion = Build.VERSION.SDK_INT;

          if(currentApiVersion > Build.VERSION_CODES.ICE_CREAM_SANDWICH){

              Intent intent = new Intent(context, DetailActivity.class);
              PendingIntent pendingIntent = TaskStackBuilder.create(context)
                      .addNextIntentWithParentStack(intent)
                      .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

              remoteViews.setPendingIntentTemplate(R.id.favorites_widget_grid, pendingIntent);
          }
          else{

              Intent intent = new Intent(context, ListActivity.class);
              PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
              remoteViews.setOnClickPendingIntent(R.id.appwidget_title, pendingIntent);
          }

          manager.updateAppWidget(appWidgetId, remoteViews);
      }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
