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


public class FifthFragment extends Fragment {

    Button next;
    ViewPager viewpager;
    Context c;


    public FifthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fifth, container, false);
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        next = view.findViewById(R.id.fifthFrag_tv_next);
        c = getContext();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play audio
                ((TkDashboardActivity)getActivity()).playVoice(6);
                viewpager.setCurrentItem(5);
            }
        });
        return view;
    }
}