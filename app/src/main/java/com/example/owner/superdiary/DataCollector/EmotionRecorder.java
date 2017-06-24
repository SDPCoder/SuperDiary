package com.example.owner.superdiary.DataCollector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.owner.superdiary.Utils.MyDBHelper;
import com.example.owner.superdiary.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class EmotionRecorder extends AppWidgetProvider {
    public static final String HAPPY_CLICKED = "com.example.owner.superdiary.action.EmotionRecorder.HAPPY";
    public static final String NORMAL_CLICKED = "com.example.owner.superdiary.action.EmotionRecorder.NORMAL";
    public static final String SAD_CLICKED = "com.example.owner.superdiary.action.EmotionRecorder.SAD";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_emotionrecorder);

        setOnClickListeners(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private void showNotification(Context context, String text) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("超级日记")
                .setContentText(text)
                .setTicker(text)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.appicon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.appicon))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
                .setWhen(System.currentTimeMillis());
        Notification notify = builder.build();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notify);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("EmotionRecorder", intent.getAction());
        if (HAPPY_CLICKED.equals(intent.getAction())) {
            Log.i("EmotionRecorder", "Happy");
            showNotification(context, "心情：快乐记录成功");
            Toast.makeText(context, "心情：快乐\n记录成功", Toast.LENGTH_SHORT).show();


            Map<String, String> table_header = new HashMap<>();
            table_header.put("emotion", "INTEGER");
            MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "emotion", table_header,null, 1);
            Map<String, Object> record = new HashMap<>();
            record.put("emotion", 100);
            mydb.insertNow(record);
            mydb.close();
        }

        if (NORMAL_CLICKED.equals(intent.getAction())) {
            Log.i("EmotionRecorder", "NORMAL");
            showNotification(context, "心情：普通记录成功");
            Toast.makeText(context, "心情：普通\n记录成功", Toast.LENGTH_SHORT).show();


            Map<String, String> table_header = new HashMap<>();
            table_header.put("emotion", "INTEGER");
            MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "emotion", table_header,null, 1);
            Map<String, Object> record = new HashMap<>();
            record.put("emotion", 0);
            mydb.insertNow(record);
            mydb.close();
        }

        if (SAD_CLICKED.equals(intent.getAction())) {
            Log.i("EmotionRecorder", "SAD");
            showNotification(context, "心情：悲伤记录成功");
            Toast.makeText(context, "心情：悲伤\n记录成功", Toast.LENGTH_SHORT).show();


            Map<String, String> table_header = new HashMap<>();
            table_header.put("emotion", "INTEGER");
            MyDBHelper mydb = new MyDBHelper(context, "superdiary_test001.db", "emotion", table_header,null, 1);
            Map<String, Object> record = new HashMap<>();
            record.put("emotion", -100);
            mydb.insertNow(record);
            mydb.close();
        }
    }

    private static void setOnClickListeners(Context context, RemoteViews views) {
        views.setOnClickPendingIntent(R.id.widget_happy,
                getPendingSelfIntent(context, HAPPY_CLICKED));
        views.setOnClickPendingIntent(R.id.widget_normal,
                getPendingSelfIntent(context, NORMAL_CLICKED));
        views.setOnClickPendingIntent(R.id.widget_sad,
                getPendingSelfIntent(context, SAD_CLICKED));
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, EmotionRecorder.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}