package com.example.jagath.notesandtasks;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmRemainderProvider extends ContentProvider {
    public static final String LOG_TAG=AlarmRemainderProvider.class.getSimpleName();
    public static final int REMAINDER=100;
    public static final int REMAINDER_ID=101;
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AlarmRemainderContract.CONTENT_AUTHORITY,AlarmRemainderContract.PATH_VEHICLE,REMAINDER);
        sUriMatcher.addURI(AlarmRemainderContract.CONTENT_AUTHORITY,AlarmRemainderContract.PATH_VEHICLE+"/#",REMAINDER_ID);
    }
    private AlarmRemainderDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper=new AlarmRemainderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database=mDbHelper.getReadableDatabase();
        Cursor cursor=null;
        int match=sUriMatcher.match(uri);
        switch (match){
            case REMAINDER:
                cursor=database.query(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case REMAINDER_ID:
                selection=AlarmRemainderContract.AlarmRemainderEntry._ID + "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case REMAINDER:
                return AlarmRemainderContract.AlarmRemainderEntry.CONTENT_LIST_TYPE;
            case REMAINDER_ID:
                return AlarmRemainderContract.AlarmRemainderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI "+ uri +" with match "+ match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case REMAINDER:
                return insertRemainder(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is Not supported for "+ uri);
        }
    }
    private Uri insertRemainder(Uri uri, ContentValues values){
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        long id=database.insert(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,null,values);
        if (id==-1){
            Log.e(LOG_TAG,"Failed To insert row for "+ uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match=sUriMatcher.match(uri);
        switch (match){
            case REMAINDER:
                rowsDeleted=database.delete(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case REMAINDER_ID:
                selection= AlarmRemainderContract.AlarmRemainderEntry._ID + "=?";
                selectionArgs=new String []{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=database.delete(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not Supported for "+uri);
        }
        if (rowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case REMAINDER:
               return  updateRemainder(uri,values,selection,selectionArgs);
            case REMAINDER_ID:
                selection= AlarmRemainderContract.AlarmRemainderEntry._ID + "=?";
                selectionArgs=new String []{String.valueOf(ContentUris.parseId(uri))};
                return  updateRemainder(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update not Supported for "+uri);
        }
    }
    private int updateRemainder(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        if(values.size()==0)
            return 0;
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowUpdated=database.update(AlarmRemainderContract.AlarmRemainderEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdated;
    }
}
