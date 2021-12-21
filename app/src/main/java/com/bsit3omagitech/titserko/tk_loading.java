package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class tk_loading extends AppCompatActivity {


    List<Integer> gifList;
    Context context;
    GifImageView gif_loading;
    Intent intent;
    String actName;
    boolean allowBlock = false;
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

        //add gif ids to gifList
        gifList = new ArrayList<>();
        gifList.add(R.drawable.loadinggif1);
        gifList.add(R.drawable.loading_gif2);
        gifList.add(R.drawable.loading_gif3);
        gifList.add(R.drawable.loading_gif4);
        gifList.add(R.drawable.loading_gif5);
        gifList.add(R.drawable.loading_gif6);
        gif_loading = findViewById(R.id.gif_loading);

        Random rand = new Random();
        int a = rand.nextInt(6);
        gif_loading.setImageResource(gifList.get(a));
        Log.d("random imageresource", a+ "");

        //generate number for loading time
        rand = new Random();
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

        Intent gotoNext = new Intent();
        switch(activityName){

            case "quiz":
                gotoNext   = new Intent(context, QuizProper.class);
                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
               break;

            case "study":
                gotoNext   = new Intent(context, LessonProper.class);
                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                break;

            case "score":
                gotoNext   = new Intent(context, Score.class);

                gotoNext.putExtra("lessonName", intent.getStringExtra("lessonName"));
                gotoNext.putExtra("Score", intent.getIntExtra("Score", 0));
                gotoNext.putExtra("lesson", intent.getStringExtra("lesson"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("username",  intent.getStringExtra("username"));
                break;

            case "dashboard":
                gotoNext   = new Intent(context, TkDashboardActivity.class);
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                break;

            case "landing":
                gotoNext   = new Intent(context, landing.class);
                gotoNext.putExtra("lesson", intent.getStringExtra("lesson"));
                gotoNext.putExtra("lessonTranslated", intent.getStringExtra("lessonTranslated"));
                gotoNext.putExtra("lessonId", intent.getStringExtra("lessonId"));
                gotoNext.putExtra("username", intent.getStringExtra("username"));
                break;

            default:
                break;
        }

        startActivity(gotoNext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        finish();



    }

    @Override
    public void onBackPressed() {
        if (allowBlock) {
            super.onBackPressed();
        } else {
            // do nothing
        }

    }
}