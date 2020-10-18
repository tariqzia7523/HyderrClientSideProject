package com.map.hyderrclientsideproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    TextView totalText;
    RecyclerView recyclerView;
    MyAdapterForCart myAdapter;
    ArrayList<DishModel> list;
    DatabaseReference myRefCart,myRefOrders;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    public static CartFragment instance;

    String TAG;
    int sum=0;
    UserModel userModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.cart_fragment, container, false);
        recyclerView=v.findViewById(R.id.cart_list);
        instance=this;
        list=new ArrayList<>();
        TAG="***CartFrag";

        userModel =(UserModel) getArguments().getSerializable("data");
        totalText=v.findViewById(R.id.total_amount);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();





        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter=new MyAdapterForCart(getContext(), getActivity(),list);
        recyclerView.setAdapter(myAdapter);

        myRefOrders= FirebaseDatabase.getInstance().getReference("Orders");
        myRefCart= FirebaseDatabase.getInstance().getReference("Carts");
        myRefCart.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             try{
                 for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                     DishModel dishModel=dataSnapshot.getValue(DishModel.class);
                     dishModel.setId(dataSnapshot.getKey());
                     list.add(dishModel);
                     myAdapter.notifyDataSetChanged();

                 }
                 totalPrice();

             }catch (Exception c){
                 c.printStackTrace();
             }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        v.findViewById(R.id.check_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activity.paymentiflatecall((double)sum);
            }
        });

        return v;
    }


    public void updateTable(String transectionid,String paymentId,String phoneNumber){
        progressDialog.show();
        OrderModel orderModel =new OrderModel();
        orderModel.setAmount(sum);
        orderModel.setDishes(list);
        orderModel.setPaymentId(paymentId);
        orderModel.setStatus("Ordered");
        orderModel.setTransactionId(transectionid);
        orderModel.setUserAddress(userModel.getAddress());
        orderModel.setUserLat(userModel.getLat());
        orderModel.setUserLng(userModel.getLng());
        orderModel.setUserName(userModel.getName());
        orderModel.setPhoneNumber(phoneNumber);

        myRefOrders.child(userModel.getId()).push().setValue(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Order Placed",Toast.LENGTH_LONG).show();

                myRefCart.child(firebaseUser.getUid()).removeValue();
            }
        });


    }


    public void totalPrice(){
        for(int i = 0 ; i < list.size(); i++){
            sum+=list.get(i).getQuantity()*Integer.parseInt(list.get(i).getPrice().replaceAll("USD",""));
        }
        totalText.setText("Total Price : "+sum+" USD");
    }

}
