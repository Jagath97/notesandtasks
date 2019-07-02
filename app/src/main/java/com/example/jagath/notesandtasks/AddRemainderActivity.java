package com.example.jagath.notesandtasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;

import com.androidadvance.topsnackbar.TSnackbar;
import com.wdullaer.materialdatetimepicker.*;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import android.content.Loader;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by jagath on 26/03/2018.
 */

public class AddRemainderActivity extends AppCompatActivity implements
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_VEHICLE_LOADER=0;
    private Toolbar toolbar;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText,mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB;
    private Calendar mCalender;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Switch mRepeatSwitch;
    private String mTitle,mTime,mDate,mRepeat=" ",mRepeatNo="1",mRepeatType=" ",mActive;
    private Uri mCurrentRemainderUri;
    private boolean mVehicleHasChanged=false;

    public final static String KEY_TITLE="title_key";
    public final static String KEY_DATE="date_key";
    public final static String KEY_TIME="time_key";
    public final static String KEY_REPEAT="repeat_key";
    public final static String KEY_REPEAT_NO="repeat_no_key";
    public final static String KEY_REPEAT_TYPE="repeat_type_key";
    public final static String KEY_ACTIVE="active_key";


    public final static long milMinute=60000L;
    public final static long milHour=3600000L;
    public final static long milDay=86400000L;
    public final static long milWeek=604800000L;
    public final static long milMonth=2592000000L;

    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mVehicleHasChanged=true;
            return false;
        }
    };
    private int prefcolor;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_remainder);
        Intent intent=getIntent();
        toolbar=findViewById(R.id.toolbarr1);
        mCurrentRemainderUri=intent.getData();
        if(mCurrentRemainderUri==null){
            toolbar.setTitle("Add Remainder");
            invalidateOptionsMenu();
        }
        else {
            toolbar.setTitle("Edit Remainder");
            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER,null,this);
        }
        RelativeLayout relativeLayout1=findViewById(R.id.interval);
        relativeLayout1.setVisibility(View.INVISIBLE);
        mDateText=findViewById(R.id.date_picker_rem);
        mTimeText=findViewById(R.id.time_picker_rem);
        mFAB=findViewById(R.id.fab_save);
        mTitleText=findViewById(R.id.remainder_title);
        mRepeatSwitch=findViewById(R.id.switch_rem);
        /*
        mRepeatSwitch.setVisibility(View.INVISIBLE);
        mFAB.setVisibility(View.INVISIBLE);
        RelativeLayout relativeLayout=findViewById(R.id.switchd);
        relativeLayout.setVisibility(View.INVISIBLE);*/
        mActive="true";
        mRepeat="false";
        mRepeatNo=Integer.toString(1);
        mRepeatType="false";

        preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        prefcolor=preferences.getInt("color", Color.parseColor("#303F9F"));
        toolbar.setBackgroundColor(prefcolor);
        LinearLayout layout=findViewById(R.id.new_rem_lin_lay);
        layout.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCalender=Calendar.getInstance();
        mHour=mCalender.get(Calendar.HOUR_OF_DAY);
        mMinute=mCalender.get(Calendar.MINUTE);
        mYear=mCalender.get(Calendar.YEAR);
        mMonth=mCalender.get(Calendar.MONTH)+1;
        mDay=mCalender.get(Calendar.DATE);

        mDate=mDay + "/" +  mMonth + "/" +mYear;
        mTime=mHour +":"+mMinute;

        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle=s.toString().trim();
                mTitleText.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateText.setText(mDate);
        mTimeText.setText(mTime);

        if(savedInstanceState!=null){
            String savedTitle=savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle=savedTitle;

            String savedTime=savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTitle);
            mTime=savedTime;

            String savedDate=savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate=savedDate;

            mActive=savedInstanceState.getString(KEY_ACTIVE);
        }

        if(mActive.equals("false")){
            mFAB.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Add Remainder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        //outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        //outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        //outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);

    }

    public void setTime(View view){
        Calendar now=Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tdp= com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this,now.get(Calendar.HOUR_OF_DAY)
        ,now.get(Calendar.MINUTE),true);
        tdp.setThemeDark(false);
        tdp.show(getFragmentManager(),"TimePickerDialog");
    }

    public void setDate(View view){
        Calendar now=Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd=new com.wdullaer.materialdatetimepicker.date.DatePickerDialog().newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dpd.show(getFragmentManager(),"DatePickerDialog");
    }


    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay=dayOfMonth;
        mMonth=monthOfYear;
        mYear=year;
        mDate=dayOfMonth+"/"+monthOfYear+"/"+year;
        mDateText.setText(mDate);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour=hourOfDay;
        mMinute=minute;
        if(minute<10){
            mTime=hourOfDay+":"+"0"+minute;
        }
        else {
            mTime=hourOfDay+":"+minute;
        }
        mTimeText.setText(mTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.remainder_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentRemainderUri==null){
            MenuItem menuItem=menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if(mTitleText.getText().toString().length()==0){
                    mTitleText.setError("Remainder Title Can't be Blank!");
                }
                else {
                    saveReminder();
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!mVehicleHasChanged){
                    NavUtils.navigateUpFromSameTask(AddRemainderActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AddRemainderActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes?");
        builder.setPositiveButton("Discard",discardButtonClickListener);
        builder.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete this remainder?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRemainder();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void deleteRemainder(){
        if(mCurrentRemainderUri!=null){
            int rowsDeleted=getContentResolver().delete(mCurrentRemainderUri,null,null);
            if(rowsDeleted==0){
                TSnackbar snackbar=TSnackbar.make(toolbar,"Error Deleting Remainder",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else{
                TSnackbar snackbar=TSnackbar.make(toolbar,"Deleted",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        finish();
    }

    public void saveReminder(){
        ContentValues values=new ContentValues();
        mRepeat="false";
        mRepeatType="H";
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE,mTitle);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_DATE,mDate);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_TIME,mTime);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT,mRepeat);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_NO,mRepeatNo);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_TYPE,mRepeatType);
        values.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_ACTIVE,mActive);

        mCalender.set(Calendar.MONTH,--mMonth);
        mCalender.set(Calendar.YEAR,mYear);
        mCalender.set(Calendar.DAY_OF_MONTH,mDay);
        mCalender.set(Calendar.HOUR_OF_DAY,mHour);
        mCalender.set(Calendar.MINUTE,mMinute);
        mCalender.set(Calendar.SECOND,0);

        long selectedTimestamp=mCalender.getTimeInMillis();

        if(mRepeatType.equals("Minute")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milMinute;
        }
        else if(mRepeatType.equals("Hour")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milHour;
        }
        else if(mRepeatType.equals("Day")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milDay;
        }

        else if(mRepeatType.equals("Week")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milWeek;
        }

        else if(mRepeatType.equals("Month")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milMonth;
        }

        if(mCurrentRemainderUri==null){
            Uri newUri=getContentResolver().insert(AlarmRemainderContract.AlarmRemainderEntry.CONTENT_URI,values);
           // int rowsAffected=getContentResolver().update(mCurrentRemainderUri,values,null,null);
            if (newUri==null){
                TSnackbar snackbar=TSnackbar.make(toolbar,"Error Saving",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else {
                TSnackbar snackbar=TSnackbar.make(toolbar,"Saved",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        else {
            int rowsAffected=getContentResolver().update(mCurrentRemainderUri,values,null,null);
            if(rowsAffected==0){
                TSnackbar snackbar=TSnackbar.make(toolbar,"Error Updating",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else {
                TSnackbar snackbar=TSnackbar.make(toolbar,"Remainder Updated",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        mRepeat="false";
        if(mActive.equals("true")){
            if(mRepeat.equals("true")){
                //new AlarmScheduler().setRepeatAlarm(getApplicationContext(),selectedTimestamp,mCurrentRemainderUri,mRepeatTime);
            }
            else if(mRepeat.equals("false"))
                new AlarmScheduler().setAlarm(getApplicationContext(),selectedTimestamp,mCurrentRemainderUri);
        }

        TSnackbar snackbar=TSnackbar.make(toolbar,"Saved",Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection={
                AlarmRemainderContract.AlarmRemainderEntry._ID,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_DATE,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_TIME,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_NO,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_TYPE,
                AlarmRemainderContract.AlarmRemainderEntry.KEY_ACTIVE,
        };

        return new CursorLoader(this,mCurrentRemainderUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data==null||data.getCount()<1)
            return;
        if (data.moveToFirst()){
            int titleColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE);

            int dateColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_DATE);

            int timeColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_TIME);

            int repeatColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT);

            int repeatNoColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_NO);

            int repeatTypeColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_TYPE);

            int activeColumnIndex=data.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_ACTIVE);

            String title=data.getString(titleColumnIndex);

            String date=data.getString(dateColumnIndex);

            String time=data.getString(timeColumnIndex);

            String repeat=data.getString(repeatColumnIndex);

            String repeatNo=data.getString(repeatNoColumnIndex);

            String repeatType=data.getString(repeatTypeColumnIndex);

            String active=data.getString(activeColumnIndex);

            mTitleText.setText(title);
            mDateText.setText(date);
            mTimeText.setText(time);
            //mRepeatNoText.setText(repeatNo);
            //mRepeatTypeText.setText(repeatType);
            //mRepeatText.setText("Every"+repeatNo+" "+repeatType+"(s)");

            /*if(repeat.equals("false")){
                mRepeatSwitch.setChecked(false);
                //mRepeatText.setText("off");

            }
            else if(repeat.equals("true"))
                mRepeatSwitch.setChecked(true);*/
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
