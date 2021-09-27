package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView iv_back;
    TextView tv_landingTitle, tv_landingTitleTranslation;
    Context c = this;
    String lessonName, lessonNameTranslated, lessonId, username;
    List<String> lessonTranslated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //initialize variables and register listeners
        init();
        reg();
    }

    private void init(){

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
        //parse json object
        //try json object
        /*
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            lessonTranslated = new ArrayList<String>();
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String _lessonName = jo_inside.getString("lesson_name_translate");

                //add
                lessonTranslated.add(_lessonName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */


    }

    private void reg(){

        //back function
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}