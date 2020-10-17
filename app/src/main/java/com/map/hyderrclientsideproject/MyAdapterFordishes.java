package com.map.hyderrclientsideproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapterFordishes extends RecyclerView.Adapter<MyAdapterFordishes.MyViewHolder> {
    ArrayList<DishModel> data;
    Context context;
    Activity activity;
    String TAG;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView title,price;
        ImageView imageView;
        Button delatis,cart;
        ProgressBar progressBar;
        public MyViewHolder(View view) {
            super(view);
//                sideImage=view.findViewById(R.id.side_image);
            title=view.findViewById(R.id.title);
            price=view.findViewById(R.id.price);
            delatis=view.findViewById(R.id.delatis);
            cart=view.findViewById(R.id.add_to_cart);
            imageView=view.findViewById(R.id.image);
            progressBar=view.findViewById(R.id.progress_bar);


        }
    }
    public MyAdapterFordishes(Context c, Activity a , ArrayList<DishModel> moviesList){
        this.data =moviesList;
        context=c;
        activity=a;
        TAG="***Adapter";
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        if (flag==1){// beign called from my profile so we have to set visible following image
//            holder.menuImage.setVisibility(View.VISIBLE);
//        }

        holder.title.setText(data.get(position).getName());
        holder.discriptioin.setText(data.get(position).getAddress());
        Glide.with(activity).load(data.get(position).getImageUrl())
                .centerCrop()
                .into(holder.imageView);

        holder.menus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity,DetailActivity.class).putExtra("data",data.get(position)));
            }
        });
    }
    @Override
    public int getItemCount() {
//        return  5;
        return data.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}
