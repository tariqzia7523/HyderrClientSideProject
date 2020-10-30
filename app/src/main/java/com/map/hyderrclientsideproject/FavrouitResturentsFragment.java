package com.map.hyderrclientsideproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FavrouitResturentsFragment extends Fragment {


    public static FavrouitResturentsFragment instance;
    DatabaseReference myRef,myRefFavrout;
    ProgressDialog progressDialog;
    ArrayList<UserModel> resturents;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    SearchView searchView;
    ArrayList<String> favRests;
    FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        instance=this;
        myRefFavrout = FirebaseDatabase.getInstance().getReference("FavoriteRestaurant");
        myRef = FirebaseDatabase.getInstance().getReference("Users").child("Restaurants");
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=v.findViewById(R.id.food_list);
        resturents=new ArrayList<>();
        favRests=new ArrayList<>();
        searchView=v.findViewById(R.id.search_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter=new MyAdapter(getContext(), getActivity(),resturents);
        recyclerView.setAdapter(myAdapter);
        progressDialog.show();
        myRefFavrout.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        favRests.add(snapshot1.getKey());
                    }
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressDialog.dismiss();
                            try{
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    UserModel dishModel=dataSnapshot1.getValue(UserModel.class);
                                    dishModel.setId(dataSnapshot1.getKey());
                                    if(favRests.contains(dataSnapshot1.getKey()))
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
                }catch (Exception c){
                    c.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
