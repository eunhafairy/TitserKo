package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuizProper extends AppCompatActivity {


    DataBaseHelper db;
    int index, score;
    String lessonName, lessonId, lessonTranslated, username, selectedAnswer, TAG = "debug";
    ImageView iv_qp;
    TextView tv_qp_description;
    Button btn_lp_confirm;
    LinearLayout ll_btnParent;
    ImageView picture;
    JSONObject targetLessonObject;
    JSONArray quizArray;
    Context c;
    ProgressBar quizProgress;
    List<String> userAnswer;
    GlobalFunctions gf;
    ConstraintLayout parent;
    MediaPlayer mp;
    HashMap<String, String> hash_english, hash_tagalog, choices;
    int sizeWidth, sizeHeight;
    float progress, maxScore;
    boolean toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_proper);

        //initialization of variables and registration of listeners
        init();
        reg();

    }

    private void init() {

        c = this;
        maxScore = 0;
        gf = new GlobalFunctions(c);
        //select toggle
        toggle = false;
        //TextView
        tv_qp_description = (TextView) findViewById(R.id.tv_qp_description);

        //size of btns
        sizeWidth = gf.convertToDp(300);
        sizeHeight = gf.convertToDp(60);

        //Linear Layout
        ll_btnParent =  findViewById(R.id.ll_btnParent);
        btn_lp_confirm = findViewById(R.id.btn_lp_confirm);
        parent = findViewById(R.id.linearLayout14);
        //ImageView
        iv_qp = findViewById(R.id.iv_qp);

        //index and score
        index = 0;
        score = 0;

        //progressbar
        quizProgress = (ProgressBar) findViewById(R.id.quizProgress);

        //Intents
        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lessonName");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        username = intent.getStringExtra("username");

        //media player
        mp = new MediaPlayer();
        //selected answer
        selectedAnswer = "";
        userAnswer = new ArrayList<String>();
        //get JSON Object of selected Quiz
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);

                if( jo_inside.getString("lesson_id").equals(lessonId) ){
                    targetLessonObject = jo_inside;
                    maxScore = m_jArry.getJSONObject(i).getJSONArray("quiz").length();
                    break;
                }


            }

            hash_english = gf.getEnglishChoices(lessonId);
            hash_tagalog = gf.getTagalogChoices(lessonId);
            Log.d("hash", hash_english.size() + "");
            //get the parts array
            quizArray =  shuffleJsonArray(targetLessonObject.getJSONArray("quiz"));

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        //first question
        try {
            tv_qp_description.setText(quizArray.getJSONObject(index).getString("question"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //first choices
        try{
            generateChoices(quizArray);

        }
        catch (JSONException e){

            e.printStackTrace();
        }


        //progress
        progress = ((index+1)/(15f)) * 100;
        quizProgress.setProgress((int)progress);
        Log.d("progress", "progress: " + progress + " | index: "+ index);

        //Button


        btn_lp_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if text is confirm, check the answer, else go to the next part
                if(btn_lp_confirm.getText().equals("Confirm")){

                    userAnswer.add(selectedAnswer);
                    String correctAnswer = "";
                    Button _confirmButton = (Button) v;
                    //get the correct answer from JSON
                    try{
                        correctAnswer = quizArray.getJSONObject(index).getString("correct_answer");
                    }
                    catch (JSONException e){
                        e.printStackTrace();

                    }

                    //check if selected answer is correct
                    if(selectedAnswer.equals(correctAnswer)){

                        //get the toggled button and change colour
                        Button _toggled = (Button) v;
                        int ctr = ll_btnParent.getChildCount();
                        for(int i = 0; i < ctr; i++){

                            if(ll_btnParent.getChildAt(i).getTag().equals("toggled")) {
                                _toggled = (Button) ll_btnParent.getChildAt(i);
                            }
                            else{

                                ll_btnParent.getChildAt(i).setAlpha(0.5f);
                            }
                            ll_btnParent.getChildAt(i).setClickable(false);
                        }


                        _toggled.setBackgroundResource(R.drawable.button_correct); //change colour
                        if(isLast(index, quizArray)){

                            _confirmButton.setText("Proceed"); //change confirm text to next
                        }
                        else{
                            _confirmButton.setText("Next"); //change confirm text to next

                        }

                        score++;


                    }
                    else{

                        //get the toggled button and change colour
                        Button _toggled = (Button) v;
                        int ctr = ll_btnParent.getChildCount();
                        for(int i = 0; i < ctr; i++){

                            ll_btnParent.getChildAt(i).setClickable(false);

                            if(ll_btnParent.getChildAt(i).getTag().equals("toggled")) {
                                _toggled = (Button) ll_btnParent.getChildAt(i);
                            }
                            else{
                                if(((Button)ll_btnParent.getChildAt(i)).getText().equals(correctAnswer)){
                                    ll_btnParent.getChildAt(i).setBackgroundResource(R.drawable.button_correct);
                                    ((Button) ll_btnParent.getChildAt(i)).setTextColor(ContextCompat.getColor(c, R.color.darkGreen));
                                    ll_btnParent.getChildAt(i).setAlpha(0.8f);
                                }
                                else{

                                    ll_btnParent.getChildAt(i).setAlpha(0.5f);
                                }


                            }

                        }


                        _toggled.setBackgroundResource(R.drawable.button_incorrect); //change colour


                        if(isLast(index, quizArray)){

                            _confirmButton.setText("Proceed"); //change confirm text to next
                        }
                        else{
                            _confirmButton.setText("Next"); //change confirm text to next

                        }






                    }


                }

                //go to the next part
                else if (btn_lp_confirm.getText().equals("Next")){
                    if(!isLast(index, quizArray)){

                        index++;
                        try{
                            generateChoices(quizArray);

                        }
                        catch(JSONException e){

                            e.printStackTrace();
                        }

                        progress = (float) ((index+1)/(15f)) * 100;
                        quizProgress.setProgress((int) progress);
                        Log.d("progress", "progress: " + progress + " | index: "+ index);
                    }


                }
                //go to score page
                else{

                    //save user highscore
                    //run the update query at the start to track progress
                    db = new DataBaseHelper(getApplicationContext());
                    db.saveHighscore(username, lessonId, score);
                    db.refreshAllStars(username);
                    //go to score activity
                    Intent i = new Intent(c, tk_loading.class);
                    i.putExtra("Score", score);
                    i.putExtra("TotalScore", quizArray.length());
                    i.putExtra("lesson", lessonName);
                    i.putExtra("lessonId", lessonId);
                    i.putExtra("lessonTranslated", lessonTranslated);
                    i.putExtra("username", username);
                    i.putExtra("quizArr", quizArray.toString());
                    i.putStringArrayListExtra("selectedAnswers", (ArrayList<String>) userAnswer);
                    i.putExtra("actName", "score");
                    startActivity(i);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    finish();
                }

            }
        });


        btn_lp_confirm.setClickable(false);
        btn_lp_confirm.setAlpha(0.5f);
        Log.d("init", "btn confirm is : " +    btn_lp_confirm.isClickable());

    }

    private void reg(){



    }


    private void generateChoices(JSONArray choicesArray) throws JSONException{


        tv_qp_description.setText(quizArray.getJSONObject(index).getString("question"));
        ll_btnParent.removeAllViews();
        btn_lp_confirm.setText("Confirm");
        btn_lp_confirm.setClickable(false);
        btn_lp_confirm.setAlpha(0.5f);
        selectedAnswer = "";
        String type = quizArray.getJSONObject(index).getString("type");

        try{

            // ORIGINAL ALGORITHM FOR CHOICES CREATION
            //choicesArray = shuffleJsonArray(quizArray.getJSONObject(index).getJSONArray("choices"));
            //int choiceCount = choicesArray.length();

            ///new algo for populate choices using hashmap
            String language = "";
            if(type.equals("audio")) language = quizArray.getJSONObject(index).getString("choices_language");
            createRandomChoices(language, type);


            //show audio button if audio
            switch(type){

//                ConstraintSet constraintSet = new ConstraintSet();
//                constraintSet.clone(parent);
                case "visual":
                    iv_qp.setOnClickListener(null);
//                    iv_qp.setLayoutParams(new ConstraintLayout.LayoutParams(gf.convertToDp(150),gf.convertToDp(150)));
//                    constraintSet.connect
                    //set size of imaegview as big if visual
                    String image_name = quizArray.getJSONObject(index).getString("img_src");
                    String image_path = "lesson" + lessonId + "/"+ image_name + ".png";
                    gf.setImage(iv_qp,image_path);

                    break;

                case "audio":


                    iv_qp.setImageResource(R.drawable.vector_audio_btn);

                    //set size as small of imageview if audio type
//                    iv_qp.setLayoutParams(new ConstraintLayout.LayoutParams(gf.convertToDp(150),gf.convertToDp(150) ));

                    //name of file
                    String audio_url = quizArray.getJSONObject(index).getString("question_audio");
                    String question_path = "lesson"+lessonId+"/"+audio_url;
                    gf.playAudio(mp, question_path);
                    //register audio listener
                    iv_qp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gf.playAudio(mp, question_path);
                        }
                    });

                    break;

            }

            //generate button choices
            for(Map.Entry<String, String> entry : choices.entrySet()){


                //name of file
                String url = entry.getValue();
                String choice_path = "lesson"+lessonId+"/"+url;

                Button btn_choice = new Button(this);
                btn_choice.setBackgroundResource(R.drawable.button_untoggled);
                btn_choice.setTag("untoggled");
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/finger_paint.ttf");
                btn_choice.setTypeface(face);
                btn_choice.setTextColor(ContextCompat.getColor(this, R.color.white));

                btn_choice.setText( entry.getKey());

                btn_choice.setLayoutParams(new LinearLayout.LayoutParams(sizeWidth, sizeHeight));
                setMargins(btn_choice,0,0,0,gf.convertToDp(10));
                btn_choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Button thisBtn = (Button) v;

                        if(thisBtn.getTag().equals("untoggled")){

                            int ll_childCount = ll_btnParent.getChildCount();
                            for(int i = 0; i < ll_childCount; i++){

                                Button _btn = (Button) ll_btnParent.getChildAt(i);
                                _btn.setBackgroundResource(R.drawable.button_untoggled);
                                _btn.setTag("untoggled");
                            }

                            thisBtn.setTag("toggled");
                            thisBtn.setBackgroundResource(R.drawable.button_toggled);
                            selectedAnswer = thisBtn.getText().toString();
                            btn_lp_confirm.setClickable(true);
                            btn_lp_confirm.setAlpha(1f);
                            gf.playAudio(mp, choice_path);
                        }
                        else{
                            thisBtn.setTag("untoggled");
                            thisBtn.setBackgroundResource(R.drawable.button_untoggled);
                            selectedAnswer = "";
                            btn_lp_confirm.setClickable(false);
                            btn_lp_confirm.setAlpha(0.5f);
                        }

                    }
                });

                ll_btnParent.addView(btn_choice);

            }

        }catch(JSONException e){

            e.printStackTrace();
        }

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


    public boolean isLast(int index, JSONArray json){

        if(json.length() < 15){

            if((index+1) < json.length())
            {
                return false;

            }
            else{
                return true;
            }

        }
        else{
            if((index+1) < 15)
            {
                return false;

            }
            else{
                return true;
            }

        }

    }

    //--------------------SHUFFLE QUIZ ARRAY-----------------------

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

    //-------------CREATE CHOICES--------------
    public void createRandomChoices(String language, String type) throws JSONException{

        Log.d("lang", language);
        if(language.equals("tagalog") && !type.equals("visual")){

            //tagalog choices
            choices = new HashMap<String, String>();
            choices.put(quizArray.getJSONObject(index).getString("correct_answer"), quizArray.getJSONObject(index).getString("correct_audio"));
            while(true){

                Random generator = new Random();
                List<String> keys = new ArrayList<>(hash_tagalog.keySet());
                String randomKey = keys.get(generator.nextInt(keys.size()));
                if(randomKey.equals(quizArray.getJSONObject(index).getString("correct_answer"))){
                    continue;
                }
                else{
                    //add to the choices hashmap
                    choices.put(randomKey, hash_tagalog.get(randomKey));
                    //Log.d("choices","randomKey: "+ randomKey + ", hash_choices.get(value): " + hash_tagalog.get(randomKey)) ;
                }
                if(choices.size() == 3) break;
            }

        }
        else{
            //english choices
            choices = new HashMap<String, String>();
            choices.put(quizArray.getJSONObject(index).getString("correct_answer"), quizArray.getJSONObject(index).getString("correct_audio"));
            while(true){

                Random generator = new Random();
                List<String> keys = new ArrayList<>(hash_english.keySet());
                String randomKey = keys.get(generator.nextInt(keys.size()));
                // String value = hash_choices.get(randomKey);
                if(randomKey.equals(quizArray.getJSONObject(index).getString("correct_answer"))){
                    continue;
                }
                else{
                    //add to the choices hashmap
                    choices.put(randomKey, hash_english.get(randomKey));
                    //Log.d("choices","randomKey: "+ randomKey + ", hash_choices.get(value): " + hash_english.get(randomKey)) ;
                }
                if(choices.size() == 3) break;


            }

        }

    }

    //for setting margins
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Quiz?")
                .setMessage("Are you sure you want to quit? Your progress will not be saved.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(c, TkDashboardActivity.class);
                        i.putExtra("username", username);
                        startActivity(i);
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
