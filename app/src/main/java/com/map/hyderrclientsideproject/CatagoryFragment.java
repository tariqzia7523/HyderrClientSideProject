package com.map.hyderrclientsideproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class CatagoryFragment extends Fragment {




    String catagoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.catagory_fragment, container, false);
        catagoryName = getArguments().getString("catagory_name");
        return v;
    }



}