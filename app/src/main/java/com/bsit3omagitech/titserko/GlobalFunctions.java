package com.bsit3omagitech.titserko;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

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


}
