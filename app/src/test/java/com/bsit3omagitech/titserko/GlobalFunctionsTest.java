package com.bsit3omagitech.titserko;

import android.provider.Settings;

import junit.framework.TestCase;

public class GlobalFunctionsTest extends TestCase {


    public void testAge(){
        String bday = "03/06/1999";
        GlobalFunctions gf = new GlobalFunctions(null);
        int output = gf.getAge(bday);
        int expected = 22;
        double delta = .1;
        assertEquals(expected, output, delta);

    }




}



