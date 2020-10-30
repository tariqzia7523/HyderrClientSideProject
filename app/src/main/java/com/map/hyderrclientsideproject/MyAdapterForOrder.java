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
import android.widget.LinearLayout;
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

public class MyAdapterForOrder extends RecyclerView.Adapter<MyAdapterForOrder.MyViewHolder> {
    ArrayList<OrderModel> data;
    Context context;
    Activity activity;
    String TAG;
    DatabaseReference myRef,myRefOrder,myRefHistory,myRefFav;
    FirebaseUser firebaseUser;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView name,dishes,status,diliverymanname,viewLocation,dilivery_man_text;
        LinearLayout markComplete;
        RelativeLayout bottomLayout;
        ImageView profileImage;
        public MyViewHolder(View view) {
            super(view);
//                sideImage=view.findViewById(R.id.side_image);
            name=view.findViewById(R.id.resturent_name);
            dishes=view.findViewById(R.id.dishes);
            status=view.findViewById(R.id.status);
            bottomLayout=view.findViewById(R.id.bottom_layout);
            diliverymanname=view.findViewById(R.id.name);
            viewLocation=view.findViewById(R.id.select);
            profileImage=view.findViewById(R.id.profile_image);
            dilivery_man_text=view.findViewById(R.id.dilivery_man_text);
            markComplete=view.findViewById(R.id.mark_complate);

        }
    }
    public MyAdapterForOrder(Context c, Activity a , ArrayList<OrderModel> moviesList){
        this.data =moviesList;
        context=c;
        activity=a;
        TAG="***Adapter";
        myRefFav= FirebaseDatabase.getInstance().getReference("FavoriteRestaurant");
        myRefHistory= FirebaseDatabase.getInstance().getReference("History");
        myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRefOrder= FirebaseDatabase.getInstance().getReference("Orders");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_model_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        if (flag==1){// beign called from my profile so we have to set visible following image
//            holder.menuImage.setVisibility(View.VISIBLE);
//        }
        myRef.child("Restaurants").child(data.get(position).getDishes().get(0).getResturentID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.dishes.setText(data.get(position).getDishes().size()+"");
        holder.status.setText(data.get(position).getStatus());


        if(data.get(position).isSelected()){
            holder.bottomLayout.setVisibility(View.VISIBLE);
            holder.viewLocation.setText("View Location");
            holder.dilivery_man_text.setVisibility(View.VISIBLE);
            holder.viewLocation.setBackgroundResource(R.drawable.button_bg_disable);
            myRef.child("DeliveryMen").child(data.get(position).getDiliverymanid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        UserModel resturentUserModel=snapshot.getValue(UserModel.class);
                        resturentUserModel.setId(snapshot.getKey());
                        Glide.with(activity).load(resturentUserModel.getImageUrl())
                                .into(holder.profileImage);
                        holder.diliverymanname.setText(resturentUserModel.getName());
                        holder.viewLocation.setBackgroundResource(R.drawable.button_selector);
                        holder.viewLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.startActivity(new Intent(activity,ViewLiveLocationActivity.class)
                                        .putExtra("data",data.get(position))
                                        .putExtra("dilivery_man",resturentUserModel));

                            }
                        });

                        holder.markComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                HistoryModel historyModel=new HistoryModel();
                                historyModel.setDate(System.currentTimeMillis()+"");
                                historyModel.setDiliverymanId(resturentUserModel.getId());
                                historyModel.setResturentid(data.get(position).getDishes().get(0).getResturentID());
                                historyModel.setUserID(firebaseUser.getUid());
                                historyModel.setPrice(data.get(position).getAmount()+" USD");
                                myRefHistory.push().setValue(historyModel);
                                myRefOrder.child(firebaseUser.getUid()).child(data.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context,"Order completed! you can view in history section",Toast.LENGTH_LONG).show();
                                        new AlertDialog.Builder(context)
                                                .setTitle("Order Marked completed")
                                                .setMessage("Do you want restaurant in your favorite list?")

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Continue with delete operation
                                                        myRefFav.child(firebaseUser.getUid()).child(data.get(position).getDishes().get(0).getResturentID()).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                data.remove(position);
                                                                notifyDataSetChanged();
                                                                Toast.makeText(context,"Added to your favorite list!",Toast.LENGTH_LONG).show();
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
                        });



                    }catch (Exception c){
                        c.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            diliverymanname,viewLocation,profileImage

        }
        holder.markComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HistoryModel historyModel=new HistoryModel();
                historyModel.setDate(System.currentTimeMillis()+"");
                historyModel.setDiliverymanId("No delivery man");
                historyModel.setResturentid(data.get(position).getDishes().get(0).getResturentID());
                historyModel.setUserID(firebaseUser.getUid());
                historyModel.setPrice(data.get(position).getAmount()+" USD");
                myRefHistory.push().setValue(historyModel);
                myRefOrder.child(firebaseUser.getUid()).child(data.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context,"Order completed! you can view in history section",Toast.LENGTH_LONG).show();


                        new AlertDialog.Builder(context)
                                .setTitle("Order Marked completed")
                                .setMessage("Do you want restaurant in your favorite list?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        myRefFav.child(firebaseUser.getUid()).child(data.get(position).getDishes().get(0).getResturentID()).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                data.remove(position);
                                                Toast.makeText(context,"Added to your favorite list!",Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                        notifyDataSetChanged();
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
