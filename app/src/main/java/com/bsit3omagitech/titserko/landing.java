package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import java.util.Random;

public class landing extends AppCompatActivity {

    Button btn_landing_study,btn_landing_quiz;
    ImageView iv_back, iv_landing_stars, iv_tips;
    TextView tv_landingTitle;
    Context c = this;
    String lessonName, lessonNameTranslated, lessonId, username;
    DataBaseHelper db;
    ProgressBar lessonProgressBar, quizProgressBar;
    List<String> lessonTranslated;
    JSONObject tipsObject;
    float maxLesson, maxScore ;
    List<Integer> animals;
    Dialog dialog;
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
        iv_tips = findViewById(R.id.iv_tips);
        iv_tips.setVisibility(View.GONE);
        tv_landingTitle = (TextView) findViewById(R.id.tv_landingTitle);
        //tv_landingTitleTranslation = (TextView) findViewById(R.id.tv_landingTitleTranslation);
        iv_landing_stars = findViewById(R.id.iv_landing_stars);
        tipsObject = new JSONObject();
        dialog = new Dialog(c);
        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lesson");
        lessonNameTranslated = intent.getStringExtra("lessonTranslated");
        lessonId = intent.getStringExtra("lessonId");
        username = intent.getStringExtra("username");
        tv_landingTitle.setText(lessonName);


        //animal list
        animals = new ArrayList<>();
        animals.add(R.drawable.vector_animal_1);
        animals.add(R.drawable.vector_animal_2);
        animals.add(R.drawable.vector_animal_3);
        animals.add(R.drawable.vector_animal_4);
        animals.add(R.drawable.vector_animal_5);

        //database
        db = new DataBaseHelper(getApplicationContext());
        db.createLessonProgressEntry(username,lessonId);

        int stars = db.getLessonStar(username, lessonId);
        switch (stars){
            case 0:
                iv_landing_stars.setImageResource(R.drawable.score_star);
                break;
            case 1:
                iv_landing_stars.setImageResource(R.drawable.score_star_1);
                break;
            case 2:
                iv_landing_stars.setImageResource(R.drawable.score_star_2);
                break;
            case 3:
                iv_landing_stars.setImageResource(R.drawable.score_star_3);
                break;
        }


        //parse json object

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            JSONObject targetLesson;
            for (int i = 0; i < m_jArry.length(); i++) {

                if(m_jArry.getJSONObject(i).getString("lesson_id").equals(lessonId)) {
                    maxLesson = m_jArry.getJSONObject(i).getJSONArray("parts").length();
                    maxScore = m_jArry.getJSONObject(i).getJSONArray("quiz").length();

                    //check if lesson has tip
                    if(m_jArry.getJSONObject(i).has("tips")) {
                        tipsObject = m_jArry.getJSONObject(i).getJSONObject("tips");
                        String title, content;
                        title = tipsObject.getString("title");
                        content = tipsObject.getString("content");
                        iv_tips.setVisibility(View.VISIBLE);

                                //set click listener for tip, when clicked show tip dialog
                        iv_tips.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openDialog(title, content);
                            }
                        });


                    }

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
       float d = (currentQuizProgress/(15)) * 100f;
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
                float d = (currentQuizProgress/(14)) * 100f;
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
                Intent intent = new Intent(c, tk_loading.class);
                intent.putExtra("username", username);
                intent.putExtra("actName", "dashboard");
                startActivity(intent);
                finish();


            }
        });

        //study
        btn_landing_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to study
                Intent intent = new Intent(landing.this, tk_loading.class);
                intent.putExtra("lessonName", lessonName);
                intent.putExtra("lessonTranslated", lessonNameTranslated);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("username", username);
                intent.putExtra("actName", "study");
                startActivity(intent);
                finish();

            }

        });

        //quiz
        btn_landing_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to quiz
                Intent intent = new Intent(landing.this, tk_loading.class);
                intent.putExtra("lessonName", lessonName);
                intent.putExtra("lessonTranslated", lessonNameTranslated);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("username", username);
                intent.putExtra("actName", "quiz");
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

    private void openDialog(String title, String content){

        dialog.setContentView(R.layout.tip_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tip_tv_title = dialog.findViewById(R.id.tip_tv_title);
        TextView tip_tv_desc = dialog.findViewById(R.id.tip_tv_desc);
        Button tip_btn_cancel = dialog.findViewById(R.id.tip_btn_cancel);
        ImageView tip_iv_animal_pic = dialog.findViewById(R.id.tip_iv_animal_pic);


        //set tip content
        tip_tv_title.setText(title);
        tip_tv_desc.setText(content);

        //set random image
            Random rand = new Random();
            int i = rand.nextInt(4);
            tip_iv_animal_pic.setImageResource(animals.get(i));

        //set listener of button
        tip_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}