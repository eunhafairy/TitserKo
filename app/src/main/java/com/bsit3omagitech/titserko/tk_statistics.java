package com.bsit3omagitech.titserko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class tk_statistics extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {


    ProgressBar pb_stat_overall;
    TextView tv_stat_overall_progress, tv_stat_overall_badges, tv_stat_overall_stars, tv_stat_overall_finished;
    String username;
    ImageView iv_stat_back;
    Context ct;
    StatisticsHelper st;
    DataBaseHelper db;
    RecyclerView stat_rv;
    List<String> titles;
    List<Integer> lessonProgress, quizHighscore, quizLowscore, quizTaken;
    NavigationView stats_navigationView;
    DrawerLayout stats_drawerLayout;
    Toolbar toolbar;
    GlobalFunctions gf;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_statistics);

        init();



    }

    private void init(){

        // ------------------------------------------ INITIALIZE VARIABLES ------------------------------------------
        pb_stat_overall = findViewById(R.id.pb_stat_overall);
        tv_stat_overall_progress = findViewById(R.id.tv_stat_overall_progress);
        tv_stat_overall_badges = findViewById(R.id.tv_stat_overall_badges);
        tv_stat_overall_stars = findViewById(R.id.tv_stat_overall_stars);
        tv_stat_overall_finished = findViewById(R.id.tv_stat_overall_finished);
        iv_stat_back = findViewById(R.id.iv_stat_back);
        stat_rv = findViewById(R.id.rv_stats);
        ct =this;
        db = new DataBaseHelper(ct);
        st = new StatisticsHelper(db,ct);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        gf = new GlobalFunctions(ct);
        mp = new MediaPlayer();
        try {
            gf.playBGM(mp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ------------------------------------------ SETTING TEXT VIEWS ------------------------------------------
        pb_stat_overall.setProgress(st.getOverallProgress(username));
        tv_stat_overall_progress.setText(st.getOverallProgress(username) + "%");
        tv_stat_overall_badges.setText(st.userTotalBadges(username) + "");
        tv_stat_overall_stars.setText(st.userTotalStars(username) + "");
        tv_stat_overall_finished.setText(st.getLessonFinished(username) + "");

        // ------------------------------------------ SETTING RECYCLER VIEW ------------------------------------------

        getLessonList(); // for titles
        lessonProgress = db.getAllLessonProgress(username);
        quizHighscore = db.getAllQuizHighscore(username);
        quizLowscore = db.getAllQuizLowscore(username);
        quizTaken = db.getAllQuizTaken(username);


        StatisticsAdapter adapter = new StatisticsAdapter(titles, lessonProgress, quizHighscore, quizLowscore, quizTaken, ct);
        stat_rv.setAdapter(adapter);
        stat_rv.setLayoutManager(new LinearLayoutManager(this));

        //navigation
        stats_navigationView = findViewById(R.id.stats_nav_view);
        stats_navigationView.setCheckedItem(R.id.nav_stats);
        toolbar = findViewById(R.id.stats_toolbar);
        stats_drawerLayout= findViewById(R.id.stats_drawer_layout);
        setSupportActionBar(toolbar);

        iv_stat_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Here", "Went here");
                stats_drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        stats_navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, stats_drawerLayout, toolbar, R.string.navigation_drawer_open ,R.string.navigation_drawer_close);
        stats_drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        stats_navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);


    }



    // -------------------------------------------- JSON PARSER FUNCTION -------------------------------------

    public void getLessonList(){

        //get lesson list
        try {
            JSONObject obj = new JSONObject(gf.loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            titles = new ArrayList<String>();
            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String _lessonName = jo_inside.getString("lesson_name");
                //add to list
                titles.add(_lessonName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


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



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            //---------Home----------
            case R.id.nav_home:
                mp.stop();
                Intent i1 = new Intent(this, TkDashboardActivity.class);
                i1.putExtra("username", username);
                startActivity(i1);
                finish();
                break;

            //--------Profile------------
            case R.id.nav_profile:
                mp.stop();
                Intent i2 = new Intent(this, tk_profile.class);
                i2.putExtra("username", username);
                startActivity(i2);
                finish();
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                mp.stop();
                Intent achievement_intent = new Intent(this, tk_achievements.class);
                achievement_intent.putExtra("username", username);
                startActivity(achievement_intent);
                finish();
                break;

            case R.id.nav_logout:
                mp.stop();
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                finish();
                break;

            case R.id.nav_stats:
                break;


        }
        stats_drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        mp.stop();
        Intent i1 = new Intent(ct, TkDashboardActivity.class);
        i1.putExtra("username", username);
        startActivity(i1);
        finish();

    }



}