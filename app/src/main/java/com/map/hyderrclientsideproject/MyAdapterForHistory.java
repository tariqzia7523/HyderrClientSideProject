package com.map.hyderrclientsideproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyAdapterForHistory extends RecyclerView.Adapter<MyAdapterForHistory.MyViewHolder> {
    ArrayList<HistoryModel> data;
    Context context;
    Activity activity;
    String TAG;
    DataSnapshot userSnapShot;
    FirebaseUser firebaseUser;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView diliverymanname,resturentName,date;
        public MyViewHolder(View view) {
            super(view);
//                sideImage=view.findViewById(R.id.side_image);
            diliverymanname=view.findViewById(R.id.dilivery_man_name);
            resturentName=view.findViewById(R.id.name);
            date=view.findViewById(R.id.date);


        }
    }
    public MyAdapterForHistory(Context c, Activity a , ArrayList<HistoryModel> moviesList,DataSnapshot snapshot){
        this.data =moviesList;
        context=c;
        activity=a;
        TAG="***Adapter";
        userSnapShot=snapshot;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        if (flag==1){// beign called from my profile so we have to set visible following image
//            holder.menuImage.setVisibility(View.VISIBLE);
//        }

        holder.resturentName.
                setText(userSnapShot.child("Restaurants").child(data.get(position).getResturentid())
                        .child("name").getValue(String.class));
        holder.diliverymanname.
                setText(userSnapShot.child("DeliveryMen").child(data.get(position).getDiliverymanId())
                        .child("name").getValue(String.class));


        holder.date.setText(getDate(Long.parseLong(data.get(position).getDate())));



//            diliverymanname,viewLocation,profileImage


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


    private String getDate(long time) {

        String date = DateFormat.format("dd-MM-yyyy", time).toString();
        return date;
    }
}
