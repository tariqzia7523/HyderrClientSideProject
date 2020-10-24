package com.map.hyderrclientsideproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderFragment extends Fragment {

    TextView totalText;
    RecyclerView recyclerView;
    MyAdapterForOrder myAdapter;
    ArrayList<OrderModel> list;
    DatabaseReference myRefOrders;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    public static OrderFragment instance;
//    UserModel userModel;
    String TAG;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.order_fragment, container, false);
        recyclerView=v.findViewById(R.id.order_list);
        instance=this;
        list=new ArrayList<>();
        TAG="***OrderFrag";

        totalText=v.findViewById(R.id.total_amount);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();





        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter=new MyAdapterForOrder(getContext(), getActivity(),list);
        recyclerView.setAdapter(myAdapter);

        myRefOrders= FirebaseDatabase.getInstance().getReference("Orders");
        myRefOrders.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             try{
                 for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                     OrderModel dishModel=dataSnapshot.getValue(OrderModel.class);
                     dishModel.setId(dataSnapshot.getKey());

                     if(dataSnapshot.child("SelectedDeliveryMan").exists()){
                         dishModel.setSelected(true);
                         dishModel.setDiliverymanid(dataSnapshot.child("SelectedDeliveryMan").getValue(String.class));
                     }
                     list.add(dishModel);
                     myAdapter.notifyDataSetChanged();

                 }


             }catch (Exception c){
                 c.printStackTrace();
             }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }

}
