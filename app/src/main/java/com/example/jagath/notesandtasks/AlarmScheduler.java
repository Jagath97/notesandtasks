package com.example.jagath.notesandtasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmScheduler {

    /**
     * Schedule a reminder alarm at the specified time for the given task
     * @param context
     * @param alarmTime
     * @param remainderTask
     */
    public void setAlarm(Context context,long alarmTime, Uri remainderTask){
        AlarmManager manager=AlarmManagerProvider.getAlarmManager(context);
        PendingIntent operation=ReminderAlarmService.getReminderPendingIntent(context,remainderTask);

        if(Build.VERSION.SDK_INT>=23)
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,alarmTime,operation);
        else if(Build.VERSION.SDK_INT>=19)
            manager.setExact(AlarmManager.RTC_WAKEUP,alarmTime,operation);
        else
            manager.set(AlarmManager.RTC_WAKEUP,alarmTime,operation);
    }
    public void setRepeatAlarm(Context context,long alarmTime,Uri remainderTask,long RepeatTime){
        AlarmManager manager=AlarmManagerProvider.getAlarmManager(context);
        PendingIntent operation=ReminderAlarmService.getReminderPendingIntent(context,remainderTask);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,alarmTime,RepeatTime,operation);
    }
    public void cancelAlarm(Context context,Uri remainderTask){
        AlarmManager alarmManager=AlarmManagerProvider.getAlarmManager(context);

        PendingIntent operation=ReminderAlarmService.getReminderPendingIntent(context,remainderTask);
        alarmManager.cancel(operation);
    }
}
