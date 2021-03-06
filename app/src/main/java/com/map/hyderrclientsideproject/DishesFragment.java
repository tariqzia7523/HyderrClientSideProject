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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DishesFragment extends Fragment {

    TextView textView;
    RecyclerView recyclerView;
    MyAdapterFordishes myAdapter;
    ArrayList<DishModel> list;
    DatabaseReference myRefDish,myRefCart;
    UserModel userModel;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        textView=v.findViewById(R.id.header_text);
        recyclerView=v.findViewById(R.id.food_list);
        textView.setText("Please Select your food");
        list=new ArrayList<>();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        userModel =(UserModel) getArguments().getSerializable("data");
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter=new MyAdapterFordishes(getContext(), getActivity(),list,progressDialog);
        recyclerView.setAdapter(myAdapter);
        myRefDish= FirebaseDatabase.getInstance().getReference("Menus");

        progressDialog.show();
        myRefDish.child(userModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    progressDialog.dismiss();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DishModel dishModel=dataSnapshot.getValue(DishModel.class);
                        dishModel.setId(dataSnapshot.getKey());
                        dishModel.setResturentID(userModel.getId());
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
