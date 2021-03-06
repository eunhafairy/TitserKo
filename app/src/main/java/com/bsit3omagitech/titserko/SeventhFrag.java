package com.bsit3omagitech.titserko;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SeventhFrag extends Fragment {


    AppCompatButton next;
    ViewPager viewpager;
    ConstraintLayout ll_dashboard;
    DataBaseHelper db;

    public SeventhFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_seventh, container, false);
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        ll_dashboard = getActivity().findViewById(R.id.ll_dashboard);
        db = new DataBaseHelper(getContext());
        next = view.findViewById(R.id.seventhFrag_tv_next);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TkDashboardActivity)getActivity()).playVoice(8);
                db.updateFirstTime(ll_dashboard.getTag().toString(), false);
                viewpager.setVisibility(View.GONE);
                ll_dashboard.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}