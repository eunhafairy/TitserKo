package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class tk_credit extends AppCompatActivity {

    ImageView btn_credit_back;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_credit);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        btn_credit_back= findViewById(R.id.btn_credit_back);
        btn_credit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tk_credit.this, tk_profile.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            }
        });


    }
}