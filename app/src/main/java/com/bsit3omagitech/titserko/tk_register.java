package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class tk_register extends AppCompatActivity {


    //var declaration
    Button btn_confirmProfile, btn_cancelProfile;
    EditText et_name, et_date;
    DatePickerDialog datePickerDialog;
    Context c = this;

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
                try {
                    userModel = new UserModel(-1, et_name.getText().toString(), et_date.getText().toString());
                    Toast.makeText(tk_register.this, "Customer name: "+ userModel.getName(), Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){

                    Toast.makeText(tk_register.this, "Error", Toast.LENGTH_SHORT).show();
                    userModel = new UserModel(-1, "error", "error");
                }

                DataBaseHelper dbHelper = new DataBaseHelper(tk_register.this);
                boolean success = dbHelper.addOne(userModel);
                if(success){
                    dbHelper.createAllLessonProgress(et_name.getText().toString());
                    Toast.makeText(tk_register.this, "Success is: " + success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(tk_register.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(tk_register.this, "Something went wrong. Make sure you choose a unique name.", Toast.LENGTH_LONG).show();
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
}