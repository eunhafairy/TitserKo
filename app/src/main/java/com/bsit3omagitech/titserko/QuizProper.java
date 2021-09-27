package com.bsit3omagitech.titserko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class QuizProper extends AppCompatActivity {

    int index;
    String lessonName, lessonId, lessonTranslated, username, selectedAnswer, TAG = "debug";
    ImageView iv_qp;
    TextView tv_qp_description;
    Button btn_lp_confirm;
    LinearLayout ll_btnParent;
    ImageView picture;
    JSONObject targetLessonObject;
    JSONArray quizArray;
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

        //select toggle
        toggle = false;
        //TextView
        tv_qp_description = (TextView) findViewById(R.id.tv_qp_description);

        //Button
        btn_lp_confirm = (Button) findViewById(R.id.btn_lp_confirm);
        btn_lp_confirm.setClickable(false);
        btn_lp_confirm.setAlpha(0.5f);

        //Linear Layout
        ll_btnParent = (LinearLayout) findViewById(R.id.ll_btnParent);

        //ImageView
        iv_qp = (ImageView) findViewById(R.id.iv_qp);

        //index
        index = 0;

        //Intents
        Intent intent = getIntent();
        lessonName = intent.getStringExtra("lessonName");
        lessonId = intent.getStringExtra("lessonId");
        lessonTranslated = intent.getStringExtra("lessonTranslated");
        username = intent.getStringExtra("username");

        //selected answer
        selectedAnswer = "";

        //get JSON Object of selected Quiz
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
        quizArray = targetLessonObject.getJSONArray("quiz");
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

    }

    private void reg(){

        btn_lp_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    }


                    _toggled.setBackgroundResource(R.drawable.button_correct); //change colour
                    _confirmButton.setText("Next"); //change confirm text to next


                }
                else{

                    //get the toggled button and change colour
                    Button _toggled = (Button) v;
                    int ctr = ll_btnParent.getChildCount();
                    for(int i = 0; i < ctr; i++){


                        if(ll_btnParent.getChildAt(i).getTag().equals("toggled")) {
                            _toggled = (Button) ll_btnParent.getChildAt(i);
                        }
                        else{
                            if(((Button)ll_btnParent.getChildAt(i)).getText().equals(correctAnswer)){
                                 ll_btnParent.getChildAt(i).setBackgroundResource(R.drawable.button_correct);
                                 ((Button) ll_btnParent.getChildAt(i)).setTextColor(Color.RED);
                                 ll_btnParent.getChildAt(i).setAlpha(0.8f);
                            }
                            else{

                                ll_btnParent.getChildAt(i).setAlpha(0.5f);
                            }


                        }

                    }


                    _toggled.setBackgroundResource(R.drawable.button_incorrect); //change colour
                    _confirmButton.setText("Next"); //change confirm text to next




                }


            }
        });

    }


    private void generateChoices(JSONArray choicesArray) throws JSONException{

        ll_btnParent.removeAllViews();
        btn_lp_confirm.setText("Confirm");
        btn_lp_confirm.setAlpha(0.5f);
        selectedAnswer = "";
        String type = quizArray.getJSONObject(index).getString("type");

        try{

            choicesArray = quizArray.getJSONObject(index).getJSONArray("choices");
            int choiceCount = choicesArray.length();

            //audio for instruction

            String instruction_url = quizArray.getJSONObject(index).getString("instruction_audio");    //name of file from JSON
            Uri instruction_uri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + instruction_url);  //uri of file
            if(!checkURIResource(this,instruction_uri)) instruction_uri =  Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/default_audio"); // check if null, if null set default audio
            MediaPlayer instruction_mp = MediaPlayer.create(this, instruction_uri);

            tv_qp_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    instruction_mp.start();
                }
            });



            //show audio button if audio
            switch(type){

                case "visual":
                    iv_qp.setOnClickListener(null);
                    String image_url = quizArray.getJSONObject(index).getString("img_src");
                    Uri image_uri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + image_url);
                    if(!checkURIResource(this,image_uri)) image_uri =  Uri.parse("android.resource://com.bsit3omagitech.titserko/drawable/default_img"); // check if null, if null set default img
                    iv_qp.setImageURI(image_uri);
                    break;

                case "audio":

                    String audio_url = quizArray.getJSONObject(index).getString("question_audio");
                    Uri audio_uri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + audio_url);  //uri of file
                    if(!checkURIResource(this,audio_uri)) audio_uri =  Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/default_audio"); // check if null, if null set default audio
                    MediaPlayer question_mp = MediaPlayer.create(this, audio_uri);
                    Log.d(TAG, "audio uri: " + audio_uri);
                    //set desfault image for audio
                    iv_qp.setImageResource(R.drawable.img_audio);

                    //register audio listener
                    iv_qp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            question_mp.start();
                        }
                    });

                    break;

            }

            //generate button choices
            for(int i = 0; i < choiceCount; i++){

                //name of file
                String url = choicesArray.getJSONObject(i).getString("audio_src"); ;

                Uri uri = Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + url);
                if(!checkURIResource(this,uri)) uri =  Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/default_audio"); // check if null, if null set default audio
                MediaPlayer mp = MediaPlayer.create(this, uri);

                Button btn_choice = new Button(this);
                btn_choice.setBackgroundResource(R.drawable.button_untoggled);
                btn_choice.setTag("untoggled");
                btn_choice.setText(choicesArray.getJSONObject(i).getString("label"));
                btn_choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
/*
                        int ll_childCount = ll_btnParent.getChildCount();
                        for(int i = 0; i < ll_childCount; i++){

                            Button _btn = (Button) ll_btnParent.getChildAt(i);
                            _btn.setBackgroundResource(R.drawable.button_untoggled);
                        }
*/
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
                            mp.start();
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

    public static boolean checkURIResource(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        boolean doesExist= (cursor != null && cursor.moveToFirst());
        if (cursor != null) {
            cursor.close();
        }
        return doesExist;
    }
}
