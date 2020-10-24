package com.map.hyderrclientsideproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String profileImageString,mainImageString,ordersImageString,cartImageString,historyImageString;
    UserModel userModel;
    private Stripe stripe;
    CardMultilineWidget cardInputWidget;
    private String secretKey ;
    private String publishableKey;
    public static MainActivity activity;
    String TAG="***MainAct";
    String transectionId,paymentId;
    String UserPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        activity=this;
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
        UserPhoneNumber="";

        secretKey="sk_test_51HV8ueHfXAtpG4cdWIO3a4QjRNBTp6OqDtkahvO8suR6ENwuRSQK3UOhqXNaYipqPYtVh3kYpus2VckWpcpjs4qe00R8oR6V2I";
        publishableKey="pk_test_51HV8ueHfXAtpG4cdzBWmS0YYqYfqh2gPeFAOFKthpvnCgRX4WzhZTQ1x14nQNtyhOSnPt72sx2UUqT1oeSKelaJz00xUlvMG1V";

        PaymentConfiguration.init(getApplicationContext(), publishableKey);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



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
                    historyImageString=snapshot.child("historyImage").getValue(String.class);
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

    public void setupCartFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",userModel);
        CartFragment newCustomFragment = new CartFragment();
        newCustomFragment.setArguments(bundle);
        transaction.replace(R.id.container, newCustomFragment );
        transaction.addToBackStack(null);
        transaction.commit();

    }
    public void setupOrderFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        OrderFragment newCustomFragment = new OrderFragment();
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
    public void setupHistoryFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",userModel);
        bundle.putString("email",firebaseUser.getEmail());
        HistoryFragment newCustomFragment = new HistoryFragment();
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
        }else {
            stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(MainActivity.this));
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
            setupCartFragment();
        }else if(item.getItemId()==R.id.nav_my_orders){
            Glide.with(MainActivity.this).load(ordersImageString)
                    .centerCrop()
                    .into(mainImage);
            setupOrderFragment();
        }else if(item.getItemId()==R.id.nav_my_profile){
            setupProfileFragment();
            Glide.with(MainActivity.this).load(profileImageString)
                    .centerCrop()
                    .into(mainImage);
        }else if(item.getItemId()==R.id.nav_histroy){
            setupHistoryFragment();
            Glide.with(MainActivity.this).load(historyImageString)
                    .centerCrop()
                    .into(mainImage);
        }


        return false;
    }

    public int  paymentiflatecall(final double price) {
        AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
//        view = inflater.inflate(R.layout.payment_layout, null);
        view = inflater.inflate(R.layout.activity_add_payment, null);
        al.setView(view);
        final AlertDialog value = al.create();
        value.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        value.setCancelable(false);
        //final ListView lv=new ListView(this);
        cardInputWidget = view.findViewById(R.id.cardInputWidget);
        final EditText perName = view.findViewById(R.id.person_name);
        perName.setText(userModel.getName());
        perName.setFocusable(false);
        final EditText phoneNumber = view.findViewById(R.id.phone_number);
        Button payButton = view.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardInputWidget.getCard().validateCard() && !perName.getText().toString().isEmpty()
                        && !phoneNumber.getText().toString().isEmpty()) {
                    progressDialog.show();
                    value.dismiss();
                    UserPhoneNumber=phoneNumber.getText().toString();
                    paymentCall(price);
                }
                else
                    Toast.makeText(getApplicationContext(),"Invalid  Details",Toast.LENGTH_LONG).show();

                Log.e(TAG,"Funtion called");
            }
        });

        value.show();
        value.setCancelable(true);
        return 0;
    }

    public void paymentCall(double price){
        com.stripe.Stripe.apiKey = secretKey;
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            List<Object> paymentMethodTypes =
                    new ArrayList<>();
            paymentMethodTypes.add("card");
            Map<String, Object> paymentIntentParams = new HashMap<>();
//            paymentIntentParams.put("amount", ((int)price * 100)); //1 is the amount to be deducted, 100 is must since it accepts cents & 1£=100cents
            paymentIntentParams.put("amount", ((int)Math.round(price) * 100)); //1 is the amount to be deducted, 100 is must since it accepts cents & 1£=100cents
            Log.e(TAG,"rounded value "+(int)Math.round(price));
            paymentIntentParams.put("currency", "usd");
//            paymentIntentParams.put("currency", "GBP");
            paymentIntentParams.put("payment_method_types", paymentMethodTypes);
            try {
                com.stripe.model.PaymentIntent paymentIntent = com.stripe.model.PaymentIntent.create(paymentIntentParams);
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntent.getClientSecret());
                final Context context = getApplicationContext();
                stripe = new Stripe(context, PaymentConfiguration.getInstance(context).getPublishableKey());
                stripe.confirmPayment(MainActivity.this, confirmParams);
            }
            catch (Exception e){
                e.printStackTrace();
            }catch (Error e){
                e.printStackTrace();
            }
        }
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<MainActivity> activityRef;

        PaymentResultCallback(@NonNull MainActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final MainActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.e("stripePayment", "Payment Completed: " + gson.toJson(paymentIntent));
                try{
                    JSONObject jsonObject=new JSONObject(gson.toJson(paymentIntent));
                    if(jsonObject.getString("status").equalsIgnoreCase("Succeeded")){
                        //add order to database
                        //succsess a gai
                        Log.e(TAG,"amount paid");
//                        veneuModel.setTransectionId(jsonObject.getString("id"));
                        transectionId=jsonObject.getString("id");
//                        veneuModel.setPaymentId(jsonObject.getString("created"));
                        paymentId=jsonObject.getString("created");
                        Toast.makeText(getApplicationContext(),"amount paid",Toast.LENGTH_LONG).show();
                        CartFragment.instance.updateTable(transectionId,paymentId,UserPhoneNumber);

                    }
                }catch (Exception c){
                    c.printStackTrace();
                    Log.e("stripePayment","json exception gai ");
                }

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                try{
                    //un secc
                }catch (Exception c){
                    c.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Unable to proceed",Toast.LENGTH_LONG).show();
                }

                Log.e("stripePayment", "Payment Failed: " + paymentIntent.getLastPaymentError().getMessage());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final MainActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.e("stripePayment", "Error: " +e.getLocalizedMessage());
            e.printStackTrace();
            try{
                errorPop(e.getLocalizedMessage());
                progressDialog.dismiss();
            }catch (Exception c){
                c.printStackTrace();
            }
        }

    }



    public void errorPop(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(text);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }


}