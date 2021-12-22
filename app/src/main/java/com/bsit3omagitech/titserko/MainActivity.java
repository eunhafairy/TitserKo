package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
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
    ImageView iv_study,imageView;
    Button tv_createProfile;
    Button btn_confirm;
    String usernameSelected;
    TextView textView;
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



        //load spinner data


        //show the group of views for welcome
        imageView = findViewById(R.id.imageView);
        iv_study = (ImageView) findViewById(R.id.iv_study);

        imageView.setVisibility(View.VISIBLE);
        iv_study.setVisibility(View.VISIBLE);

        //show the group of views for signing in
        spnr_profile = (Spinner) findViewById(R.id.spnr_profile);
        textView = findViewById(R.id.textView);
        tv_createProfile = findViewById(R.id.tv_createProfile);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        spnr_profile.setOnItemSelectedListener(this);
        loadSpinnerData();


        spnr_profile.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.GONE);
        tv_createProfile.setVisibility(View.GONE);




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
                    spnr_profile.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    btn_confirm.setVisibility(View.VISIBLE);
                    tv_createProfile.setVisibility(View.VISIBLE);

                    imageView.setVisibility(View.GONE);
                    iv_study.setVisibility(View.GONE);



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
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onBackPressed() {

        if( spnr_profile.getVisibility() == View.VISIBLE){

            spnr_profile.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
            tv_createProfile.setVisibility(View.GONE);

            imageView.setVisibility(View.VISIBLE);
            iv_study.setVisibility(View.VISIBLE);

        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit App?")
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
                    .setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();


        }




    }





}