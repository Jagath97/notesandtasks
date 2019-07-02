package com.example.jagath.notesandtasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by jagath on 28/02/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG="DatabaseHelper";
    private static final String TABLENAME="User_Notes_Table";
    private static final String COL1="ID";
    private static final String COL2="Title";
    private static final String COL3="Content";
    private static final String COL4="Lastmodified";
    private static final String COL5="Pin";
    private static final String COL6="Remdate";
    private static final String COL7="Remtime";
    private static final String COL8="RemStatus";
    private static final String COL9="Notestatus";
    private static final String COL10="Ncolor";
    public DataBaseHelper(Context context) {
        super(context,TABLENAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String create="CREATE TABLE " + TABLENAME + " (" +
            COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL2 + " TEXT," +
            COL3 + " TEXT," +
            COL4 + " TEXT," +
            COL5 + " TEXT," +
            COL10 + " INTEGER," +
            COL9 + " TEXT)";
    db.execSQL(create);
    //createNewNote("Welcome Note","Click 'New Note' to Add ","Null","no",0,"active");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+TABLENAME);
        onCreate(db);
    }

    public boolean createNewNote(String title,String content,String lastmod,String pin,int color,String nstatus){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL2,title);
        contentValues.put(COL3,content);
        contentValues.put(COL4,lastmod);
        contentValues.put(COL5,pin);
        contentValues.put(COL10,color);

        contentValues.put(COL9,nstatus);
        Log.d(TAG,"CreateNewNode:Adding"+title+"\n"+content+"to"+TABLENAME);
        long res=database.insert(TABLENAME,null,contentValues);
        return res != -1;
    }

    public Cursor getNoteData(){
        SQLiteDatabase database=this.getReadableDatabase();
        String query="SELECT * FROM " + TABLENAME;
        return database.rawQuery(query,null);
    }
    public void deleteNote(int id){
        SQLiteDatabase database=this.getWritableDatabase();
        String query="DELETE FROM " + TABLENAME + " WHERE " + COL1 + " = " +id;
        database.execSQL(query);
    }
    public void archiveNote(int id){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL9,"archived");
        database.update(TABLENAME,contentValues,"ID=" + id,null);
    }
    public void unarchiveNote(int id){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL9,"active");
        database.update(TABLENAME,contentValues,"ID=" + id,null);
    }
    public boolean updateNote(int id,String title,String content,String modified,int color){
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL2,title);
        contentValues.put(COL3,content);
        contentValues.put(COL4,modified);
        contentValues.put(COL10,color);
        SQLiteDatabase database=this.getWritableDatabase();
        int res=database.update(TABLENAME,contentValues,"ID="+ id,null);
        return res > 0;
    }
}
