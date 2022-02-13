package com.bsit3omagitech.titserko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

public class tk_settings extends AppCompatActivity implements OnNavigationItemSelectedListener{



    Context context;
    DataBaseHelper dataBaseHelper;
    GlobalFunctions gf;
    String username;
    MediaPlayer mp;
    //elements
    ImageView settings_btn_profile_back;
    Button settings_save;
    SeekBar bgm_seekBar, sfx_seekbar;
    NavigationView settings_nav_view;
    Toolbar toolbar;
    DrawerLayout settings_drawer_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_settings);

        init();
        reg();
    }

    private void init(){

        context = this;
        dataBaseHelper = new DataBaseHelper(context);
        gf = new GlobalFunctions(context);
        Intent i = getIntent();
        username = i.getStringExtra("username");
        Log.d("taag", ""+username);

        //-----------------PLAY MUSIC------------------
        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bgm_settings_1);
        try {
            gf.playBGM(mp, mediaPath, username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //---------------------ELEMENTS-------------------
        settings_btn_profile_back = findViewById(R.id.settings_btn_profile_back);
        settings_save = findViewById(R.id.settings_save);
        settings_save.setClickable(false);
        settings_save.setAlpha(0.5f);
        bgm_seekBar = findViewById(R.id.bgm_seekBar);
        sfx_seekbar = findViewById(R.id.sfx_seekbar);


        //----------ASSIGN VALUE TO SEEKBAR---------------
        bgm_seekBar.setMax(100);
        bgm_seekBar.setProgress((int) (dataBaseHelper.getBGM(username) * 100f));
        Log.d("ano", ""+ bgm_seekBar.getProgress());

        sfx_seekbar.setMax(100);
        sfx_seekbar.setProgress((int) (dataBaseHelper.getSFX(username) * 100f));
        Log.d("eto", ""+ sfx_seekbar.getProgress());

        // --------------------------------------------- NAVIGATION ---------------------------------------------
        settings_nav_view = findViewById(R.id.settings_nav_view);
        settings_nav_view.setCheckedItem(R.id.nav_settings);
        toolbar = findViewById(R.id.settings_toolbar);
        settings_drawer_layout = findViewById(R.id.settings_drawer_layout);
        setSupportActionBar(toolbar);

        settings_btn_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_drawer_layout.openDrawer(Gravity.LEFT);
            }
        });

        settings_nav_view.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, settings_drawer_layout, toolbar, R.string.navigation_drawer_open ,R.string.navigation_drawer_close);
        settings_drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        settings_nav_view.setNavigationItemSelectedListener((OnNavigationItemSelectedListener) this);


    }
    private void reg(){

        settings_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database
                float _bgm = (((float)bgm_seekBar.getProgress() ) / 100);
                float _sfx = (((float)sfx_seekbar.getProgress() ) / 100);
                dataBaseHelper.setVolumes(username,_bgm, _sfx);
                Toast.makeText(context, "Successfully changed settings!", Toast.LENGTH_SHORT).show();
                settings_save.setAlpha(0.5f);
                settings_save.setClickable(false);
            }
        });

        bgm_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //enable save btn
                if(bgm_seekBar.getProgress() != ((int)(dataBaseHelper.getBGM(username) * 100f))) {

                    settings_save.setAlpha(1f);
                    settings_save.setClickable(true);


                }
                mp.setVolume((((float)bgm_seekBar.getProgress() ) / 100),(((float)bgm_seekBar.getProgress() ) / 100));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       sfx_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //enable save btn
                if(sfx_seekbar.getProgress() != ((int)(dataBaseHelper.getSFX(username) * 100f))) {

                    settings_save.setAlpha(1f);
                    settings_save.setClickable(true);


                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //---------Home----------
            case R.id.nav_home:
                mp.stop();
                Intent i4 = new Intent(this, TkDashboardActivity.class);
                i4.putExtra("username", username);
                startActivity(i4);
                finish();
                break;

            //--------Profile------------
            case R.id.nav_profile:
                mp.stop();
                Intent i = new Intent(this, tk_profile.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                mp.stop();
                Intent achievement_intent = new Intent(this, tk_achievements.class);
                achievement_intent.putExtra("username", username);
                startActivity(achievement_intent);
                finish();
                break;
            case R.id.nav_logout:
                mp.stop();
                Intent i2 = new Intent(this, MainActivity.class);
                startActivity(i2);
                finish();
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_stats:
                mp.stop();
                Intent i3 = new Intent(this, tk_statistics.class);
                i3.putExtra("username", username);
                startActivity(i3);
                finish();
                break;


        }
        settings_drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {

        mp.stop();
        Intent i1 = new Intent(context, TkDashboardActivity.class);
        i1.putExtra("username", username);
        startActivity(i1);
        finish();

    }
}