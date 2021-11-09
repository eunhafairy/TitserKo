package com.bsit3omagitech.titserko;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class GlobalFunctions {
    public Context context;


    public GlobalFunctions(Context context) {
        this.context = context;
    }

    public void playAudio(MediaPlayer m, String fileName) {
        try {
            if (m.isPlaying()) {
                m.stop();
                m.reset();
                m = new MediaPlayer();
            }

            m.reset();

            if(checkFileExists(fileName+".mp3")){

                AssetFileDescriptor descriptor = context.getAssets().openFd(fileName+".mp3");
                m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();


            }
            else{

                AssetFileDescriptor descriptor = context.getAssets().openFd("general_audio/default_audio.mp3");
                m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();

            }
            m.prepare();
            m.setVolume(1f, 1f);
            m.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImage(ImageView iv, String fileName){
        // load image
        try {
            // get input stream
            InputStream ims = context.getAssets().open(fileName);
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            iv.setImageDrawable(d);
        }
        catch(IOException ex) {
            iv.setImageResource(R.drawable.default_img);
            return;
        }

    }


    public boolean checkFileExists(String path) throws IOException {

        AssetManager mg = context.getResources().getAssets();
        InputStream is = null;
        try {
            is = mg.open(path);
            //File exists so do something with it
            return true;
        } catch (IOException ex) {
            //file does not exist
            return false;
        } finally {
            if (is != null) {
                is.close();
            }
        }


    }


    public String getLessonImgPath(String lessonId){

        String path = "";
        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String id = jo_inside.getString("lesson_id");
                if(id.equals(lessonId) ){

                    path = jo_inside.getString("lesson_img");
                    break;

                }
            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }
        Log.d("paaa", path);
        return path;



    }



    //get the JSON file of lessons
    public String loadJSONFromAsset(String name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(name);
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
