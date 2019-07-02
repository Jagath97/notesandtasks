package com.example.jagath.notesandtasks;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.net.URL;
import java.util.ArrayList;

public class MainActivityLauncher extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GetNoteFromDb {
    BottomNavigationView bottomNavigationView;
    private DataBaseHelper helper;
    private GoogleApiClient apiClient;
   private RecyclerView recyclerView,recyclerViewpin;
   private NotesRecyclerAdapter adapter,adapterp;
   private SwipeRefreshLayout swipeRefreshLayout;
   private ArrayList<NoteContent> arrayList,arrayList1;
   private RecyclerView.LayoutManager layoutManager;
   private Cursor cursor;
   ImageView intro;
   TextView pin,others,header_login,user_email;
   ImageView profile;
   private static final int RC_SIGN_IN=1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static String TAG="MainAcitivity";
    private RelativeLayout relativeLayout;
    private DrawerLayout drawerLayout;
    CoordinatorLayout layout1;
    LinearLayout layout;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_launcher);
         toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences preferences=getSharedPreferences("settingcolor",MODE_PRIVATE);
        int prefcolor=preferences.getInt("color", Color.parseColor("#303F9F"));
        toolbar.setBackgroundColor(prefcolor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(prefcolor);
        }
        intro=findViewById(R.id.intro_content);
        relativeLayout=findViewById(R.id.lin_main_Content);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout_note);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getAllNotes(getApplicationContext());

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        progressDialog=new ProgressDialog(this);
        layout=findViewById(R.id.botom_nav_lay);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView=findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        helper=new DataBaseHelper(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        bottomNavigationView=findViewById(R.id.bottom_nav);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehaviour());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.view_note:
                        break;
                    case R.id.write_note:
                        startActivity(new Intent(getApplicationContext(),NoteCreation.class));
                        break;
                    case R.id.view_remainder:
                        startActivity(new Intent(getApplicationContext(),GetAllRemainders.class));
                        break;
                    default:

                }
                return false;
            }
        });
        layout1=findViewById(R.id.coordinator_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getAllNotes(this);
        View headerViewr=navigationView.getHeaderView(0);
        header_login=headerViewr.findViewById(R.id.textView);
        user_email=headerViewr.findViewById(R.id.user_email);
        profile=headerViewr.findViewById(R.id.profile_image);
        //final FragmentManager fragmentManager=this.getFragmentManager();
        /*authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    String Email=mAuth.getCurrentUser().getEmail();
                    String Display=mAuth.getCurrentUser().getDisplayName();
                    header_login.setText(Display);
                    user_email.setText(Email);
                    user_email.setVisibility(View.VISIBLE);
                }
        };
        };*/
        updateUser(user);
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

    }

    private void signIn() {
        apiClient.clearDefaultAccountAndReconnect();
        Intent signinIntent=Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signinIntent,RC_SIGN_IN);
    }
    public void updateUser(FirebaseUser user){
        if(user!=null){
            //Toast.makeText(this,"User Present",Toast.LENGTH_LONG).show();
            String Email=user.getEmail();
            String Display=user.getDisplayName();
            Uri url=user.getPhotoUrl();
            header_login.setText(Display);
            user_email.setText(Email);
            profile.setImageURI(null);
            profile.setImageURI(url);
            profile.setVisibility(View.VISIBLE);
            user_email.setVisibility(View.VISIBLE);
        }
        else {
            //Toast.makeText(this,"User Not Present",Toast.LENGTH_LONG).show();
            header_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SignInDialog dialog=new SignInDialog();
                    //dialog.show(fragmentManager,"Sigin in dialog");
                    signIn();
                }
            });
        }
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


    public void getAllNotes(Context context) {
        cursor=helper.getNoteData();
        arrayList=new ArrayList<NoteContent>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        if(cursor.getCount()==0){
            Toast.makeText(this,"No Notes",Toast.LENGTH_LONG).show();
            intro.setVisibility(View.VISIBLE);
        }else {
            intro.setVisibility(View.INVISIBLE);
        }
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
                    if(status.equals("active")){
                        NoteContent content=new NoteContent(id,color,title,content1,last1,pin1,status);
                        arrayList.add(content);
                    }
                }while (cursor.moveToPrevious());
            }
        }
        if(arrayList.isEmpty()){
            intro.setVisibility(View.VISIBLE);
        }
        adapter=new NotesRecyclerAdapter(arrayList,getApplicationContext(),0,toolbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnTapListener(new OnTapListener() {
            @Override
            public void OnTapView(int position) {
                Toast.makeText(getApplicationContext(),"CLICKED"+position,Toast.LENGTH_LONG).show();
            }
        });
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //adapter.dismmiss(viewHolder.getAdapterPosition(),getApplicationContext());
                adapter.archive(viewHolder.getAdapterPosition(),getApplicationContext());
                TSnackbar snackbar=TSnackbar.make(toolbar,"Archived",Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.parseColor("#323232"));
                TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
                getAllNotes(getApplicationContext());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {

        } else if (id == R.id.nav_remainders) {
            startActivity(new Intent(this,GetAllRemainders.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,SettingsActivity.class));
        } else if (id == R.id.help_feedback) {

        }
        else if (id==R.id.nav_archived){
            startActivity(new Intent(this,ArchivedNotes.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
