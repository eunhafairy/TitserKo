package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class tk_profile extends AppCompatActivity {


    ImageView btn_profile_back;
    TextView tv_profile_name, tv_profile_name2, tv_profile_age, tv_profile_birthday;
    String name, dateTime, age;
    Context c;
    DataBaseHelper db;
    Date bday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_profile);


        init();
        reg();
    }

    private  void init(){

        // --------------------------------------------- INITIALIZE ----------------------------------------------
        btn_profile_back = findViewById(R.id.btn_profile_back);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        tv_profile_name2 = findViewById(R.id.tv_profile_name2);
        tv_profile_age = findViewById(R.id.tv_profile_age);
        tv_profile_birthday = findViewById(R.id.tv_profile_birthday);

        c = this;
        db = new DataBaseHelper(c);

        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        bday = db.getUserBirthday(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = bday;
        dateTime = dateFormat.format(date);
        age = getAge(dateTime) + "";

    }

    private void reg(){

        // --------------------------------------------- REGISTER LISTENERS AND FUNCTIONS ----------------------------------------------
        tv_profile_name.setText(name);
        tv_profile_name2.setText(name);
         tv_profile_birthday.setText(dateTime);
        tv_profile_age.setText(age);

        btn_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to dashboard
                Intent i = new Intent(c, TkDashboardActivity.class);
                i.putExtra("username", name);
                startActivity(i);
                finish();
            }
        });


    }

    public int getAge(String dateTime){

        int age = 0;

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null){
        Log.d("dada", "null");
            return 0;
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);
        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }




        return age;



    }
}