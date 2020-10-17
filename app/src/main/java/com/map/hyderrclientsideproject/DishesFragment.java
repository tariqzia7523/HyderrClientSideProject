package com.map.hyderrclientsideproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DishesFragment extends Fragment {

    TextView textView;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        textView=v.findViewById(R.id.header_text);
        recyclerView=v.findViewById(R.id.food_list);
        textView.setText("Please Select your food");

        return v;
    }



}
