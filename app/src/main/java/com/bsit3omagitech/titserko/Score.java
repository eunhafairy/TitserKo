package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Score extends AppCompatActivity {

    TextView score_tv_title, score_tv_score;
    Button score_btn;
    Intent intent;
    String lessonName, lessonId, lessonTranslated, username;
    DataBaseHelper db;
    int score, totalScore;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        init();
        reg();

    }

    private void init(){

        //context
        c = this;

        //get intents
        intent  = getIntent();
        lessonName = intent.getStringExtra("lesson");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        username = intent.getStringExtra("username");
        score = intent.getIntExtra("Score", 0);
        totalScore = intent.getIntExtra("TotalScore", 0);

        //views
        score_tv_title = (TextView) findViewById(R.id.score_tv_title);
        score_tv_title.setText(lessonName + " Quiz");
        score_tv_score = (TextView) findViewById(R.id.score_tv_score);
        score_tv_score.setText(score + "/" + totalScore);
        score_btn = (Button) findViewById(R.id.score_btn);
        db = new DataBaseHelper(this);
    }

    private void reg(){

        score_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.refreshAllStars(username);
                Intent intent = new Intent(c, TkDashboardActivity.class);
                intent.putExtra("lesson", lessonName);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("lessonTranslated", lessonTranslated);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

    }
}