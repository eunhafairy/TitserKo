package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spnr_profile;
    ImageView iv_study;
    LinearLayout ll_landing, ll_profile;
    TextView tv_createProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        init();

    }

    private void init(){

        spnr_profile = (Spinner) findViewById(R.id.spnr_profile);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_profile.setAdapter(adapter);
        spnr_profile.setOnItemSelectedListener(this);
        iv_study = (ImageView) findViewById(R.id.iv_study);
        ll_landing = (LinearLayout) findViewById(R.id.ll_landing);
        ll_profile = (LinearLayout) findViewById(R.id.ll_profile);
        tv_createProfile = (TextView) findViewById(R.id.tv_createProfile);

        iv_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_landing.setVisibility(View.GONE);
                ll_profile.setVisibility(View.VISIBLE);
            }
        });

        tv_createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, tk_register.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}