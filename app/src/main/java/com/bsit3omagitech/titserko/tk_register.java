package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class tk_register extends AppCompatActivity {

    Button btn_confirmProfile, btn_cancelProfile;
    EditText et_name, et_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_register);


        //intialize variables
        init();
    }

    private void init(){

        //edit text
        et_name = findViewById(R.id.et_name);
        et_date = findViewById(R.id.et_date);

        //button
        btn_confirmProfile = findViewById(R.id.btn_confirmProfile);
        btn_cancelProfile = findViewById(R.id.btn_cancelProfile);

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
                Toast.makeText(tk_register.this, "Success is: " + success, Toast.LENGTH_SHORT).show();
            }
        });

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