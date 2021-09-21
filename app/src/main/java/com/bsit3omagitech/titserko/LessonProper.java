package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.time.chrono.MinguoDate;
import java.util.ArrayList;

public class LessonProper extends AppCompatActivity {

    Button btn_lp_finish;
    TextView tv_lp_description;
    ImageView btn_lp_previous, btn_lp_next;
    String lessonName, lessonId, lessonTranslated;
    JSONObject targetLessonObject;
    JSONArray partsArray;
    String TAG = "debug";
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_proper);

        //initialize variables and register listeners
        init();
        reg();
    }

    private void init(){

        index = 0;
        //initalialize button
        btn_lp_finish = (Button) findViewById(R.id.btn_lp_finish);
        btn_lp_finish.setVisibility(View.GONE);
        //initialize textview
        tv_lp_description = (TextView) findViewById(R.id.tv_lp_description);

        //initialize nav
        btn_lp_previous = (ImageView) findViewById(R.id.btn_lp_previous);
        btn_lp_next = (ImageView) findViewById(R.id.btn_lp_next);

        btn_lp_previous.setVisibility(View.INVISIBLE);
        btn_lp_next.setClickable(true);

        //Intent
        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lessonName");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        //locate the JSON Object of selected lesson
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);

                if( jo_inside.getString("lesson_id").equals(lessonId) ){


                    targetLessonObject = jo_inside;
                    break;
                }


            }
            //get the parts array
         partsArray = targetLessonObject.getJSONArray("parts");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //first description
        try{

            tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));

            //flag it for progress

        //  partsArray.getJSONObject(index).put("flag", true);
          Log.d(TAG ,partsArray.getJSONObject(index).toString());

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("Index: ",index + "");

    }
    private void reg() {

        btn_lp_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //next part

                //check if index is at last part
                if((index+1) == partsArray.length()){
                    btn_lp_next.setVisibility(View.INVISIBLE);
                    btn_lp_finish.setVisibility(View.VISIBLE);
                    btn_lp_previous.setVisibility(View.VISIBLE);

                }
                else{
                    index++;
                    btn_lp_next.setVisibility(View.VISIBLE);
                    btn_lp_finish.setVisibility(View.INVISIBLE);
                    btn_lp_previous.setVisibility(View.VISIBLE);

                    //hide next button if index is last
                    if((index+1) == partsArray.length()) {
                        btn_lp_next.setVisibility(View.INVISIBLE);
                        btn_lp_finish.setVisibility(View.VISIBLE);
                    }

                    try {
                        tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }



                Log.d("Index: ",index + "");

            }
        });

        btn_lp_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //previous part

                if(index == 0) {
                    btn_lp_previous.setVisibility(View.INVISIBLE);
                    btn_lp_finish.setVisibility(View.INVISIBLE);
                    btn_lp_next.setVisibility(View.VISIBLE);
                }
                else{
                    index--;
                    btn_lp_previous.setVisibility(View.VISIBLE);
                    btn_lp_finish.setVisibility(View.VISIBLE);
                    btn_lp_next.setVisibility(View.VISIBLE);

                    //hide previous button if index is 0
                    if(index == 0)  btn_lp_previous.setVisibility(View.INVISIBLE);

                    if((index+1) == partsArray.length()){
                        btn_lp_finish.setVisibility(View.VISIBLE);
                        btn_lp_next.setVisibility(View.INVISIBLE);


                    }


                    try {
                        tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                Log.d("Index: ",index + "");

            }


        });

        btn_lp_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish and return to landing page
                Intent intent = new Intent(LessonProper.this, landing.class);
                intent.putExtra("lesson", lessonName);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("lessonTranslated", lessonTranslated);
                startActivity(intent);
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