package com.bsit3omagitech.titserko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class tk_achievements extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    String name;
    RecyclerView achieveRv;
    List<String> titles, imagePaths, achievementIds, desc;
    List<Boolean> unlocked;
    Context c;
    DataBaseHelper db;
    Dialog dialog;
    ImageView iv_achieve_back;
    NavigationView achievement_navigationView;
    DrawerLayout achievement_drawerLayout;
    GlobalFunctions gf;
    Toolbar toolbar;
    MediaPlayer mp;
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
        gf = new GlobalFunctions(c);
        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + c.getPackageName() + "/" + R.raw.bgm_achieve_1);
        try {
            gf.playBGM(mp, mediaPath, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new Dialog(c);
        titles = new ArrayList<>();
        imagePaths = new ArrayList<>();
        achievementIds = new ArrayList<>();
        desc = new ArrayList<>();
        unlocked = new ArrayList<>();
        db = new DataBaseHelper(this);
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

        //navigation

        achievement_navigationView = findViewById(R.id.achievement_nav_view);
        achievement_navigationView.setCheckedItem(R.id.nav_achievements);
        toolbar = findViewById(R.id.achievement_toolbar);
        achievement_drawerLayout = findViewById(R.id.achievement_drawer_layout);
        setSupportActionBar(toolbar);


        achievement_navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, achievement_drawerLayout, toolbar, R.string.navigation_drawer_open ,R.string.navigation_drawer_close);
        achievement_drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        achievement_navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) c);



        //-------------------------------------------BACK BUTTON---------------------------------
        iv_achieve_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achievement_drawerLayout.openDrawer(Gravity.LEFT);
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
        Button btn_confirm_achieve_equip = dialog.findViewById(R.id.btn_confirm_achieve_equip);
        ImageView iv_achieve_dialog = dialog.findViewById(R.id.iv_achieve_dialog);

        tv_title_achieve_dialog.setText(titles.get(pos));
        tv_desc_achieve_dialog.setText(desc.get(pos));

        Uri imageUri =Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePaths.get(pos));
        iv_achieve_dialog.setImageURI(imageUri);


        //check if current achievement is equipped
        if(db.isEquipped(name, achievementIds.get(pos))){
            btn_confirm_achieve_equip.setText("Equipped");
            btn_confirm_achieve_equip.setClickable(false);
            btn_confirm_achieve_equip.setAlpha(0.5f);

        }
        else{

            if(!unlocked.get(pos)){

                Uri lockUri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/lockedbadge");
                iv_achieve_dialog.setImageURI(lockUri);
                btn_confirm_achieve_equip.setText("Equip Badge");
                btn_confirm_achieve_equip.setClickable(false);
                btn_confirm_achieve_equip.setAlpha(0.5f);

            }
            else{

                btn_confirm_achieve_equip.setText("Equip Badge");
                btn_confirm_achieve_equip.setClickable(true);
                btn_confirm_achieve_equip.setAlpha(1);
                btn_confirm_achieve_equip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //equip the badge
                        mp.stop();
                        db.updateBadge(name, achievementIds.get(pos));
                        Intent intent = new Intent(c, tk_loading.class);
                        intent.putExtra("username", name);
                        intent.putExtra("actName", "dashboard");
                        startActivity(intent);
                        finish();
                        Toast.makeText(c, "Successfully changed badge.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }

        btn_confirm_achieve_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mp.start();
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

        mp.stop();
        Intent i1 = new Intent(c, TkDashboardActivity.class);
        i1.putExtra("username", name);
        startActivity(i1);
        finish();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            //---------Home----------
            case R.id.nav_home:
                mp.stop();
                Intent i1 = new Intent(this, TkDashboardActivity.class);
                i1.putExtra("username", name);
                startActivity(i1);
                finish();
                break;

            //--------Profile------------
            case R.id.nav_profile:
                mp.stop();
                Intent i2 = new Intent(this, tk_profile.class);
                i2.putExtra("username", name);
                startActivity(i2);
                finish();

                break;

            case R.id.nav_settings:
                mp.stop();
                Intent i5 = new Intent(this, tk_settings.class);
                i5.putExtra("username", name);
                startActivity(i5);
                finish();
                break;

            //--------Achievements----------
            case R.id.nav_achievements:
                break;

            case R.id.nav_logout:
                mp.stop();
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                finish();
                break;

            case R.id.nav_stats:
                mp.stop();
                Intent i4 = new Intent(this, tk_statistics.class);
                i4.putExtra("username", name);
                startActivity(i4);
                finish();
                break;


        }
        achievement_drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }




}