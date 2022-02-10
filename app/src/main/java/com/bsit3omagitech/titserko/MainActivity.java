package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.security.spec.ECField;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spnr_profile;
    ImageView iv_study,imageView, main_iv_info;
    Button tv_createProfile;
    Button btn_confirm;
    String usernameSelected;
    TextView textView;
    DataBaseHelper db;
    MediaPlayer mp;
    Context context;
    GlobalFunctions gf;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        init();

    }

    private void init() {

        context = this;
        db = new DataBaseHelper(context);
        usernameSelected = "";



        //load spinner data


        //show the group of views for welcome
        imageView = findViewById(R.id.imageView);
        iv_study = (ImageView) findViewById(R.id.iv_study);
        main_iv_info= findViewById(R.id.main_iv_info);
        imageView.setVisibility(View.VISIBLE);
        iv_study.setVisibility(View.VISIBLE);
        main_iv_info.setVisibility(View.VISIBLE);
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




        //======================= FOR BGM ===========================
        gf = new GlobalFunctions(this);
        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bensound_bgm);
        try{
            gf.playBGM(mp, mediaPath, "0");
        }
        catch (Exception e){
            e.printStackTrace();

        }





        //=================================register listeners ====================================

        main_iv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog box
                openDialog();
            }
        });

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
                    main_iv_info.setVisibility(View.GONE);



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
            main_iv_info.setVisibility(View.VISIBLE);

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

    //-----------------------------------------OPEN CUSTOM DIALOG--------------------------------------
    private void openDialog(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.tip_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tip_dialog_title = dialog.findViewById(R.id.tip_dialog_title);
        tip_dialog_title.setText("Titser.ko");
        ImageView tip_iv_animal_pic = dialog.findViewById(R.id.tip_iv_animal_pic);
        tip_iv_animal_pic.setImageResource(R.drawable.vector_animal_1);
        TextView tip_tv_title = dialog.findViewById(R.id.tip_tv_title);
        tip_tv_title.setText("Handa ka na ba matuto mag-Ingles?");
        TextView tip_tv_desc = dialog.findViewById(R.id.tip_tv_desc);
        tip_tv_desc.setText("Ang \"Titser.ko\" ay isang OFFLINE na application na dinevelop upang maturuan ang mga bata ng mga simpleng bokubolaryo ng wikang Ingles. Mangolekta ng mga badge at mag-unlock ng mga Achievements upang maipagmalaki sa iyong mga magulang at mga kaibigan. Matuto habang naglalaro gamit ang Titser.ko!");
        Button tip_btn_cancel = dialog.findViewById(R.id.tip_btn_cancel);
        tip_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }







}