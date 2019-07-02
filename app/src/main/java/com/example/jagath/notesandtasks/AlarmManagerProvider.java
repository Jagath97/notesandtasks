package com.example.jagath.notesandtasks;

import android.app.AlarmManager;
import android.content.Context;

/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmManagerProvider {
    private static final String TAG= AlarmManagerProvider.class.getSimpleName();
    private static AlarmManager sAlarmManager;
    public static synchronized void injectAlarmManager(AlarmManager alarmManager){
        if (sAlarmManager!=null){
            throw new IllegalStateException("AlarmManager Already Set");}
        sAlarmManager=alarmManager;
    }
    static synchronized AlarmManager getAlarmManager(Context context){
        if (sAlarmManager==null){
            sAlarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);}
        return sAlarmManager;
    }
}
