package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    List<String> titles, imagePaths, achievementIds, desc;
    List<Boolean> unlocked;
    Context c;
    DataBaseHelper db;
    Dialog dialog;
    ImageView iv_achieve_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_achievements);

    init();


    }

    private void init(){
        // --------------------------------------------- INTENTS ---------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        // --------------------------------------------- INITIALIZE ----------------------------------------------

        iv_achieve_back = findViewById(R.id.iv_achieve_back);
        achieveRv = findViewById(R.id.rv_achieve);
        c = this;
        dialog = new Dialog(c);
        titles = new ArrayList<>();
        imagePaths = new ArrayList<>();
        achievementIds = new ArrayList<>();
        desc = new ArrayList<>();
        unlocked = new ArrayList<>();
        db = new DataBaseHelper(this);
        db.refreshAchievements(name);
        getAchievementList();
        //-------------------------------------------GET DATA FROM JSON AND DATABASE---------------------------------
        AchievementAdapter adapter = new AchievementAdapter(this, titles, imagePaths, achievementIds, name, unlocked);
        achieveRv.setAdapter(adapter);
        achieveRv.setLayoutManager(new LinearLayoutManager(this));

        adapter.setIndividualScreenListener(new AchievementAdapter.OnIndividualDialog() {
            @Override
            public void convertViewOnIndividualScreen(int position) {

                openDialog(position);
            }
        });


        //-------------------------------------------BACK BUTTON---------------------------------
        iv_achieve_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tk_achievements.this, TkDashboardActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
                finish();
            }
        });



    }

    //-----------------------------------------OPEN CUSTOM DIALOG--------------------------------------
    private void openDialog(int pos){

        dialog.setContentView(R.layout.achievement_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_title_achieve_dialog = dialog.findViewById(R.id.tv_title_achieve_dialog);
        TextView tv_desc_achieve_dialog = dialog.findViewById(R.id.tv_desc_achieve_dialog);
        Button btn_confirm_achieve_dialog = dialog.findViewById(R.id.btn_confirm_achieve_dialog);
        ImageView iv_achieve_dialog = dialog.findViewById(R.id.iv_achieve_dialog);

        tv_title_achieve_dialog.setText(titles.get(pos));
        tv_desc_achieve_dialog.setText(desc.get(pos));

        Uri imageUri =Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePaths.get(pos));
        iv_achieve_dialog.setImageURI(imageUri);

        btn_confirm_achieve_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

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
                String description = jo_inside.getString("achieve_desc");
                //add to lists
                titles.add(title);
                imagePaths.add(path);
                achievementIds.add(id);
                desc.add(description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //database
        for(int x = 0; x < achievementIds.size(); x++){

            unlocked.add(db.isUnlocked(achievementIds.get(x), name));

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