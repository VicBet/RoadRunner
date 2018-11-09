package com.roadrunner.android.roadrunner;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private DatabaseReference mDatabaseUser;
    private TextView mtxtUsername, mtxtEmail;
    private CircleImageView mUserProf;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ReportFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_report);
        }

        View header=navigationView.getHeaderView(0);

        mtxtEmail = (TextView) header.findViewById(R.id.txtEmail);
        mtxtUsername = (TextView) header.findViewById(R.id.txtUsername);
        mUserProf = (CircleImageView) header.findViewById(R.id.imageProfView);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();



        mUserProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        mtxtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        mtxtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value= mCurrentUser.getEmail();
        String valueP= mCurrentUser.getEmail();
        final String value2 = mCurrentUser.getUid();

        mtxtEmail.setText(valueP);

        mDatabaseUser = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseUser.keepSynced(true);


        mDatabaseUser.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String post_image = (String) dataSnapshot.child("profimage").getValue();
                final String navusername = (String) dataSnapshot.child("username").getValue();




                if (navusername!=null) {
                    mtxtUsername.setText(navusername);
                }
                else {
                    mtxtUsername.setText("No Username");
                }

                Picasso.get().load(post_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.userprof2).into(mUserProf, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(post_image).placeholder(R.drawable.userprof2).into(mUserProf);

                    }



                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.nav_account:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;

            case R.id.nav_location:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LocationFragment()).commit();
                break;


            case R.id.nav_Refueling:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RefuelFragment()).commit();
                break;


            case R.id.nav_car:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyVehiclesFragment()).commit();
                break;

            case R.id.nav_garage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyServiceFragment()).commit();
                break;

            case R.id.nav_report:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ReportFragment()).commit();
                break;

            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();
                break;


            case R.id.nav_language:
                Toast.makeText(this, "Change Language", Toast.LENGTH_SHORT).show();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
            finish();
        }

    }
}