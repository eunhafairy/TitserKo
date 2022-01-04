package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Score extends AppCompatActivity {

    TextView score_tv_title, score_tv_score, score_high_score;
    Button score_btn, score_btn_retry;
    Intent intent;
    ImageView iv_trophy;
    String lessonName, lessonId, lessonTranslated, username;
    DataBaseHelper db;
    GlobalFunctions gf;
    int score, totalScore, highscore;
    List<String> achieveList;
    Context c;
    Dialog dialog;
    public LinkedBlockingQueue<Dialog> dialogsToShow = new LinkedBlockingQueue<>();

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
        gf = new GlobalFunctions(c);
        //get intents
        intent  = getIntent();
        lessonName = intent.getStringExtra("lesson");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        username = intent.getStringExtra("username");
        score = intent.getIntExtra("Score", 0);
        totalScore = 15;
        highscore= 0;

        //for achievements
        db = new DataBaseHelper(this);
        dialog = new Dialog(c);
        achieveList = db.refreshAchievements(username);
        queueAchievements(achieveList, dialog);

        //for highscore
        highscore =  db.getQuizProgress(username, lessonId);

        //views
        score_high_score = findViewById(R.id.score_high_score);
        score_high_score.setText(highscore + "");
        score_tv_title = (TextView) findViewById(R.id.score_tv_title);
        score_tv_title.setText(lessonName);
        score_tv_score = (TextView) findViewById(R.id.score_tv_score);
        score_tv_score.setText(score + "/" + totalScore);
        score_btn = (Button) findViewById(R.id.score_btn);

        score_btn_retry = findViewById(R.id.score_btn_retry);
        iv_trophy = findViewById(R.id.iv_trophy);

        if(score < 7){
            //bronze
            iv_trophy.setImageResource(R.drawable.trophy_bronze);
        }
        else if(score < 15){
            iv_trophy.setImageResource(R.drawable.trophy_silver);
            //silver
        }
        else if (score == 15){
            //gold
            iv_trophy.setImageResource(R.drawable.trophy_gold);
        }



    }

    private void reg(){

        score_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.refreshAllStars(username);
                Intent intent = new Intent(c, tk_loading.class);
                intent.putExtra("username", username);
                intent.putExtra("actName","dashboard");
                startActivity(intent);
                finish();
            }
        });

        score_btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Score.this, tk_loading.class);
                intent.putExtra("lessonName", lessonName);
                intent.putExtra("lessonTranslated", lessonTranslated);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("username", username);
                intent.putExtra("actName","quiz");
                startActivity(intent);
                finish();
            }
        });

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
            //sound
            MediaPlayer sfx = new MediaPlayer();
            String audio_url = "achievement_unlocked_sound";
            String audio_path = "general_audio/"+audio_url;
            gf.playAudio(sfx, audio_path);

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