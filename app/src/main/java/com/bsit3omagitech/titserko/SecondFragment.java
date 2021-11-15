package com.bsit3omagitech.titserko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SecondFragment extends Fragment {

    TextView next;
    ViewPager viewpager;
    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        //initialize view pager
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        next = view.findViewById(R.id.secondFrag_tv_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(2);
            }
        });


        return view;
    }
}