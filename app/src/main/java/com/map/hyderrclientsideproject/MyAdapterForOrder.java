package com.map.hyderrclientsideproject;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterForOrder extends RecyclerView.Adapter {
    public MyAdapterForOrder(Context context, FragmentActivity activity, ArrayList<OrderModel> list) {

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
    
    @Override
    public int getItemCount() {
        return 0;
    }
}
