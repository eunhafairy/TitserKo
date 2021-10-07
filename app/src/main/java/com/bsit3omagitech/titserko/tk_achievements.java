package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class tk_achievements extends AppCompatActivity {

    String name;
    RecyclerView achieveRv;
    List<String> titles, imagePaths, achievementIds;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_achievements);

    init();
    reg();

    }

    private void init(){
        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        // --------------------------------------------- INITIALIZE ----------------------------------------------
        achieveRv = findViewById(R.id.rv_achieve);
        c = this;
        titles = new ArrayList<>();
        imagePaths = new ArrayList<>();
        achievementIds = new ArrayList<>();
        getAchievementList();
        //-------------------------------------------GET DATA FROM JSON AND DATABASE---------------------------------
        AchievementAdapter adapter = new AchievementAdapter(this, titles, imagePaths, achievementIds, name);
        achieveRv.setAdapter(adapter);
        achieveRv.setLayoutManager(new LinearLayoutManager(this));


    }
    private void reg(){


    }

    // ----------------------------------------------- FOR DATA LIST OF RECYCLER VIEW ------------------------------------------
    public void getAchievementList(){


        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("achievements");

            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String title = jo_inside.getString("achieve_name");
                String path =  jo_inside.getString("achieve_img");
                String id =  jo_inside.getString("achieve_id");

                //add to lists
                titles.add(title);
                imagePaths.add(path);
                achievementIds.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //--------------------------------------------JSON PARSER------------------------------------------
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("achievements.json");
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, TkDashboardActivity.class);
        intent.putExtra("username", name);
        startActivity(intent);
        finish();

    }
}