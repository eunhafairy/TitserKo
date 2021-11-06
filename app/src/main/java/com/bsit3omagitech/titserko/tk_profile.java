package com.bsit3omagitech.titserko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.apmem.tools.layouts.FlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class tk_profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ImageView btn_profile_back, iv_profile_badge;
    TextView tv_profile_name, tv_profile_age;
    String name, dateTime, age;
    Context c;
    DataBaseHelper db;
    Date bday;

    List<String> imagePaths;
    NavigationView profile_navigationView;
    DrawerLayout profile_drawerLayout;
    Toolbar toolbar;
    LinearLayout ll_profile_badge_list;
    int numOfBadges = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_profile);


        init();
        reg();
    }

    private  void init(){

        // --------------------------------------------- INITIALIZE ----------------------------------------------
        btn_profile_back = findViewById(R.id.btn_profile_back);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        tv_profile_age = findViewById(R.id.tv_profile_age);
        iv_profile_badge  = findViewById(R.id.iv_profile_badge);
        ll_profile_badge_list = findViewById(R.id.ll_profile_badge_list);

        c = this;
        db = new DataBaseHelper(c);

        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        bday = db.getUserBirthday(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = bday;
        dateTime = dateFormat.format(date);
        age = getAge(dateTime) + "";

        populateProfileBadge();
        iv_profile_badge.setImageURI(db.getUserBadge(name));

        // --------------------------------------------- NAVIGATION ---------------------------------------------
        profile_navigationView = findViewById(R.id.profile_nav_view);
        profile_navigationView.setCheckedItem(R.id.nav_profile);
        toolbar = findViewById(R.id.profile_toolbar);
        profile_drawerLayout = findViewById(R.id.profile_drawer_layout);
        setSupportActionBar(toolbar);

        btn_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        profile_navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, profile_drawerLayout, toolbar, R.string.navigation_drawer_open ,R.string.navigation_drawer_close);
        profile_drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        profile_navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) c);

    }

    private void reg(){

        // --------------------------------------------- REGISTER LISTENERS AND FUNCTIONS ----------------------------------------------
        tv_profile_name.setText(name);
        numOfBadges = db.getTotalUserBadge(name);
        tv_profile_age.setText(age + " years old | " + numOfBadges + " Badges");

        btn_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


    }

    public int getAge(String dateTime){

        int age = 0;

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null){
        Log.d("dada", "null");
            return 0;
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);
        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }




        return age;



    }



    private void populateProfileBadge(){

        imagePaths = db.getProfileBadges(name);
        int ctr = imagePaths.size();

        int sizeInDP = 20;
        int sizeBadges = 80;

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());
        int sizeBadgeIndp =  (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeBadges, getResources()
                        .getDisplayMetrics());



        for(int i = 0; i < ctr; i++){

            ImageView imageView = new ImageView(c);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(sizeBadgeIndp,sizeBadgeIndp));

           LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams) imageView.getLayoutParams();
           layoutParams.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
           imageView.setLayoutParams(layoutParams);

            Uri imageUri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePaths.get(i));
            imageView.setImageURI(imageUri);

            if(imagePaths.get(i).equals("lockedbadge")){

                imageView.setAlpha(0.5f);
            }
            ll_profile_badge_list.addView(imageView);

        }


    }

    // --------------------------------------------- SIDE BAR NAVIGATION FUNCTIONS -----------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            //---------Home----------
            case R.id.nav_home:
                Intent i1 = new Intent(this, TkDashboardActivity.class);
                i1.putExtra("username", name);
                startActivity(i1);
                break;

            //--------Profile------------
            case R.id.nav_profile:
                Intent i2 = new Intent(this, tk_profile.class);
                i2.putExtra("username", name);
                startActivity(i2);
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                Intent achievement_intent = new Intent(this, tk_achievements.class);
                achievement_intent.putExtra("username", name);
                startActivity(achievement_intent);
                break;

            case R.id.nav_logout:
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                finish();
                break;

            case R.id.nav_stats:
                Intent i4 = new Intent(this, tk_statistics.class);
                i4.putExtra("username", name);
                startActivity(i4);
                finish();
                break;


        }
        profile_drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}