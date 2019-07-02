package com.example.jagath.notesandtasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.support.v7.widget.Toolbar;

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
 * Created by jagath on 18/03/2018.
 */

public class ArchivedNotes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private DataBaseHelper helper;
    private Cursor cursor;
    private ArrayList<NoteContent> arrayList;
    private NotesRecyclerAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    private TextView header_login,user_email;
    ImageView profile;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static String TAG="MainAcitivity";
    private GoogleApiClient apiClient;
    private RelativeLayout relativeLayout;
    private android.support.v7.widget.Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archived_main_layout);
        recyclerView=findViewById(R.id.recycler_viewa);
        helper=new DataBaseHelper(this);
         toolbar=findViewById(R.id.toolbarx);
        SharedPreferences preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        int prefcolor=preferences.getInt("color",R.color.colorPrimary);
        toolbar.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }

        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        relativeLayout=findViewById(R.id.lin_archive_Content);
        FirebaseUser user=mAuth.getCurrentUser();
        toolbar.setTitle("Archived");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout_archive_note);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getAllNotes();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        getAllNotes();
        updateUser(user);

    }
    public void getAllNotes() {
        cursor=helper.getNoteData();
        arrayList=new ArrayList<NoteContent>();
        if(cursor!=null){
            if(cursor.moveToLast()){
                do{
                    int id=cursor.getInt(0);
                    String title=cursor.getString(1);
                    String content1=cursor.getString(2);
                    String last1=cursor.getString(3);
                    String pin1=cursor.getString(4);
                    int color=cursor.getInt(5);
                    String status=cursor.getString(6);
                    if(status.equals("archived")){
                        NoteContent content=new NoteContent(id,color,title,content1,last1,pin1,status);
                        arrayList.add(content);
                    }
                }while (cursor.moveToPrevious());
            }
        }
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        adapter=new NotesRecyclerAdapter(arrayList,getApplicationContext(),1,relativeLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
       /* ItemTouchHelper.SimpleCallback simpleItemTouchCallback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.dismmiss(viewHolder.getAdapterPosition(),getApplicationContext());
                getAllNotes();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"Long Clicked",Toast.LENGTH_LONG).show();
             return true;
            }
        });*/
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
        } else if (id == R.id.nav_remainders) {
            Intent intent=new Intent(this,GetAllRemainders.class);
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
