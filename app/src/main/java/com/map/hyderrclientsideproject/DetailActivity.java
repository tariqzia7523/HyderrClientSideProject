package com.map.hyderrclientsideproject;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    UserModel userModel;
    DatabaseReference myRefDish,myRefCart;
    ImageView mainImage;
    TextView resturentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        mainImage=findViewById(R.id.main_image);
        resturentName=findViewById(R.id.user_name);

        myRefDish= FirebaseDatabase.getInstance().getReference("Menus");
        try{
            userModel=(UserModel)getIntent().getExtras().get("data");
            Glide.with(DetailActivity.this).load(userModel.getImageUrl())
                    .centerCrop()
                    .into(mainImage);
            toolbar.setTitle(userModel.getName());
            toolbar.setCollapseIcon(R.drawable.ic_baseline_arrow_back_24);
            toolBarLayout.setTitle(userModel.getName());
            toolBarLayout.setTitleEnabled(true);
            resturentName.setText(userModel.getName());
            myRefDish.child(userModel.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{

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

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}