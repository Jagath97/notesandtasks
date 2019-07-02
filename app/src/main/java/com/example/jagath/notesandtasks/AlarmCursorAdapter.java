package com.example.jagath.notesandtasks;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jagath on 26/03/2018.
 */

public class AlarmCursorAdapter extends CursorAdapter {

    private TextView mTitleText,mDateAndTimeText,mRepeatInfoText;
    private ImageView mActiveImage;
//private ColorGenerator mColorGenerator=ColorGenerator.DEFAULT;
//public TextDrawable mDrawableBuilder;
    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.remaindercard,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTitleText=view.findViewById(R.id.rem_title);
        mDateAndTimeText=view.findViewById(R.id.rem_date_time);
        mRepeatInfoText=view.findViewById(R.id.rem_repeat);
        mActiveImage=view.findViewById(R.id.thumbnai_rem);

        int titleColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE);

        int dateColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_DATE);

        int timeColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_TIME);

        int repeatColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT);

        int repeatNoColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_NO);

        int repeatTypeColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_REPEAT_TYPE);

        int activeColumnIndex=cursor.getColumnIndex(AlarmRemainderContract.AlarmRemainderEntry.KEY_ACTIVE);

        String title=cursor.getString(titleColumnIndex);

        String date=cursor.getString(dateColumnIndex);

        String time=cursor.getString(timeColumnIndex);

        String repeat=cursor.getString(repeatColumnIndex);

        String repeatNo=cursor.getString(repeatNoColumnIndex);

        String repeatType=cursor.getString(repeatTypeColumnIndex);

        String active=cursor.getString(activeColumnIndex);

        String dateTime=date+ " " +time;

        setReminderTitle(title);
        if(date!=null)
        {
            setReminderDateTime(dateTime);
        }
        else
            mDateAndTimeText.setText("Date Not Set");
        if(repeat!=null)
            setRemainderRepeatInfo(repeat,repeatNo,repeatType);
        if(active!=null){
            setActiveImage(active);
        }



    }

    public void setReminderTitle(String title){
        mTitleText.setText(title);
        String letter="A";
        /*if(title!=null && title.isEmpty()){
            letter=title.substring(0,1);
        }
        int color=mColorGenerator.getRandomColor();

        mDrawableBuilder=TextDrawable.builder()*/

    }
    public void setReminderDateTime(String dateTime){
        mDateAndTimeText.setText(dateTime);
    }
    public void setRemainderRepeatInfo(String repeat,String repeatNo,String repeatType){
        if(repeat.equals("true"))
            mRepeatInfoText.setText("Every"+repeatNo+" "+repeatType+"(s)");
        else if(repeat.equals(false))
            mRepeatInfoText.setText("Repeatoff");
    }
    public void setActiveImage(String activeImage){
        if(activeImage.equals("true"))
            mActiveImage.setImageResource(R.drawable.ic_alarm_on_black_24dp);
        else if(activeImage.equals("false"))
            mActiveImage.setImageResource(R.drawable.ic_alarm_off_black_24dp);
    }
}
