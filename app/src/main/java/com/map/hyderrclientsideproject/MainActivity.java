package com.map.hyderrclientsideproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference myRef,myRefMainImage;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    DrawerLayout drawer;
    ImageView mainImage;
    ImageView profileImage;
    DatabaseReference myRefUser;
    FirebaseUser currentUser;
    ProgressDialog progressDialog;
    ProgressBar progressBarImage;
    TextView userName;
    NavigationView navigationView;
    CollapsingToolbarLayout toolBarLayout;
    String profileImageString,mainImageString,ordersImageString,cartImageString;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("Menus");
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child("Clients");
        myRefMainImage = FirebaseDatabase.getInstance().getReference("HomeScreen");
        mainImage=findViewById(R.id.main_image);
        drawer = findViewById(R.id.drawer_layout);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        progressDialog.show();

        myRefMainImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{

                    mainImageString=snapshot.child("imageUrl").getValue(String.class);
                    profileImageString=snapshot.child("profilingImage").getValue(String.class);
                    cartImageString=snapshot.child("myCartImage").getValue(String.class);
                    ordersImageString=snapshot.child("foodOrders").getValue(String.class);
                    Glide.with(MainActivity.this).load(mainImageString)
                            .centerCrop()
                            .into(mainImage);
                    toolbar.setTitle("Restaurants");
                    toolBarLayout.setTitleEnabled(true);
                }catch (Exception c){
                    c.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        drawerInit();
        myRefUser.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                try{
                    userModel=snapshot.getValue(UserModel.class);
                    userModel.setId(snapshot.getKey());
                    userName.setText(userModel.getName());
                    Glide.with(MainActivity.this).load(userModel.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBarImage.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBarImage.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(profileImage);

                }catch (Exception c){
                    c.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
        setupHomeFragment();
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    public void drawerInit(){
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        profileImage=header.findViewById(R.id.profile_image);
        progressBarImage=header.findViewById(R.id.progress_bar_image);
        userName=header.findViewById(R.id.name);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void setupHomeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment newCustomFragment = new HomeFragment();
        transaction.replace(R.id.container, newCustomFragment );
        transaction.addToBackStack(null);
        transaction.commit();

    }
    public void setupProfileFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",userModel);
        bundle.putString("email",firebaseUser.getEmail());
        ProfileFragment newCustomFragment = new ProfileFragment();

        newCustomFragment.setArguments(bundle);
        transaction.replace(R.id.container, newCustomFragment );
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
           ProfileFragment.instance.updateImage(data.getData());
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishAffinity();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(Gravity.LEFT);

        navigationView.setCheckedItem(item.getItemId());
        if(item.getItemId()==R.id.nav_home){
            Glide.with(MainActivity.this).load(mainImageString)
                    .centerCrop()
                    .into(mainImage);
            setupHomeFragment();
        }else if(item.getItemId()==R.id.nav_cart){
            Glide.with(MainActivity.this).load(cartImageString)
                    .centerCrop()
                    .into(mainImage);
        }else if(item.getItemId()==R.id.nav_my_orders){
            Glide.with(MainActivity.this).load(ordersImageString)
                    .centerCrop()
                    .into(mainImage);
        }else if(item.getItemId()==R.id.nav_my_profile){
            setupProfileFragment();
            Glide.with(MainActivity.this).load(profileImageString)
                    .centerCrop()
                    .into(mainImage);
        }

        return false;
    }
}