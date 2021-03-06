package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class tk_register extends AppCompatActivity {


    //var declaration
    Button btn_confirmProfile, btn_cancelProfile;
    EditText et_name, et_date;
    DatePickerDialog datePickerDialog;
    Context c = this;
    String name = "";
    Dialog dialog;
    ProgressBar register_progressBar;
    MediaPlayer mp;
    GlobalFunctions gf;
    int _year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_register);


        //intialize variables and registering listeners
        init();
        reg();
    }

    private void init(){

        //edit texts
        et_name = findViewById(R.id.et_name);
        et_date = findViewById(R.id.et_date);
        //buttons
        btn_confirmProfile = findViewById(R.id.btn_confirmProfile);
        btn_cancelProfile = findViewById(R.id.btn_cancelProfile);
        dialog = new Dialog(c);

        //progress bar
        register_progressBar = findViewById(R.id.register_progressBar);
        register_progressBar.setVisibility(View.GONE);


        //mediaplayer
        mp = new MediaPlayer();
        gf = new GlobalFunctions(c);
        Uri mediaPath = Uri.parse("android.resource://" + c.getPackageName() + "/" + R.raw.bgm_stats_1);
        try {
            gf.playBGM(mp, mediaPath, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reg(){

        //for date picker


        datePickerDialog = new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_date.setText((month+1)+"/"+(dayOfMonth)+"/"+(year));
                _year = year;
            }
        },2010,1,1);

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog.show();
            }
        });


        //for registering new profile
        btn_confirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create profile
                UserModel userModel;

                //disable button
                btn_confirmProfile.setClickable(false);
                register_progressBar.setVisibility(View.VISIBLE);

                name = et_name.getText().toString();
                name.trim();
                if (checkIfEmpty()) {
                    openDialog("Error", "Please complete all fields.");

                } else {


                    //check length
                    if(name.length() == 1){

                        name = name.toUpperCase();
                    }
                    else if (name.length() > 1) {
                        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    }
                    DataBaseHelper dbHelper = new DataBaseHelper(tk_register.this);

                    int ctr = name.length();

                    //check if unique
                    if (dbHelper.checkExisting(name)) {
                        openDialog("Error", "The name should be unique.");
                        btn_confirmProfile.setClickable(true);
                        register_progressBar.setVisibility(View.GONE);
                        et_name.setText("");

                    }
                    else if(testIfThisYear()){

                        openDialog("Error", "Birth year shouldn't be equals to this year.");
                        et_name.setText("");
                        et_date.setText("");
                        btn_confirmProfile.setClickable(true);
                        register_progressBar.setVisibility(View.GONE);
                    }

                    //check if name length is less than one
                    else if (ctr < 1) {
                        openDialog("Error", "Enter a valid name.");
                        et_name.setText("");
                        btn_confirmProfile.setClickable(true);
                        register_progressBar.setVisibility(View.GONE);
                    }

                    //valid
                    else if (ctr <= 16) {

                        try {
                            userModel = new UserModel(-1, name, et_date.getText().toString());


                        } catch (Exception e) {

                            Toast.makeText(tk_register.this, "Error", Toast.LENGTH_SHORT).show();
                            userModel = new UserModel(-1, "error", "error");
                            btn_confirmProfile.setClickable(true);
                            register_progressBar.setVisibility(View.GONE);
                        }


                        boolean success = dbHelper.addOne(userModel);
                        if (success) {
                            dbHelper.createAllLessonProgress(name);
                            dbHelper.createAllAchievements(name);
                            Intent intent = new Intent(tk_register.this, tk_loading.class);
                            intent.putExtra("actName", "main");
                            startActivity(intent);
                            mp.stop();
                            finish();
                        } else {
                            openDialog("Error", "Something went wrong.");
                            et_name.setText("");
                            btn_confirmProfile.setClickable(true);
                            register_progressBar.setVisibility(View.GONE);
                        }


                    } else {
                        //show error
                        openDialog("Error", "The name should be less than 17 characters.");
                        et_name.setText("");
                        btn_confirmProfile.setClickable(true);
                        register_progressBar.setVisibility(View.GONE);
                    }


                }
            }
        });


        //cancel button
        btn_cancelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(tk_register.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
// ------------------------------------ FOR DIALOG ------------------------------------
    public void openDialog( String title, String message){

        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_title_achieve_dialog = dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_desc_achieve_dialog = dialog.findViewById(R.id.tv_dialog_message);
        Button btn_confirm_achieve_dialog = dialog.findViewById(R.id.btn_dialog_okay);

        tv_title_achieve_dialog.setText(title);
        tv_desc_achieve_dialog.setText(message);
        btn_confirm_achieve_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               btn_confirmProfile.setClickable(true);
                register_progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        dialog.show();

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


    private boolean checkIfEmpty(){
        String name = et_name.getText().toString().trim();
        String bday = et_date.getText().toString().trim();

        if(name.length() < 1 || bday.length() < 1){
            return true;

        }
        else{
            return false;
        }
    }

    private boolean testIfThisYear(){
        int year = _year;
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        if(year >= thisYear){

            return true;

        }
        else{
            return false;

        }


    }

}