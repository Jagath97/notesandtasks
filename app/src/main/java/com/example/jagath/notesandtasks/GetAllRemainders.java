package com.example.jagath.notesandtasks;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by jagath on 26/03/2018.
 */

public class GetAllRemainders extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    private FloatingActionButton    addReminder;
    AlarmCursorAdapter alarmCursorAdapter;
    AlarmRemainderDbHelper alarmRemainderDbHelper=new AlarmRemainderDbHelper(this);
    ListView reminderListView;
    ProgressDialog progressDialog;
    private TextView header_login,user_email;
    ImageView profile;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static String TAG="MainAcitivity";
    private GoogleApiClient apiClient;
    private RelativeLayout relativeLayout;

    private static final int VEHICLE_LOADER=0;
    private SharedPreferences preferences;
    private int prefcolor;
    private String alarmTitle;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remainder_mainlayout);
        toolbar=findViewById(R.id.toolbarr);
        toolbar.setTitle("Remainders");
        setSupportActionBar(toolbar);
        preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        reminderListView=findViewById(R.id.rem_rec_view);
        View emptyView=findViewById(R.id.empty_rem);
        reminderListView.setEmptyView(emptyView);
        prefcolor=preferences.getInt("color", Color.parseColor("#303F9F"));
        alarmCursorAdapter=new AlarmCursorAdapter(this,null);
        reminderListView.setAdapter(alarmCursorAdapter);
        toolbar.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerViewr=navigationView.getHeaderView(0);
        header_login=headerViewr.findViewById(R.id.textView);
        user_email=headerViewr.findViewById(R.id.user_email);
        profile=headerViewr.findViewById(R.id.profile_image);
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(),"Error ",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(GetAllRemainders.this,AddRemainderActivity.class);
                Uri currentVehicleUri= ContentUris.withAppendedId(AlarmRemainderContract.AlarmRemainderEntry.CONTENT_URI,id);
                intent.setData(currentVehicleUri);
                startActivity(intent);
            }
        });
        addReminder=findViewById(R.id.fab_new_rem);
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(v.getContext(),AddRemainderActivity.class);
                //startActivity(intent);
                addReminderTitle();
            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER,null,this);
        updateUser(user);
    }

    private void addReminderTitle() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Set Reminder Title");
        final EditText input=new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.requestFocus();
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().isEmpty())
                    return;
                alarmTitle=input.getText().toString();
                ContentValues contentValues=new ContentValues();
                contentValues.put(AlarmRemainderContract.AlarmRemainderEntry.KEY_TITLE,alarmTitle);
                Uri newUri=getContentResolver().insert(AlarmRemainderContract.AlarmRemainderEntry.CONTENT_URI,contentValues);
                restartLoader();

                if (newUri==null){
                    Toast.makeText(getApplicationContext(),"Setting Title Failed",Toast.LENGTH_SHORT).show();}
                    else
                     Toast.makeText(getApplicationContext(),"Title Set",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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

        return new CursorLoader(this, AlarmRemainderContract.AlarmRemainderEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        alarmCursorAdapter.swapCursor(data);
    }
    private void restartLoader() {
        getLoaderManager().initLoader(VEHICLE_LOADER,null,this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    alarmCursorAdapter.swapCursor(null);
    }


    private void signIn() {
        apiClient.clearDefaultAccountAndReconnect();
        Intent signinIntent=Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signinIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = task.getResult(ApiException.class);
                progressDialog.setMessage("Signing In");
                progressDialog.show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAuth=FirebaseAuth.getInstance();
                            Toast.makeText(getApplicationContext(),"Authentication Success.",Toast.LENGTH_LONG).show();
                            TSnackbar.make(toolbar, "Authentication Success.", Snackbar.LENGTH_SHORT).show();
                            updateUser(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"Authentication Failed.",Toast.LENGTH_LONG).show();
                            TSnackbar.make(toolbar, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void updateUser(FirebaseUser user){
        if(user!=null){
            String Email=user.getEmail();
            String Display=user.getDisplayName();
            header_login.setText(Display);
            user_email.setText(Email);
            user_email.setVisibility(View.VISIBLE);
            Uri url=user.getPhotoUrl();
            profile.setImageURI(null);
            profile.setImageURI(url);
            profile.setVisibility(View.VISIBLE);
        }
        else {
            header_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            Intent intent=new Intent(this,MainActivityLauncher.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_archived) {
            Intent intent=new Intent(this,ArchivedNotes.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent=new Intent(this,SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.help_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
