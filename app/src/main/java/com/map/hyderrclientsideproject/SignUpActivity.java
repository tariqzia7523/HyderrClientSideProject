package com.map.hyderrclientsideproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText edname,edemail,edpassword,edconfirmPassword,edaddress;
    ImageView imageView;
    Uri imageUri;
    String TAG,fcmToken,imageUrl;
    ProgressBar progressBar;
    TextView signuptext;
    StorageReference mStorageRef;
    DatabaseReference myRef;
    Location curLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child("Clients");
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        getFcmToken();
        edname=findViewById(R.id.name);
        edemail=findViewById(R.id.email);
        edaddress=findViewById(R.id.address);
        edpassword=findViewById(R.id.password);
        edconfirmPassword=findViewById(R.id.confrim_password);
        imageView=findViewById(R.id.profile_image);
        progressBar=findViewById(R.id.progress_bar);
        signuptext=findViewById(R.id.login_text);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);
            }
        });
        findViewById(R.id.login_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

            }
        });
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=edname.getText().toString();
                final String email=edemail.getText().toString();
                String pass=edpassword.getText().toString();
                String conPass=edconfirmPassword.getText().toString();
                if(pass.equals(conPass)){
                    if(!name.isEmpty() || !email.isEmpty()){
                        if(imageUri==null){
                            Toast.makeText(getApplicationContext(),"Please select an image too.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        signuptext.setVisibility(View.GONE);
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            currentUser = mAuth.getCurrentUser();

                                            final StorageReference riversRef = mStorageRef.child("User/"+currentUser.getUid());
                                            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            Log.d("image View", "onSuccess: uri= "+ uri.toString());
                                                            imageUrl=uri.toString();
                                                            UserModel resturentUserModel=new UserModel();
                                                            resturentUserModel.setAddress(edaddress.getText().toString());
                                                            resturentUserModel.setFcmToken(fcmToken);
                                                            resturentUserModel.setImageUrl(imageUrl);
                                                            resturentUserModel.setName(edname.getText().toString());
                                                            resturentUserModel.setType("User");
                                                            if(curLocation!=null){
                                                                resturentUserModel.setLat(curLocation.getLatitude());
                                                                resturentUserModel.setLng(curLocation.getLongitude());
                                                            }else{
                                                                resturentUserModel.setLat(0.0);
                                                                resturentUserModel.setLng(0.0);
                                                            }
                                                            myRef.child(currentUser.getUid()).setValue(resturentUserModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    signuptext.setVisibility(View.VISIBLE);
                                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));

                                                                }
                                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                                @Override
                                                                public void onCanceled() {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    signuptext.setVisibility(View.VISIBLE);
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });


                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            signuptext.setVisibility(View.VISIBLE);

                                        }

                                        // ...
                                    }
                                });

//                        UserModel userModel=new UserModel(name,email,gender,age)

                    }else{
                        Toast.makeText(getApplicationContext(),"Please add all data",Toast.LENGTH_LONG).show();

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Password does not match",Toast.LENGTH_LONG).show();
                }
            }
        });
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(SignUpActivity.this);
        if (ActivityCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SignUpActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        // Got last known location. In some rare situations this can be null.

                        if (location != null) {
                            curLocation = location;
                            edaddress.setText(getAddress(location.getLatitude(),location.getLongitude()));

//                            Toast.makeText(getActivity(),"called",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(SignUpActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    public void getFcmToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcmToken = task.getResult().getToken().toString();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            return add;

            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}