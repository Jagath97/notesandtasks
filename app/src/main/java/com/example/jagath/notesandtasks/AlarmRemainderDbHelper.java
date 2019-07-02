package com.example.jagath.notesandtasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmRemainderDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="alarmreminder.db";
    private static final int DATABASE_VERSION=1;
    public AlarmRemainderDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE " + AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME + " (" +
                AlarmRemainderContract.AlarmRemainderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_DATE + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_TIME + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_NO + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_TYPE + " TEXT," +
                AlarmRemainderContract.AlarmRemainderEntry.KEY_ACTIVE + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
