package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class tk_profile extends AppCompatActivity {


    ImageView btn_profile_back;
    TextView tv_profile_name, tv_profile_name2, tv_profile_age, tv_profile_birthday;
    String name, dateTime, age;
    Context c;
    DataBaseHelper db;
    Date bday;
    FlowLayout flowLayout;
    List<String> imagePaths;

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
        flowLayout = findViewById(R.id.fl_profile);
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

        populateProfileBadge();

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

    private void populateProfileBadge(){

        imagePaths = db.getProfileBadges(name);
        int ctr = imagePaths.size();

        int sizeInDP = 20;
        int sizeBadges = 60;

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());
        int sizeBadgeIndp =  (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeBadges, getResources()
                        .getDisplayMetrics());



        for(int i = 0; i < ctr; i++){

            ImageView imageView = new ImageView(c);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(sizeBadgeIndp,sizeBadgeIndp));

           LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams) imageView.getLayoutParams();
           layoutParams.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
           imageView.setLayoutParams(layoutParams);

            Uri imageUri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePaths.get(i));
            imageView.setImageURI(imageUri);

            if(imagePaths.get(i).equals("lockedbadge")){

                imageView.setAlpha(0.5f);
            }
            flowLayout.addView(imageView);

        }


    }


}