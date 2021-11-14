package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.internal.ContextUtils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TkDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //declaration
    TextView db_tv_name, tv_headerName;
    String name;
    RecyclerView myRv;
    List<String> lessonList, lessonTranslated, lessonId, achieveList;
    List<Integer> stars;
    DataBaseHelper db;
    SwipeRefreshLayout swipeRefreshLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageView btn_nav, iv_badge, iv_headerBadge;
    GlobalFunctions gf;
    Context c;
    Dialog dialog;
    public LinkedBlockingQueue<Dialog> dialogsToShow = new LinkedBlockingQueue<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tk_dashboard);

        //initialize
        init();

    }



    private void init(){

        c = this;
        dialog = new Dialog(c);
        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        // --------------------------------------------- INITIALIZE ----------------------------------------------

        db = new DataBaseHelper(this);
        db_tv_name = (TextView) findViewById(R.id.db_tv_name);
        myRv = (RecyclerView) findViewById(R.id.myRv);
        btn_nav = findViewById(R.id.btn_nav);
        db_tv_name.setText(name);
        iv_badge = findViewById(R.id.iv_badge);

        //show badge

        iv_badge.setImageURI(db.getUserBadge(name));


        // ------------------------------------------ DATABASE AND RECYCLER VIEW FUNCTIONS ------------------------------------------
        db.updateNewLessons(name);
        db.updateNewAchievements(name);
        stars = db.getUserStars(name);
        getLessonList();
        myAdapter adapter = new myAdapter(this, lessonList, stars, lessonId);
        myRv.setAdapter(adapter);
        myRv.setLayoutManager(new LinearLayoutManager(this));


        adapter.setIndividualScreenListener(new myAdapter.OnIndividualScreen() {
            @Override
            public void convertViewOnIndividualScreen(int position) {
                String lesson = lessonList.get(position);
                String _lessonTranslated = lessonTranslated.get(position);
                String _lessonId = lessonId.get(position);
                Intent i = new Intent(TkDashboardActivity.this, landing.class);
                i.putExtra("lesson", lesson);
                i.putExtra("lessonTranslated", _lessonTranslated);
                i.putExtra("lessonId", _lessonId);
                i.putExtra("username", name);
                startActivity(i);

            }
        });

        gf = new GlobalFunctions(c);
        db.refreshAllStars(name);
        achieveList = db.refreshAchievements(name);
        queueAchievements(achieveList, dialog);


        // ----------------------------------------- FOR SWIPE REFRESH -----------------------------------------


        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                iv_badge.setImageURI(db.getUserBadge(name));
                lessonList.clear();
                getLessonList();
                stars.clear();
                stars = db.getUserStars(name);
                db.refreshAllStars(name);
                myAdapter adapter = new myAdapter(TkDashboardActivity.this, lessonList, stars, lessonId);
                myRv.setAdapter(adapter);

                adapter.setIndividualScreenListener(new myAdapter.OnIndividualScreen() {
                    @Override
                    public void convertViewOnIndividualScreen(int position) {
                        String lesson = lessonList.get(position);
                        String _lessonTranslated = lessonTranslated.get(position);
                        String _lessonId = lessonId.get(position);
                        Intent i = new Intent(TkDashboardActivity.this, landing.class);
                        i.putExtra("lesson", lesson);
                        i.putExtra("lessonTranslated", _lessonTranslated);
                        i.putExtra("lessonId", _lessonId);
                        i.putExtra("username", name);
                        startActivity(i);

                    }
                });
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        // ------------------------------------------- NAVIGATION ------------------------------------------------
        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);

        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open ,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }


    //----------------------------- BACK BUTTON PRESS FUNCTION -----------------------------------
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit App?")
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          System.exit(0);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
                    .setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
       //     super.onBackPressed();

        }

    }

// -------------------------------------------- JSON PARSER FUNCTION -------------------------------------
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("lessons.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    // ----------------------------------------------- FOR DATA LIST OF RECYCLER VIEW ------------------------------------------
    public void getLessonList(){


        //get lesson list
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            lessonList = new ArrayList<String>();
            lessonTranslated = new ArrayList<String>();
            lessonId = new ArrayList<String>();
            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String _lessonName = jo_inside.getString("lesson_name");
                String _lessonTranslated =  jo_inside.getString("lesson_name_translate");
                String _lessonId =  jo_inside.getString("lesson_id");

                //add to lists
                lessonList.add(_lessonName);
                lessonTranslated.add(_lessonTranslated);
                lessonId.add(_lessonId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // --------------------------------------------- SIDE BAR NAVIGATION FUNCTIONS -----------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            //---------Home----------
            case R.id.nav_home:
                break;

            //--------Profile------------
            case R.id.nav_profile:
                Intent i = new Intent(this, tk_profile.class);
                i.putExtra("username", name);
                startActivity(i);
                finish();
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                Intent achievement_intent = new Intent(this, tk_achievements.class);
                achievement_intent.putExtra("username", name);
                startActivity(achievement_intent);
                finish();
                break;

            case R.id.nav_logout:
                Intent i2 = new Intent(this, MainActivity.class);
                startActivity(i2);
                finish();
                break;

            case R.id.nav_stats:
                Intent i3 = new Intent(this, tk_statistics.class);
                i3.putExtra("username", name);
                startActivity(i3);
                finish();
                break;


        }
    drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    //------------------- ACHIEVEMENTS ------------------------------

    public void queueAchievements(List<String> achieveList, Dialog dialog) {


        db = new DataBaseHelper(c);
        for (int i = 0; i < achieveList.size(); i++) {

            Dialog dialog2 = new Dialog(c);
            Log.d("xboxhaha", achieveList.get(i));
            String id = achieveList.get(i);
            List<String> info = db.getAchievementInfo(id);
            final String title = info.get(0), imagePath = info.get(2), desc = info.get(1);
            dialog2.setContentView(R.layout.achievement_unlocked);
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btn = dialog2.findViewById(R.id.btn_confirm_achieve_unlocked);
            TextView tv_title_achieve_dialog = dialog2.findViewById(R.id.tv_title_achieve_unlocked);
            TextView tv_desc_achieve_dialog = dialog2.findViewById(R.id.tv_desc_achieve_unlocked);
            ImageView iv_achieve_dialog = dialog2.findViewById(R.id.iv_achieve_badge_unlocked);
            Uri imageUri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePath);
            iv_achieve_dialog.setImageURI(imageUri);
            tv_title_achieve_dialog.setText(title);
            tv_desc_achieve_dialog.setText(desc);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Log.d("xbox", "title: " + title + "id: " + id);
                    dialog2.dismiss();
                }
            });

            dialog2.setCancelable(true);
            showDialog(dialog2);


        }
    }

    public void showDialog(final Dialog dialog){
        if(dialogsToShow.isEmpty()){
            dialog.show();
        }
        dialogsToShow.offer(dialog);
        dialog.setOnDismissListener((d) -> {
            dialogsToShow.remove(dialog);
            if(!dialogsToShow.isEmpty()){
                dialogsToShow.peek().show();
            }
        });

    }
}