package com.map.hyderrclientsideproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapterFordishes extends RecyclerView.Adapter<MyAdapterFordishes.MyViewHolder> {
    ArrayList<DishModel> data;
    Context context;
    Activity activity;
    String TAG;
    DatabaseReference myRefCart;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView title,price,discription;
        ImageView imageView;
        LinearLayout cart;
        ProgressBar progressBar;
        public MyViewHolder(View view) {
            super(view);
//                sideImage=view.findViewById(R.id.side_image);
            title=view.findViewById(R.id.title);
            discription=view.findViewById(R.id.discription);
            price=view.findViewById(R.id.price);
            cart=view.findViewById(R.id.add_to_cart);
            imageView=view.findViewById(R.id.image);
            progressBar=view.findViewById(R.id.progress_bar);


        }
    }
    public MyAdapterFordishes(Context c, Activity a , ArrayList<DishModel> moviesList,ProgressDialog progressDialog){
        this.data =moviesList;
        context=c;
        activity=a;
        TAG="***Adapter";
        myRefCart= FirebaseDatabase.getInstance().getReference("Carts");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog=progressDialog;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        if (flag==1){// beign called from my profile so we have to set visible following image
//            holder.menuImage.setVisibility(View.VISIBLE);
//        }

        holder.title.setText(data.get(position).getName());
        holder.discription.setText(data.get(position).getDescription());
        holder.price.setText(data.get(position).getPrice()+"USD");
        Glide.with(activity).load(data.get(position).getImageUrl())
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.imageView);
        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                data.get(position).setQuantity(1);
                myRefCart.child(firebaseUser.getUid()).child(data.get(position).getId()).setValue(data.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(activity,"added to cart",Toast.LENGTH_LONG).show();
                    }
                });
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
