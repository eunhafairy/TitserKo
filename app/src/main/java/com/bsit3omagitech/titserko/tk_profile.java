package com.bsit3omagitech.titserko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.apmem.tools.layouts.FlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class tk_profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ImageView btn_profile_back, iv_profile_badge;
    TextView tv_profile_name, tv_profile_age;
    String name, dateTime, age;
    Context c;
    DataBaseHelper db;
    Date bday;
    TextView btn_logout, btn_credits, btn_delete, btn_tutorial;
    GlobalFunctions gf;
    List<String> imagePaths;
    NavigationView profile_navigationView;
    DrawerLayout profile_drawerLayout;
    Toolbar toolbar;
    ConstraintLayout ll_profile_badge_list;
    MediaPlayer mp;
    Dialog dialog;
    int numOfBadges = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_profile);


        init();
        reg();
    }

    private  void init(){
        c = this;
        db = new DataBaseHelper(c);
        gf = new GlobalFunctions(c);
        // --------------------------------------------- INITIALIZE ----------------------------------------------
        btn_profile_back = findViewById(R.id.btn_profile_back);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        tv_profile_age = findViewById(R.id.tv_profile_age);
        iv_profile_badge  = findViewById(R.id.iv_profile_badge);
        ll_profile_badge_list = findViewById(R.id.ll_profile_badge_list);

        //buttons
        btn_logout = findViewById(R.id.prof_btn_logout);
        btn_credits = findViewById(R.id.prof_btn_credits);
        btn_delete = findViewById(R.id.prof_btn_delete);
        btn_tutorial = findViewById(R.id.prof_btn_tutorial);
        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");
        bday = db.getUserBirthday(name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = bday;
        dateTime = dateFormat.format(date);
        age = gf.getAge(dateTime) + "";



        //media player
        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + c.getPackageName() + "/" + R.raw.bgm_achieve_1);
        try {
            gf.playBGM(mp, mediaPath, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dialog
        dialog = new Dialog(c);




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

        btn_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rerun tutorial
                //first, set user's first time to true
                db.updateFirstTime(name, true);
                //then start intent to dashboard
                mp.stop();
                Intent i1 = new Intent(c, tk_loading.class);
                i1.putExtra("username", name);
                i1.putExtra("actName", "dashboard");
                startActivity(i1);
                finish();

            }
        });

        btn_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new activity for all the credits
                mp.stop();
                Intent i1 = new Intent(c, tk_credit.class);
                i1.putExtra("username", name);
                startActivity(i1);
                finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show warning box
                openDialog("Confirm", "Are you sure you want to delete? All data will be lost.");


            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout the user
                mp.stop();
                Intent i3 = new Intent(c, MainActivity.class);
                startActivity(i3);
                finish();
            }
        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mp.start();
    }



    private void populateProfileBadge(){

        imagePaths = db.getProfileBadges(name);
        int ctr = imagePaths.size();

        for(int i = 0; i < ctr; i++){


            //constraints layout
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(ll_profile_badge_list);


            //random number for id
            int randNum;
            Random rand = new Random();
            while(true){
                randNum = rand.nextInt(10000);
                if(!isResourceIdInPackage("com.bsit3omagitech.titserko",randNum)){
                    break;
                }
            }

            //CREATE IMAGE VIEW
            ImageView imageView = new ImageView(c);
            imageView.setId(randNum);
            ll_profile_badge_list.addView(imageView);
            imageView.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
            Uri imageUri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePaths.get(i));
            imageView.setImageURI(imageUri);

            if(imagePaths.get(i).equals("lockedbadge")){
                imageView.setAlpha(0.5f);
            }


            if(i == 0)
            {
                constraintSet.connect(imageView.getId(), ConstraintSet.START, ll_profile_badge_list.getId(), ConstraintSet.START);
            }
            else{
                constraintSet.connect(imageView.getId(), ConstraintSet.START, ll_profile_badge_list.getChildAt(i-1).getId(), ConstraintSet.END);
            constraintSet.setMargin(imageView.getId(),ConstraintSet.START, sizeInDp(20));
            }

            constraintSet.connect(imageView.getId(), ConstraintSet.TOP, ll_profile_badge_list.getId(), ConstraintSet.TOP);
            constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, ll_profile_badge_list.getId(), ConstraintSet.BOTTOM);

            constraintSet.setDimensionRatio(imageView.getId(),"1:1");
//            constraintSet.setMargin(imageView.getId(),ConstraintSet.START, sizeInDp(20));
//            constraintSet.setMargin(imageView.getId(),ConstraintSet.TOP, sizeInDp(85));
//            constraintSet.setMargin(imageView.getId(),ConstraintSet.BOTTOM, sizeInDp(30));




            constraintSet.applyTo(ll_profile_badge_list);

        }


    }



    //checker if id exists. credits from Chris Sprague of StackOverFlow
    private boolean isResourceIdInPackage(String packageName, int resId){
        if(packageName == null || resId == 0){
            return false;
        }

        Resources res = null;
        if(packageName.equals(getPackageName())){
            res = getResources();
        }else{
            try{
                res = getPackageManager().getResourcesForApplication(packageName);
            }catch(PackageManager.NameNotFoundException e){
                Log.w("isExisting", packageName + "does not contain " + resId + " ... " + e.getMessage());
            }
        }

        if(res == null){
            return false;
        }

        return isResourceIdInResources(res, resId);
    }

    private boolean isResourceIdInResources(Resources res, int resId) {

        try {
            getResources().getResourceName(resId);

            //Didn't catch so id is in res
            return true;

        } catch (Resources.NotFoundException e) {
            return false;
        }
    }


    private int sizeInDp(int index){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, index, getResources()
                        .getDisplayMetrics());

    }

    public void openDialog( String title, String message){

        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView confirm_tv_dialog_title = dialog.findViewById(R.id.confirm_tv_dialog_title);
        TextView confirm_tv_dialog_message = dialog.findViewById(R.id.confirm_tv_dialog_message);
        Button confirm_btn_dialog_okay = dialog.findViewById(R.id.confirm_btn_dialog_okay);
        Button confirm_btn_dialog_cancel = dialog.findViewById(R.id.confirm_btn_dialog_cancel);

        confirm_tv_dialog_title.setText(title);
        confirm_tv_dialog_message.setText(message);

        confirm_btn_dialog_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteUserData(name);
                Toast.makeText(c,"Successfully deleted: "+ name, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                Intent intent = new Intent(c, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        confirm_btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    // --------------------------------------------- SIDE BAR NAVIGATION FUNCTIONS -----------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            //---------Home----------
            case R.id.nav_home:
                mp.stop();
                Intent i1 = new Intent(this, TkDashboardActivity.class);
                i1.putExtra("username", name);
                startActivity(i1);
                finish();
                break;

            //--------Profile------------
            case R.id.nav_profile:
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                mp.stop();
                Intent achievement_intent = new Intent(this, tk_achievements.class);
                achievement_intent.putExtra("username", name);
                startActivity(achievement_intent);
                finish();
                break;

            case R.id.nav_logout:
                mp.stop();
                Intent i3 = new Intent(c, MainActivity.class);
                startActivity(i3);
                finish();
                break;

            case R.id.nav_stats:
                mp.stop();
                Intent i4 = new Intent(this, tk_statistics.class);
                i4.putExtra("username", name);
                startActivity(i4);
                finish();
                break;
            case R.id.nav_settings:
                mp.stop();
                Intent i5 = new Intent(this, tk_settings.class);
                i5.putExtra("username", name);
                startActivity(i5);
                finish();
                break;


        }
        profile_drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onBackPressed() {

        mp.stop();
        Intent i1 = new Intent(c, TkDashboardActivity.class);
        i1.putExtra("username", name);
        startActivity(i1);
        finish();

    }

}