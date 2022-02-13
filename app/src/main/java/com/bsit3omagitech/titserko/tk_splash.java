package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

public class tk_splash extends AppCompatActivity {

    MediaPlayer mp;
    Context context;
    GlobalFunctions gf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_splash);

        context =this;
        mp = new MediaPlayer();
        gf = new GlobalFunctions(context);
        Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bgm_splash);
        try {
            gf.playBGM(mp, mediaPath, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //generate number for loading time

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                mp.stop();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();


            }
        }, 7000);

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
    public void onBackPressed() {
        //do nothing
    }
}