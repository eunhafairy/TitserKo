package com.bsit3omagitech.titserko;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SixthFragment extends Fragment {


    AppCompatButton next;
    ViewPager viewpager;
    public SixthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sixth, container, false);
        viewpager = getActivity().findViewById(R.id.main_viewPager);
        next = view.findViewById(R.id.sixthFrag_tv_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(6);
            }
        });
        return view;
    }
}