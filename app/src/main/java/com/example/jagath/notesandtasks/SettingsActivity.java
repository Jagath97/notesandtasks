package com.example.jagath.notesandtasks;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

import static junit.runner.BaseTestRunner.getPreference;

public class SettingsActivity extends AppCompatActivity{
    private static final int DIALOG = 0;
    Button b;
    Toolbar toolbar;
    SharedPreferences.Editor editor;
    int butoncolor=0,prefcolor=0;
    String morning,afternoon,evening;
    ListView listView1,listView2,listView3;
    List<SettingsContent> list1,list2,list3;
    SettingsContentAdapter adapter1,adapter2,adapter3;
    SharedPreferences preferences;
    private int year,month,day,hour,minute;
    private static final int Dialog2=1;
    private static final int Dialog3=2;
    FirebaseAuth auth;
    Button logout;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Fragment fragment=new SettingsFragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        if(savedInstanceState==null){
            fragmentTransaction.add(R.id.preference_layout,fragment,"settings_fragment");
            fragmentTransaction.commit();
        }
        else {
            fragment=getFragmentManager().findFragmentByTag("settings_fragment");
        }
        */
        setContentView(R.layout.activity_settings);
        toolbar=findViewById(R.id.toolbar2);
        preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        toolbar.setTitle("Settings");
        prefcolor=preferences.getInt("color",Color.parseColor("#303F9F"));
        version=preferences.getString("app_version","1.0");
        auth=FirebaseAuth.getInstance();
        toolbar.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        logout=findViewById(R.id.logout);
        if(auth.getCurrentUser()==null){
            logout.setVisibility(View.INVISIBLE);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent=new Intent(getApplicationContext(),MainActivityLauncher.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        listView1=findViewById(R.id.settings_list1);
        list1=new ArrayList<>();
        list1.add(new SettingsContent("App Color"," "));
        adapter1=new SettingsContentAdapter(this,list1);
        listView1.setAdapter(adapter1);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        colorpicker();
                        break;
                }
            }
        });

        refresh();
        listView3=findViewById(R.id.settings_about_list);
        list3=new ArrayList<>();
        list3.add(new SettingsContent("App Name","My Notes"));
        list3.add(new SettingsContent("Build Version",version));
        //list3.add(new SettingsContent("Open-source licences",""));
        adapter3=new SettingsContentAdapter(this,list3);
        listView3.setAdapter(adapter3);
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        Toast.makeText(getApplicationContext(),version,Toast.LENGTH_LONG).show();
                }
            }
        });
        final Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);
    }
    private void refresh(){
        morning=preferences.getString("morningtime","08:00");
        afternoon=preferences.getString("aftntime","13:00");
        evening=preferences.getString("evngtime","18:00");

        /*listView2=findViewById(R.id.settings_list2);
        list2=new ArrayList<>();
        list2.add(new SettingsContent("Morning",morning));
        list2.add(new SettingsContent("Afternoon",afternoon));
        list2.add(new SettingsContent("Evening",evening));
        adapter2=new SettingsContentAdapter(this,list2);
        listView2.setAdapter(adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        showDialog(DIALOG);
                        break;
                    case 1:
                        showDialog(Dialog2);
                        break;
                    case 2:
                        showDialog(Dialog3);
                        break;
                }
            }
        });*/
    }

    private void colorpicker() {
        editor=preferences.edit();
        ColorPicker colorPicker=new ColorPicker(SettingsActivity.this);
        ArrayList<String> colors=new ArrayList<>();
        colors.add("#D3D3D3");
        colors.add("#00FF00");
        colors.add("#FF0000");
        colors.add("#CB1755");
        colors.add("#FF96B4");
        colors.add("#C0C0C0");
        colors.add("#FFA500");
        colors.add("#FFA500");
        colors.add("#3F51B5");
        colors.add("#303F9F");
        colorPicker.setColors(colors)
                .setColumns(5)
                .setTitle("Choose a Color")
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onChooseColor(int position, int color) {
                        toolbar.setBackgroundColor(color);
                        getWindow().setStatusBarColor(color);
                        butoncolor=color;
                        //Colorize();
                        editor.putInt("color",color);
                        editor.apply();
                        editor.commit();
                        Intent intent=new Intent(SettingsActivity.this,MainActivityLauncher.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
    }
/*
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void Colorize(){
        ShapeDrawable d=new ShapeDrawable(new OvalShape());
        d.setBounds(30,30,30,30);
        d.getPaint().setStyle(Paint.Style.FILL);
        d.getPaint().setColor(butoncolor);
        b.setBackground(d);
    }
    */
@Override
protected Dialog onCreateDialog(int id) {
    final int i=id;
    if(id==DIALOG||id==Dialog2||id==Dialog3)
        return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editor=preferences.edit();
                String time;
                time=settime(hourOfDay,minute);
                if(i==DIALOG)
                    editor.putString("morningtime",time);
                if(i==Dialog2)
                    editor.putString("aftntime",time);
                if(i==Dialog3)
                    editor.putString("evngtime",time);
                editor.apply();
                editor.commit();
                refresh();
            }
        },hour,minute,true);
   return null;
}
/*<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/remainder_settings"
    android:paddingLeft="14dp"
    android:paddingStart="14dp"
    android:paddingTop="10dp"
    android:paddingBottom="5dp"
    android:textColor="@color/colorAccent"
    android:textSize="16sp"
    android:textStyle="bold"
    tools:ignore="RtlSymmetry" />
    <ListView
    android:id="@+id/settings_list2"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    </ListView>*/
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
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
