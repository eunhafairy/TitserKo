package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.internal.ContextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TkDashboardActivity extends AppCompatActivity {

    //declaration
    TextView db_tv_name;
    String name;
    RecyclerView myRv;
    List<String> lessonList, lessonTranslated, lessonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tk_dashboard);

        //initialize
        init();
    }



    private void init(){

        db_tv_name = (TextView) findViewById(R.id.db_tv_name);

        myRv = (RecyclerView) findViewById(R.id.myRv);

        //intent
        //get intent
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        //set values
        db_tv_name.setText(name);



        //try json object
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");
            lessonList = new ArrayList<String>();
            lessonTranslated = new ArrayList<String>();
            lessonId = new ArrayList<String>();
            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String _lessonName = jo_inside.getString("lesson_name");
                String _lessonTranslated =  jo_inside.getString("lesson_name_translate");
                String _lessonId =  jo_inside.getString("lesson_id");

                //add to lists
                lessonList.add(_lessonName);
                lessonTranslated.add(_lessonTranslated);
                lessonId.add(_lessonId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myAdapter adapter = new myAdapter(this, lessonList);
        myRv.setAdapter(adapter);
        myRv.setLayoutManager(new LinearLayoutManager(this));


        adapter.setIndividualScreenListener(new myAdapter.OnIndividualScreen() {
            @Override
            public void convertViewOnIndividualScreen(int position) {
                String lesson = lessonList.get(position);
                String _lessonTranslated = lessonTranslated.get(position);
                String _lessonId = lessonId.get(position);
                Intent i = new Intent(TkDashboardActivity.this, landing.class);
                i.putExtra("lesson", lesson);
                i.putExtra("lessonTranslated", _lessonTranslated);
                i.putExtra("lessonId", _lessonId);
                startActivity(i);

            }
        });


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("lessons.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}