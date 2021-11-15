package com.bsit3omagitech.titserko;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class IntroAdapter extends FragmentPagerAdapter {


    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new FirstFragment();
            case 1:
                return new SecondFragment();
            case 2:
                return new ThirdFragment();
            case 3:
                return new FourthFragment();
            case 4:
                return new FifthFragment();
            case 5:
                return new SixthFragment();
            case 6:
                return new SeventhFrag();
            default:
                return null;
        }

    }


    @Override
    public int getCount() {
        return 7;
    }
}
