package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class tk_profile extends AppCompatActivity {


    Button btn_profile_back;
    TextView tv_profile_name;
    String name;
    Context c;
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

        c = this;
        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");


    }

    private void reg(){

        // --------------------------------------------- REGISTER LISTENERS AND FUNCTIONS ----------------------------------------------
        tv_profile_name.setText(name);

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
}