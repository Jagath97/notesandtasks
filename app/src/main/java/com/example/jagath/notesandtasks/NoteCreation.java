package com.example.jagath.notesandtasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import petrov.kristiyan.colorpicker.ColorPicker;

public class NoteCreation extends AppCompatActivity {
    private boolean ischecked=false;
    EditText title,content;
    FloatingActionButton save;
    private static final int DIALOG=0;
    int y,m,d,h,mn;
    Calendar c;
    int color2,color1=0;
    TextView edited;
    DataBaseHelper helper;
    int length1,length2,length3,length4;
    int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_creation_and_view);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
       toolbar.setTitle("Note");
       title=findViewById(R.id.note_new_title);
       content=findViewById(R.id.new_note);
       title.requestFocus();
        helper=new DataBaseHelper(this);
        edited=findViewById(R.id.edited_last);
        SharedPreferences preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        int prefcolor=preferences.getInt("color",Color.parseColor("#303F9F"));
        toolbar.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }
        save=findViewById(R.id.save_floating_button);
        save.setBackgroundTintList(ColorStateList.valueOf(prefcolor));
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(insertDataNote()){
                    Intent intent=new Intent(getApplicationContext(),MainActivityLauncher.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            String tit=bundle.getString("title");
            String con=bundle.getString("content");
            String mod=bundle.getString("modified");
            int color=bundle.getInt("color");
            color2=color;
            id=bundle.getInt("id");
            title.setText(tit);
            content.setText(con);
            length1=title.getText().toString().length();
            length2=content.getText().toString().length();
            edited.setText("Edited: "+mod);
            LinearLayout layout=findViewById(R.id.cons_color);
            layout.setBackgroundColor(color);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updateNote()){
                        Intent intent=new Intent(getApplicationContext(),MainActivityLauncher.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean updateNote() {
        c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        int day=c.get(Calendar.DAY_OF_MONTH);
        String t1=settime(hour,minute);
        month=month+1;
        String m1=setMonth(month);
        String modified=day+" "+m1+" "+year+" "+t1;
        if(title.getText().toString().length()==0&&content.getText().toString().length()==0){
            title.setError("Required");
            return false;
        }
        else{
            String title1=title.getText().toString();
            String content1=content.getText().toString();
           return helper.updateNote(id,title1,content1,modified,color2);
        }
    }

    private boolean insertDataNote() {
        c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        int day=c.get(Calendar.DAY_OF_MONTH);
        String t1=settime(hour,minute);
        month=month+1;
        String m1=setMonth(month);
        String modified=day+" "+m1+" "+year+" "+t1;
        if(title.getText().toString().length()==0){
            title.setError("Required");
            return false;
        }
        else{
            String title1=title.getText().toString();
            String content1=content.getText().toString();
            return helper.createNewNote(title1,content1,modified,"no",color2,"active");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_create_menu_remainder, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem checkable=menu.findItem(R.id.action_pin);
        //checkable.setChecked(ischecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        /*if (id == R.id.action_pin) {
            if(!item.isChecked()){
            ischecked=!item.isChecked();
            item.setChecked(ischecked);
            item.setIcon(R.drawable.ic_push_pinw);
            }
            else if(item.isChecked()){
                ischecked=!item.isChecked();
                item.setChecked(ischecked);
                item.setIcon(R.drawable.ic_push_pin);
            }
            return true;
        }*/
        if(id==R.id.action_color){
            addColor();
        }
        /*
        if(id==R.id.action_label){
            Toast.makeText(this,"Remainder",Toast.LENGTH_LONG).show();
            //addRemainder();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /*private void addRemainder() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog alertDialog;
       View view=getLayoutInflater().inflate(R.layout.new_note_remainder,null);
        builder.setView(view);
        alertDialog=builder.create();
        alertDialog.show();
       EditText sel_date=view.findViewById(R.id.select_datetime);
       sel_date.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showDialog(DIALOG);
           }
       });
        Spinner spinner=view.findViewById(R.id.select_repeat_rem);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.repeat));
        spinner.setAdapter(arrayAdapter);
        Button save=view.findViewById(R.id.button2);
        Button cancel=view.findViewById(R.id.cancel_remainder);
        Button delete=view.findViewById(R.id.delete_rem);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Save",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        c=Calendar.getInstance();
        TimePickerDialog dialog2=new TimePickerDialog(this,timelistener1,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true);
        DatePickerDialog dialog1=new DatePickerDialog(this,datepicker1,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        dialog1.setButton(DialogInterface.BUTTON_POSITIVE,"Continue",dialog1);
        if (id==DIALOG)
           return dialog1;
        if(id==1)
            return new TimePickerDialog(this,timelistener1,c.get(Calendar.HOUR_OF_DAY)+1,c.get(Calendar.MINUTE),true);
        return null;
    }
    DatePickerDialog.OnDateSetListener datepicker1=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            showDialog(1);
        }
    };
    TimePickerDialog.OnTimeSetListener timelistener1=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        }
    };
*/

    public void addColor(){
        final int[] color1 = new int[1];
        ColorPicker colorPicker=new ColorPicker(this);
        ArrayList<String> colors=new ArrayList<>();
        colors.add("#ffffff");
        colors.add("#D3D3D3");
        colors.add("#00FF00");
        colors.add("#FF0000");
        colors.add("#CB1755");
        colors.add("#FF96B4");
        colors.add("#C0C0C0");
        colors.add("#FFA500");
        colors.add("#FFA500");

        colorPicker.setColors(colors)
                .setColumns(5)
                .setTitle("Choose a Color")
                .setDefaultColorButton(Color.parseColor("#ffffff"))
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onChooseColor(int position, int color) {
                        LinearLayout layout=findViewById(R.id.cons_color);
                                layout.setBackgroundColor(color);
                                sendColor(color);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
    }

    private void sendColor(int color) {
        color2=color;
        color1=color;
    }
    public String settime(int hourOfDay,int minute){
        String time;
        if(hourOfDay<10||minute<10){
            time ="0"+Integer.toString(hourOfDay)+":"+Integer.toString(minute);
            if(minute<10&&hourOfDay<10){
                time="0"+Integer.toString(hourOfDay)+":0"+Integer.toString(minute);
            }
            if(minute<10&&hourOfDay>=10){
                time=Integer.toString(hourOfDay)+":0"+Integer.toString(minute);
            }
        }

        else {
            time = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
        }
        return time;
    }
    public String setMonth(int month){
    String mon[]={"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
    for(int i=1;i<13;i++){
        if(i==month){
            return mon[i-1];
        }
    }
    return "Invalid";
    }
    @Override
    public void onBackPressed() {
        String title1=title.getText().toString();
        String content1=content.getText().toString();
        length3=title1.length();
        length4=content1.length();
        if(length3==length1&&length4==length2&&color1==0){
            NoteCreation.super.onBackPressed();
        }
        else if(title.getText().toString().length()!=0||content.getText().toString().length()!=0){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            AlertDialog alertDialog;
            builder.setCancelable(true);
            builder.setTitle("Save Changes?");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(id==0) {
                        if (insertDataNote()) {
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivityLauncher.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                    else{
                        if (updateNote()) {
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivityLauncher.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NoteCreation.super.onBackPressed();

                }
            });

            alertDialog=builder.create();
            alertDialog.show();
        }
        else {
            super.onBackPressed();
        }

    }
}
