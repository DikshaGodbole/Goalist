package com.example.android.goalist;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    LoginOptionActivity loginOptionActivity = new LoginOptionActivity();
    private StorageReference mStorageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String user_id,email;
    private ImageView imageView;
    private TextView emailView,displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

        Toasty.Config.getInstance().setTextColor(getResources().getColor(R.color.secondaryColor)).apply();
        boolean status = loginOptionActivity.isConnectingToInternet(this);
        if(!status) {
            Toasty.custom(MainActivity.this,"No Internet",R.drawable.t_internet,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
        }

        Toolbar  myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.profile_image_drawer);
        displayView = (TextView) header.findViewById(R.id.profile_name);
        emailView = (TextView) header.findViewById(R.id.profile_email);

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("users");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
            email = user.getEmail();
        }
        emailView.setText(email);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_messages:
                        selectedFragment = MessageFragment.newInstance();
                        break;
                    case R.id.todist:
                        selectedFragment = TodoFragment.newInstance();
                        break;
                }
                FragmentTransaction fragmentTransaction =  getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containFragment,selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containFragment, MessageFragment.newInstance());
        transaction.commit();

        mStorageReference.child("profilePhotos").child(user_id).child("photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(MainActivity.this).load(uri).transform(new CircleTransformActivity()).fit().centerCrop().into(imageView);
            }
        });

        Intent fragment = getIntent();
        String fragmentToLoad = fragment.getStringExtra("FragmentTodo");
        if(fragmentToLoad!=null && fragmentToLoad.equals("Todo")){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction1 = fragmentManager.beginTransaction();
            transaction1.replace(R.id.containFragment,TodoFragment.newInstance());
            transaction1.commit();
            bottomNavigationView.getMenu().findItem(R.id.todist).setChecked(true);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        databaseReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class);
                if(user!=null) {
                    displayView.setText(user.displayName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_out) {
            mAuth.signOut();
            Toasty.custom(MainActivity.this, "Signed Out", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
            finish();
            Intent intent = new Intent(this, LoginOptionActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_about ){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
