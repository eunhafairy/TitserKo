package com.bsit3omagitech.titserko;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FourthFragment extends Fragment {


    Button next;
    ViewPager viewpager;
    Context c;

    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        //play audio
        c = getContext();

        //initialize view pager
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        next = view.findViewById(R.id.fourthFrag_tv_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((TkDashboardActivity)getActivity()).playVoice(5);
                viewpager.setCurrentItem(4);
            }
        });
        return view;
    }
}