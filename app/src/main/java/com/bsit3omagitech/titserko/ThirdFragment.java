package com.bsit3omagitech.titserko;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ThirdFragment extends Fragment {

    Button done;
    ViewPager viewpager;

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        done = view.findViewById(R.id.thirdFrag_btn_next);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("frag", "went here");
                viewpager.setCurrentItem(3);
            }
        });

        return view;
    }
}