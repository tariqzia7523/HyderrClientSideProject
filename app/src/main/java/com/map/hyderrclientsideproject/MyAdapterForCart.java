package com.map.hyderrclientsideproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapterForCart extends RecyclerView.Adapter<MyAdapterForCart.MyViewHolder> {
    ArrayList<DishModel> data;
    Context context;
    Activity activity;
    String TAG;
    DatabaseReference myRefCart;
    FirebaseUser firebaseUser;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView title,discriptioin,price,quantity;
        ImageView imageView,add,min;
        Button remove;
        public MyViewHolder(View view) {
            super(view);
//                sideImage=view.findViewById(R.id.side_image);
            title=view.findViewById(R.id.title);
            discriptioin=view.findViewById(R.id.discription);
            price =view.findViewById(R.id.price);
            quantity=view.findViewById(R.id.quantity);
            add =view.findViewById(R.id.add);
            min =view.findViewById(R.id.minus);
            imageView=view.findViewById(R.id.image);
            remove=view.findViewById(R.id.remove);




        }
    }
    public MyAdapterForCart(Context c, Activity a , ArrayList<DishModel> moviesList){
        this.data =moviesList;
        context=c;
        activity=a;
        TAG="***Adapter";
        myRefCart= FirebaseDatabase.getInstance().getReference("Carts");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        if (flag==1){// beign called from my profile so we have to set visible following image
//            holder.menuImage.setVisibility(View.VISIBLE);
//        }

        holder.title.setText(data.get(position).getName());
        holder.discriptioin.setText(data.get(position).getDescription());
        holder.price.setText(data.get(position).getPrice()+" USD");
        holder.quantity.setText(data.get(position).getQuantity()+"");

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).setQuantity( data.get(position).getQuantity()+1);
                myRefCart.child(firebaseUser.getUid()).child(data.get(position).getId()).child("quantity").setValue(data.get(position).getQuantity());
                CartFragment.instance.totalPrice();
                notifyDataSetChanged();
            }
        });
        holder.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).getQuantity()>1){
                    data.get(position).setQuantity( data.get(position).getQuantity()-1);
                    myRefCart.child(firebaseUser.getUid()).child(data.get(position).getId()).child("quantity").setValue(data.get(position).getQuantity());
                    notifyDataSetChanged();
                    CartFragment.instance.totalPrice();

                }else{
                    Toast.makeText(activity,"Can not perform this action",Toast.LENGTH_LONG).show();
                }
            }
        });
        Glide.with(activity).load(data.get(position).getImageUrl())
                .centerCrop()
                .into(holder.imageView);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myRefCart.child(firebaseUser.getUid()).child(data.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        data.remove(position);
                                        notifyDataSetChanged();
                                        CartFragment.instance.totalPrice();
                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
