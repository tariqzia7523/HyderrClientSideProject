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

public class HistoryFragment extends Fragment {



    public static HistoryFragment instance;
    DatabaseReference myRefHistory,myRefUser;
    RecyclerView recyclerView;
    MyAdapterForHistory myAdapterForHistory;
    ArrayList<HistoryModel> historyModels;

    FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.history_fragment, container, false);
        instance=this;
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("please message");
        progressDialog.show();
        recyclerView=v.findViewById(R.id.history_item_list);
        historyModels=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myRefHistory= FirebaseDatabase.getInstance().getReference("History");
        myRefUser= FirebaseDatabase.getInstance().getReference("Users");

        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    myAdapterForHistory=new MyAdapterForHistory(getContext(),getActivity(),historyModels,snapshot);
                    recyclerView.setAdapter(myAdapterForHistory);
                    myRefHistory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                progressDialog.dismiss();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    HistoryModel historyModel=dataSnapshot.getValue(HistoryModel.class);
                                    historyModel.setId(dataSnapshot.getKey());
                                    if(historyModel.getUserID().equalsIgnoreCase(firebaseUser.getUid())){
                                        historyModels.add(historyModel);
                                        myAdapterForHistory.notifyDataSetChanged();
                                    }
                                }
                            }catch (Exception c){
                                c.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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


        return v;
    }


}
