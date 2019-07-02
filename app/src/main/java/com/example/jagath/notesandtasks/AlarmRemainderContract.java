package com.example.jagath.notesandtasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmRemainderContract {
    private AlarmRemainderContract(){}
    public static final String CONTENT_AUTHORITY= "com.example.jagath.notesandtasks";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_VEHICLE = "reminder-path";
    public static final class AlarmRemainderEntry implements BaseColumns{
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_VEHICLE);
        public static final String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +CONTENT_AUTHORITY+"/"+PATH_VEHICLE;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY+"/"+PATH_VEHICLE;

        public static final String TABLE_NAME="vehicles";

        public static final String _ID=BaseColumns._ID;
        public final static String KEY_TITLE="title";
        public final static String KEY_DATE="date";
        public final static String KEY_TIME="time";
        public final static String KEY_REPEAT="repeat";
        public final static String KEY_REPEAT_NO="repeat_no";
        public final static String KEY_REPEAT_TYPE="repeat_type";
        public final static String KEY_ACTIVE="active";

    }
    public static String getColumnString(Cursor cursor,String columName){
        return cursor.getString(cursor.getColumnIndex(columName));
    }


}
