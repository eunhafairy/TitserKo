package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class tk_credit extends AppCompatActivity {

    ImageView btn_credit_back;
    GlobalFunctions gf;
    MediaPlayer mp;
    Context context;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_credit);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        context = this;
        gf = new GlobalFunctions(context);
        //-----------------PLAY MUSIC------------------
        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bgm_lesson_1);
        try {
            gf.playBGM(mp, mediaPath, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_credit_back= findViewById(R.id.btn_credit_back);
        btn_credit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tk_credit.this, tk_profile.class);
                i.putExtra("username", username);
                startActivity(i);
                mp.stop();
                finish();
            }
        });


    }


     @Override
        public void onBackPressed() {


        Intent i1 = new Intent(this, tk_profile.class);
        i1.putExtra("username", username);
        startActivity(i1);
        mp.stop();
        finish();

        }

}