package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaCasException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LessonProper extends AppCompatActivity {


    ConstraintLayout parent;
    TextView tv_lp_study;
    GlobalFunctions gf;
    DataBaseHelper db;
    LinearLayout ll_parent;
    ImageView btn_lp_finish;
    TextView tv_lp_description;
    ImageView btn_lp_previous, btn_lp_next, picture;
    String lessonName, lessonId, lessonTranslated, username;
    JSONObject targetLessonObject;
    JSONArray partsArray;
    ProgressBar lessonProgress;
    String TAG = "debug";
    Context c;
    MediaPlayer bgmMp;
    List<Integer> lessonBgms;
    boolean isOrdered = false;
    int sizeWidth, sizeHeight, sizeWidthIndp, sizeHeightIndp;
    int index;
    float progress, maxIndex;
    MediaPlayer instruction_mp = new MediaPlayer(), mp = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_proper);

        //initialize variables and register listeners
        init();
        reg();
    }

    private void init(){

        bgmMp = new MediaPlayer();
        lessonBgms = new ArrayList<>();
        lessonBgms.add(R.raw.bgm_lesson_1);
        lessonBgms.add(R.raw.bgm_lesson_2);
        lessonBgms.add(R.raw.bgm_lesson_3);
        index = 0;
        c = this;
        gf = new GlobalFunctions(c);
        //initalialize button
        btn_lp_finish = findViewById(R.id.btn_lp_finish);
        btn_lp_finish.setVisibility(View.GONE);

        //initialize textview
        tv_lp_study = findViewById(R.id.tv_lp_study);
        tv_lp_description = (TextView) findViewById(R.id.tv_lp_description);
        lessonProgress = (ProgressBar) findViewById(R.id.lessonProgress);
        lessonProgress.setProgress(index);
        //initialize nav and image view
        btn_lp_previous = (ImageView) findViewById(R.id.btn_lp_previous);
        btn_lp_next = (ImageView) findViewById(R.id.btn_lp_next);
        btn_lp_next.setVisibility(View.VISIBLE);
        picture = (ImageView) findViewById(R.id.iv_imageView);

        btn_lp_previous.setVisibility(View.INVISIBLE);
        btn_lp_next.setClickable(true);

        //initialize layout
        ll_parent = (LinearLayout) findViewById(R.id.ll_btnParent);
        parent = findViewById(R.id.linearLayout13);

        //Intent
        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lessonName");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        username = intent.getStringExtra("username");

        //=========================================run the update query at the start to track progress===================================
        db = new DataBaseHelper(getApplicationContext());
        db.updateLessonProgress(index, username, lessonId);

        //=======================================size in dp==========================================
        sizeHeight = gf.convertToDp(60);
        sizeWidth = gf.convertToDp(250);

        //=======================bgm=============================
        Random random = new Random();
        int randomNum = random.nextInt(3);
        gf = new GlobalFunctions(c);
        Uri mediaPath = Uri.parse("android.resource://" + c.getPackageName() + "/" + lessonBgms.get(randomNum));
        try {
            gf.playBGM(bgmMp, mediaPath, username);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //================================locate the JSON Object of selected lesson================================
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);

                if( jo_inside.getString("lesson_id").equals(lessonId) ){

                    targetLessonObject = jo_inside;
                    if (jo_inside.has("ordered")) isOrdered = true;
                    break;

                }


            }

         //get the parts array
            if(isOrdered){
                //shuffle
                partsArray = targetLessonObject.getJSONArray("parts");
            }
            else{

                partsArray = shuffleJsonArray(targetLessonObject.getJSONArray("parts"));
            }

         maxIndex = partsArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //first description
        try{

            tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));


        }
        catch (JSONException e){
            e.printStackTrace();
        }

        //first choices
        try{
            generateChoices(partsArray);

        }
        catch (JSONException e){

            e.printStackTrace();
        }


        //progress bar
        progress = (float) (index/(maxIndex-1)) * 100;
        lessonProgress.setProgress((int)progress);

    }
    private void reg() {

        btn_lp_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //next part

                //check if index is at last part
                if((index+1) == partsArray.length()){
                    btn_lp_next.setVisibility(View.GONE);
                    btn_lp_finish.setVisibility(View.VISIBLE);
                    btn_lp_previous.setVisibility(View.VISIBLE);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(parent);
                    constraintSet.connect(tv_lp_study.getId(), ConstraintSet.END, btn_lp_finish.getId(), ConstraintSet.START);
                    constraintSet.applyTo(parent);

                }
                else{
                    index++;
                    btn_lp_next.setVisibility(View.VISIBLE);
                    btn_lp_finish.setVisibility(View.GONE);
                    btn_lp_previous.setVisibility(View.VISIBLE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(parent);
                    constraintSet.connect(tv_lp_study.getId(), ConstraintSet.END, btn_lp_next.getId(), ConstraintSet.START);
                    constraintSet.applyTo(parent);

                    //hide next button if index is last
                    if((index+1) == partsArray.length()) {
                        btn_lp_next.setVisibility(View.GONE);
                        btn_lp_finish.setVisibility(View.VISIBLE);
                        ConstraintSet constraintSet1 = new ConstraintSet();
                        constraintSet1.clone(parent);
                        constraintSet1.connect(tv_lp_study.getId(), ConstraintSet.END, btn_lp_finish.getId(), ConstraintSet.START);
                        constraintSet1.applyTo(parent);
                    }

                    try {
                        tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                try{
                    generateChoices(partsArray);

                }
                catch (JSONException e){

                    e.printStackTrace();
                }

                db.updateLessonProgress(index, username, lessonId);
                progress = (float) (index/(maxIndex-1)) * 100;
                lessonProgress.setProgress((int)progress);


            }
        });

        btn_lp_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //previous part
                ConstraintSet constraintSet1 = new ConstraintSet();
                constraintSet1.clone(parent);
                constraintSet1.connect(tv_lp_study.getId(), ConstraintSet.END, btn_lp_next.getId(), ConstraintSet.START);
                constraintSet1.applyTo(parent);
                if(index == 0) {
                    btn_lp_previous.setVisibility(View.INVISIBLE);
                    btn_lp_finish.setVisibility(View.GONE);
                    btn_lp_next.setVisibility(View.VISIBLE);

                }
                else{
                    index--;
                    btn_lp_previous.setVisibility(View.VISIBLE);
                    btn_lp_finish.setVisibility(View.GONE);
                    btn_lp_next.setVisibility(View.VISIBLE);

                    //hide previous button if index is 0
                    if(index == 0)  btn_lp_previous.setVisibility(View.INVISIBLE);

                    if((index+1) == partsArray.length()){
                        btn_lp_finish.setVisibility(View.VISIBLE);
                        btn_lp_next.setVisibility(View.GONE);


                    }


                    try {
                        tv_lp_description.setText(partsArray.getJSONObject(index).getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                try{
                    generateChoices(partsArray);

                }
                catch (JSONException e){

                    e.printStackTrace();
                }

                db.updateLessonProgress(index, username, lessonId);
                progress = (float) (index/(maxIndex-1)) * 100;
                lessonProgress.setProgress((int)progress);
            }


        });

        btn_lp_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish and return to landing page
                db.refreshAllStars(username);
                Intent intent = new Intent(LessonProper.this, tk_loading.class);
                intent.putExtra("lesson", lessonName);
                intent.putExtra("lessonId", lessonId);
                intent.putExtra("lessonTranslated", lessonTranslated);
                intent.putExtra("username", username);
                intent.putExtra("actName", "landing");
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();
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

    private void generateChoices(JSONArray choicesArray) throws JSONException{

        ll_parent.removeAllViews();

        try{

            choicesArray = partsArray.getJSONObject(index).getJSONArray("choices");
            int choiceCount = choicesArray.length();

            //------------------------audio for instruction---------------------------
            //name of file
            String instruction_url = partsArray.getJSONObject(index).getString("instruction_audio");
            String lesson_path = "lesson"+lessonId+"/"+instruction_url;

            tv_lp_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      gf.playAudio(instruction_mp, lesson_path, username);
                }
            });

            //-------------------------image for imageview-----------------------
            String image_name = partsArray.getJSONObject(index).getString("image_src");
            String image_path = "lesson" + lessonId + "/"+image_name + ".png";
            gf.setImage(picture,image_path);

            //------------------------------generate button choices---------------------------
            for(int i = 0; i < choiceCount; i++){

                //name of file
                String url = choicesArray.getJSONObject(i).getString("audio_src");
                String label_path = "lesson"+lessonId+"/"+url;
                Button btn_choice = new Button(this);
                btn_choice.setText(choicesArray.getJSONObject(i).getString("label"));
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/finger_paint.ttf");
                btn_choice.setTypeface(face);
                btn_choice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                btn_choice.setBackgroundResource(R.drawable.vector_lesson_btn);
                   btn_choice.setTextColor(ContextCompat.getColor(this, R.color.white));
                   btn_choice.setShadowLayer(1.5f,1.3f,1.6f, R.color.customBrown);
                btn_choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gf.playAudio(mp, label_path, username);
                    }
                });
                ll_parent.addView(btn_choice);

            }

        }catch(JSONException e){

            e.printStackTrace();
        }

    }



    //--------------------SHUFFLE LESSON ARRAY-----------------------

    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Study?")
                .setMessage("Are you sure you want to quit? Your progress will not be saved.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(c, tk_loading.class);
                        i.putExtra("username", username);
                        i.putExtra("actName", "dashboard");
                        startActivity(i);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Apply activity transition
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        })
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}