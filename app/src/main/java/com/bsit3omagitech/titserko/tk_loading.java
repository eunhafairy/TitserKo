package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class tk_loading extends AppCompatActivity {



    Context context;
    Intent intent;
    String actName;
    int _seconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_loading);

        init();
    }

    private void init(){

        intent = getIntent();
        actName = intent.getStringExtra("actName");


        //generate number for loading time
       Random rand = new Random();
        _seconds = (int) rand.nextInt(2000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                goToNextActivity(actName);
            }
        }, _seconds + 500);
        Log.d("rand", _seconds + "");
        context = this;
    }

    private void goToNextActivity(String activityName){

        Intent gotoNext;
        switch(activityName){

            case "quiz":
                gotoNext   = new Intent(context, QuizProper.class);
                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                startActivity(gotoNext);
                finish();
                break;

            case "study":
                gotoNext   = new Intent(context, LessonProper.class);
                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                startActivity(gotoNext);
                finish();
                break;

            case "score":
                gotoNext   = new Intent(context, Score.class);

                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("Score", intent.getIntExtra("Score", 0));
                gotoNext.putExtra("lesson", intent.getStringExtra("lesson"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("username",  intent.getStringExtra("username"));
                startActivity(gotoNext);
                finish();
                break;

            case "dashboard":
                gotoNext   = new Intent(context, TkDashboardActivity.class);
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                startActivity(gotoNext);
                finish();
                break;

            case "landing":
                gotoNext   = new Intent(context, landing.class);
                gotoNext.putExtra("lesson", intent.getStringExtra("lesson"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                startActivity(gotoNext);
                finish();
                break;

            default:
                break;
        }


    }



}