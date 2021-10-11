package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class tk_statistics extends AppCompatActivity {


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_statistics);

        init();
        reg();


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

    }

    private void reg(){
        // ------------------------------------------ REGISTER LISTENERS ------------------------------------------
        iv_stat_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ct, TkDashboardActivity.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            }
        });
    }


    // -------------------------------------------- JSON PARSER FUNCTION -------------------------------------

    public void getLessonList(){

        //get lesson list
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
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





}