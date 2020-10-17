package com.map.hyderrclientsideproject;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {


    DatabaseReference myRef;
    UserModel userModel;
    EditText edname,edemail,edaddress;
    String emailString;
    ImageView profileImage;
    Uri imageUri;
    public static ProfileFragment instance;
    Location curLocation;
    private StorageReference mStorageRef;
    ProgressBar progressBar;
    TextView signuptext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        instance=this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child("Clients");
        userModel =(UserModel) getArguments().getSerializable("data");
        emailString =getArguments().getString("email");
        edname=v.findViewById(R.id.name);
        edemail=v.findViewById(R.id.email);
        edaddress=v.findViewById(R.id.address);
        profileImage=v.findViewById(R.id.profile_image);
        progressBar=v.findViewById(R.id.progress_bar);
        signuptext=v.findViewById(R.id.login_text);
        if(userModel!=null){
//            Toast.makeText(getActivity(),"Model Not null",Toast.LENGTH_LONG).show();
            edname.setText(userModel.getName());
            edemail.setText(emailString);
            edemail.setFocusable(false);
            edaddress.setText(userModel.getAddress());
            Glide.with(getActivity()).load(userModel.getImageUrl())
                    .centerCrop()
                    .into(profileImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                getActivity().startActivityForResult(photoPickerIntent, 100);
            }
        });

        v.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edaddress.getText().toString().isEmpty() ||
                        edname.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"Please add all data",Toast.LENGTH_LONG).show();
                }else if(userModel.getImageUrl().isEmpty() && imageUri==null){
                    Toast.makeText(getActivity(),"Select Image",Toast.LENGTH_LONG).show();
                }else if(!userModel.getImageUrl().isEmpty() && imageUri==null){
                    HashMap<String ,Object> udate=new HashMap<>();
                    udate.put("address",edaddress.getText().toString());
                    udate.put("name",edname.getText().toString());
                    try{
                        udate.put("lat",curLocation.getLatitude());
                        udate.put("lng",curLocation.getLongitude());
                    }catch (Exception c){
                        c.printStackTrace();
                    }

                    myRef.child(userModel.getId()).updateChildren(udate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            signuptext.setVisibility(View.VISIBLE);
                            startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            progressBar.setVisibility(View.GONE);
                            signuptext.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    if(imageUri!=null){
                        final StorageReference riversRef = mStorageRef.child("User/"+userModel.getId());
                        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d("image View", "onSuccess: uri= "+ uri.toString());
                                        HashMap<String,Object> udate=new HashMap<>();
                                        udate.put("address",edaddress.getText().toString());
                                        udate.put("imageUrl",uri.toString());
                                        udate.put("name",edname.getText().toString());
                                        udate.put("lat",curLocation.getLatitude());
                                        udate.put("lng",curLocation.getLongitude());
                                        myRef.child(userModel.getId()).updateChildren(udate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);
                                                signuptext.setVisibility(View.VISIBLE);
                                                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));

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
                    }
                }

            }
        });

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return v;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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



        return v;
    }


    public void updateImage(Uri uri){
        imageUri=uri;
        try {
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            profileImage.setImageBitmap(selectedImage);
            userModel.setImageUrl("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }


}
