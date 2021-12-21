package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spnr_profile;
    ImageView iv_study;
    LinearLayout ll_landing, ll_profile;
    Button tv_createProfile;
    Button btn_confirm;
    String usernameSelected;
    DataBaseHelper db;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        init();

    }

    private void init(){

        db = new DataBaseHelper(this);
        usernameSelected = "";
        spnr_profile = (Spinner) findViewById(R.id.spnr_profile);
        spnr_profile.setOnItemSelectedListener(this);
        //load spinner data
        loadSpinnerData();
        iv_study = (ImageView) findViewById(R.id.iv_study);
        ll_landing = (LinearLayout) findViewById(R.id.ll_landing);
        ll_landing.setVisibility(View.VISIBLE);
        ll_profile = (LinearLayout) findViewById(R.id.ll_profile);
        ll_profile.setVisibility(View.GONE);
        tv_createProfile = findViewById(R.id.tv_createProfile);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        mp = new MediaPlayer();

        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bensound_bgm);
        try {
            mp.setDataSource(getApplicationContext(), mediaPath);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //register listeners
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login

                if (spnr_profile.getCount()==0){
                    Toast.makeText(getApplicationContext(),"Create a profile first!",
                            Toast.LENGTH_LONG).show();
                }
                else{


                    mp.stop();
                    Intent intent = new Intent(MainActivity.this, tk_loading.class);
                    intent.putExtra("username", spnr_profile.getSelectedItem().toString());
                    intent.putExtra("actName", "dashboard");
                    startActivity(intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    finish();
                }

            }
        });

        iv_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(db.userExists()){
                    ll_landing.setVisibility(View.GONE);
                    ll_profile.setVisibility(View.VISIBLE);

                }
                else{
                    mp.stop();
                    Intent intent = new Intent(MainActivity.this, tk_register.class);
                    startActivity(intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    finish();
                }

            }
        });

        tv_createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create profile
                mp.stop();
                Intent intent = new Intent(MainActivity.this, tk_register.class);
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();

            }
        });

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

    private void loadSpinnerData(){

        // database handler
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());

        // Spinner Drop down elements
        List<String> usernames = db.getAllUsername();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, usernames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnr_profile.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        usernameSelected = text;
    } public void onBackPressed() {

        if(ll_profile.getVisibility() == View.VISIBLE){

            ll_profile.setVisibility(View.GONE);
            ll_landing.setVisibility(View.VISIBLE);
        }
        else{
            System.exit(0);
            finish();

        }




    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }






}