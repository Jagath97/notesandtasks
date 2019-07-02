package com.example.jagath.notesandtasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by jagath on 26/03/2018.
 */
public class ReminderAlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public static final String CONTENT_AUTHORITY= "com.example.jagath.notesandtasks";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    private static final String TAG=ReminderAlarmService.class.getSimpleName();
    private static final int NOTICATION_ID=42;
    public static final String PATH_VEHICLE = "reminder-path";
    public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_VEHICLE);
    public ReminderAlarmService() {
        super(TAG);
    }

    public static PendingIntent getReminderPendingIntent(Context context, Uri uri){
        Intent action=new Intent(context,ReminderAlarmService.class);
        action.setData(uri);
        return PendingIntent.getService(context,0,action,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Uri uri=intent.getData();

        Intent action=new Intent(this,AddRemainderActivity.class);
        action.setData(uri);
       Cursor cursor=getContentResolver().query(CONTENT_URI,null,null,null,null);
        String description="";
        String id="";
        String str=uri.toString();
        String id1=str.substring(Math.max(str.length() - 2, 0));
        try {
            if(cursor!=null ) {
                while (cursor.moveToNext()) {
                    id = AlarmRemainderContract.getColumnString(cursor, AlarmRemainderContract.AlarmRemainderEntry._ID);
                    if (id.equals(id1))
                        description = AlarmRemainderContract.getColumnString(cursor, AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE);
                }
            }
        }
        finally {
            if(cursor!=null)
                cursor.close();
        }
        PendingIntent operation= TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Remainder")
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentIntent(operation)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setAutoCancel(true)
                .build();
        manager.notify(NOTICATION_ID,notification);
       /* Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("AlarmRemainder")
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setAutoCancel(true)
                .build();
        manager.notify(NOTICATION_ID,notification);*/
    }


}