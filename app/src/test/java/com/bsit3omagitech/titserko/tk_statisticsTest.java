package com.bsit3omagitech.titserko;

import android.util.Log;

import junit.framework.TestCase;

public class tk_statisticsTest extends TestCase {

    public void testLoadJSONFromAsset(){
        tk_statistics ts = new tk_statistics();
        String a = ts.loadJSONFromAsset();
        if(a.equals(""))
        {
            Log.d("testing", "empty");

        }
        else{
            Log.d("testing", "not empty");
        }
        
    }
}