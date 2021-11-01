package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;

public class landing extends AppCompatActivity {

    Button btn_landing_study,btn_landing_quiz;
    ImageView iv_back, lesson_img;
    TextView tv_landingTitle, tv_landingTitleTranslation;
    Context c = this;
    String lessonName, lessonNameTranslated, lessonId, username;
    DataBaseHelper db;
    ProgressBar lessonProgressBar, quizProgressBar;
    List<String> lessonTranslated;
    float maxLesson, maxScore;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //initialize variables and register listeners
        init();
        reg();
    }

    private void init(){

        maxLesson = 0;

        btn_landing_study = (Button) findViewById(R.id.btn_landing_study);
        btn_landing_quiz = (Button) findViewById(R.id.btn_landing_quiz);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_landingTitle = (TextView) findViewById(R.id.tv_landingTitle);
        tv_landingTitleTranslation = (TextView) findViewById(R.id.tv_landingTitleTranslation);



        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lesson");
        lessonNameTranslated = intent.getStringExtra("lessonTranslated");
        lessonId = intent.getStringExtra("lessonId");
        username = intent.getStringExtra("username");
        tv_landingTitle.setText(lessonName);
        tv_landingTitleTranslation.setText(lessonNameTranslated);

        //database
        db = new DataBaseHelper(getApplicationContext());
        db.createLessonProgressEntry(username,lessonId);




        //parse json object

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            JSONObject targetLesson;
            for (int i = 0; i < m_jArry.length(); i++) {

                if(m_jArry.getJSONObject(i).getString("lesson_id").equals(lessonId)) {
                maxLesson = m_jArry.getJSONObject(i).getJSONArray("parts").length();
                maxScore = m_jArry.getJSONObject(i).getJSONArray("quiz").length();
                break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        //for lesson progress
       db.refreshAllStars(username);
       lessonProgressBar = (ProgressBar) findViewById(R.id.lessonProgressBar);
       float currentProgress = db.getLessonProgress(username, lessonId);
       float c = (currentProgress/(maxLesson-1)) * 100f;
       //Log.d(" percentage",  "Current Progress: "+currentProgress + ", Max Lesson: " +maxLesson + ", Percent: " + c);
       lessonProgressBar.setProgress((int) c);

       quizProgressBar = (ProgressBar) findViewById(R.id.quizProgressBar);
       float currentQuizProgress = db.getQuizProgress(username, lessonId);
       float d = (currentQuizProgress/(maxScore)) * 100f;
       quizProgressBar.setProgress((int) d);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                float currentProgress = db.getLessonProgress(username, lessonId);
                float c = (currentProgress/(maxLesson-1)) * 100f;
                Log.d(" percentage",  "Current Progress: "+currentProgress + ", Max Lesson: " +maxLesson + ", Percent: " + c);
                lessonProgressBar.setProgress((int) c);

                float currentQuizProgress = db.getQuizProgress(username, lessonId);
                float d = (currentQuizProgress/(maxScore-1)) * 100f;
                quizProgressBar.setProgress((int) d);
                swipeRefreshLayout.setRefreshing(false);

            }
        });


    }

    private void reg(){

        //back function
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.refreshAllStars(username);
                Intent intent = new Intent(c, TkDashboardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);


            }
        });

        //study
        btn_landing_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to study
                Intent intent = new Intent(landing.this, LessonProper.class);
                intent.putExtra("lessonName", lessonName);
                intent.putExtra("lessonTranslated", lessonNameTranslated);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();

            }

        });

        //quiz
        btn_landing_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to quiz
                Intent intent = new Intent(landing.this, QuizProper.class);
                intent.putExtra("lessonName", lessonName);
                intent.putExtra("lessonTranslated", lessonNameTranslated);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
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

    public void onBackPressed() {

        db.refreshAllStars(username);
        Intent intent = new Intent(c, TkDashboardActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();

    }


}