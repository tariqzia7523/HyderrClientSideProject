package com.map.hyderrclientsideproject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {


    String[] PERMISSIONS = {Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(getApplicationContext());
        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, 150);
                }else {
//                    startActivity(new Intent(Splash.this, UserTypeSelectorActivity.class));
//                    finish();
                    decideFlow();
                }


            }
        }, 3000);


    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==150){
//            startActivity(new Intent(Splash.this, UserTypeSelectorActivity.class));
//            finish();
            decideFlow();
        }
    }

    public void decideFlow(){
        if(currentUser==null ){
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
    }
}