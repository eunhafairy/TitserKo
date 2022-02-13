package com.bsit3omagitech.titserko;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FirstFragment extends Fragment {


    TextView next;
    Context c;
    ViewPager viewpager;
    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //play audio
        c = getContext();
        ((TkDashboardActivity)getActivity()).playVoice(1);

        //inflate layout
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        //initialize view pager
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        next = view.findViewById(R.id.firstFrag_tv_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TkDashboardActivity)getActivity()).playVoice(2);

                viewpager.setCurrentItem(1);
            }
        });


        return view;
    }
}