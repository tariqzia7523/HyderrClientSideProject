package com.map.hyderrclientsideproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    DatabaseReference myRef;
    ProgressDialog progressDialog;
    ArrayList<UserModel> resturents;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        myRef = FirebaseDatabase.getInstance().getReference("Users").child("Restaurants");
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        recyclerView=v.findViewById(R.id.food_list);
        resturents=new ArrayList<>();
        searchView=v.findViewById(R.id.search_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter=new MyAdapter(getContext(), getActivity(),resturents);
        recyclerView.setAdapter(myAdapter);
        progressDialog.show();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                try{
                    for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                        UserModel dishModel=dataSnapshot1.getValue(UserModel.class);
                        dishModel.setId(dataSnapshot1.getKey());
                        resturents.add(dishModel);
                        myAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }catch (Exception c){
                    c.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        searchView.setIconifiedByDefault(false);
//            searchView.setSubmitButtonEnabled(true);
        
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                    myAdapter.getFilter().filter(newText);
                newText=newText.toLowerCase();
//                    System.out.println(results);
                ArrayList<UserModel> newList=new ArrayList<>();
                for (UserModel userInfo : resturents){
                    String username=userInfo.getName().toLowerCase();
//                                String status=userInfo.getJobStatus().toLowerCase();
//                        String type=userInfo.getType().toLowerCase();
                    if (username.contains(newText)){
                        newList.add(userInfo);
                    }
                }
                myAdapter.setFilter(newList);
                return true;
            }
        });
        searchView.setQueryHint("Search Here");

        return v;
    }



}
