package com.map.hyderrclientsideproject;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ViewLiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference myRefLocation;
    OrderModel orderModel;
    UserModel resturentUserModel;
    ImageView profile_image;
    TextView textView,remaingTime;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_live_location);
        getSupportActionBar().hide();
        profile_image=findViewById(R.id.profile_image);
        textView=findViewById(R.id.name);
        remaingTime=findViewById(R.id.remaing_time);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try{
            orderModel=(OrderModel)getIntent().getExtras().get("data");
            resturentUserModel=(UserModel) getIntent().getExtras().get("dilivery_man");

            Glide.with(ViewLiveLocationActivity.this).load(resturentUserModel.getImageUrl())
                    .into(profile_image);
            textView.setText(resturentUserModel.getName());

        }catch (Exception c){
            c.printStackTrace();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        myRefLocation = FirebaseDatabase.getInstance().getReference("Users").child("DeliveryMen").child(resturentUserModel.getId()).child("currentLocation");
        myRefLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    LocationModel locationModel=snapshot.getValue(LocationModel.class);
                    mMap.clear();
                    LatLng sydney = new LatLng(locationModel.getLat(), locationModel.getLng());
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
                    try{
                        Location location=new Location("Other");
                        location.setLatitude(locationModel.getLat());
                        location.setLongitude(locationModel.getLng());
                        double meter = MainActivity.curlocation.distanceTo(location);
                        double km= (meter/1000);
                        double time =km*40;
                        DecimalFormat precision = new DecimalFormat("0.00");
// dblVariable is a number variable and not a String in this case
                        remaingTime.setText(precision.format(time)+" min");
                        Log.e("time ","disctance is "+km);

                    }catch (Exception c){
                        c.printStackTrace();
                        remaingTime.setVisibility(View.GONE);
                    }

                }catch (Exception c){
                    c.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}