package com.map.hyderrclientsideproject;

import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    UserModel userModel;

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



        }catch (Exception c){
            c.printStackTrace();
        }

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setupDishFragment();
    }

    public void setupDishFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DishesFragment newCustomFragment = new DishesFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",userModel);
        newCustomFragment.setArguments(bundle);
        transaction.replace(R.id.container, newCustomFragment );
        transaction.addToBackStack(null);
        transaction.commit();

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(DetailActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }
}