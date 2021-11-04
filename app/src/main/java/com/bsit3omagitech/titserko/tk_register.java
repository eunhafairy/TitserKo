package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class tk_register extends AppCompatActivity {


    //var declaration
    Button btn_confirmProfile, btn_cancelProfile;
    EditText et_name, et_date;
    DatePickerDialog datePickerDialog;
    Context c = this;
    String name = "";
    Dialog dialog;

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
    }

    private void reg(){

        //for date picker
        datePickerDialog = new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_date.setText((month+1)+"/"+(dayOfMonth)+"/"+(year));
            }
        },1990,1,1);

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        //for registering new profile
        btn_confirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create profile
                UserModel userModel;
                int ctr = et_name.getText().length();
                name  =  et_name.getText().toString();
                name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                DataBaseHelper dbHelper = new DataBaseHelper(tk_register.this);
                if(dbHelper.checkExisting(name)){
                    openDialog("Error", "The name should be unique.");
                    et_name.setText("");

                }
                else if(ctr <= 16){

                    try {
                        userModel = new UserModel(-1,name, et_date.getText().toString());


                    }
                    catch (Exception e){

                        Toast.makeText(tk_register.this, "Error", Toast.LENGTH_SHORT).show();
                        userModel = new UserModel(-1, "error", "error");
                    }


                    boolean success = dbHelper.addOne(userModel);
                    if(success){
                        dbHelper.createAllLessonProgress(name);
                        dbHelper.createAllAchievements(name);
                        Intent intent = new Intent(tk_register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        openDialog("Error", "Something went wrong. Make sure you choose a unique name.");
                        et_name.setText("");
                    }


                }

                else{
                    //show error
                    openDialog("Error", "The name should be less than 16 characters.");
                    et_name.setText("");
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
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}