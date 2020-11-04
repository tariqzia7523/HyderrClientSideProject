package com.map.hyderrclientsideproject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edemail,edpassword;
    FirebaseAuth mAuth;
    String TAG;
    ProgressBar progressBar;
    TextView signuptext;
    FirebaseUser user;
    DatabaseReference myRef;
    String fcmToken;
    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";

    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        edemail=findViewById(R.id.email);
        edpassword=findViewById(R.id.password);
        TAG="***Login";
        getFcmToken();
        mCallbackManager = CallbackManager.Factory.create();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progress_bar);
        signuptext=findViewById(R.id.login_text);
        findViewById(R.id.sign_up_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));

            }
        });
        findViewById(R.id.google_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                signuptext.setVisibility(View.GONE);
                if(edemail.getText().toString().isEmpty() || edpassword.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(),"Please add all data",Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(edemail.getText().toString(), edpassword.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressBar.setVisibility(View.GONE);
                                    signuptext.setVisibility(View.VISIBLE);
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try{
                                                if(snapshot.child("Clients").hasChild(user.getUid())){
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }else if(snapshot.child("DeliveryMen").hasChild(user.getUid())){
                                                    FirebaseAuth.getInstance().signOut();
                                                    new AlertDialog.Builder(LoginActivity.this)
                                                            .setTitle("Credentials not valid! ")
                                                            .setMessage("You are registered as Delivery man.Press Ok to get respective app from Play Store")

                                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Continue with delete operation
                                                                    try {
                                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.new.deliveryprojectnew")));
                                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.new.deliveryprojectnew" )));
                                                                    }
                                                                }
                                                            })

                                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                                            .setNegativeButton(android.R.string.no, null)
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }else if(snapshot.child("Restaurants").hasChild(user.getUid())){
                                                    FirebaseAuth.getInstance().signOut();
                                                    new AlertDialog.Builder(LoginActivity.this)
                                                            .setTitle("Credentials not valid! ")
                                                            .setMessage("You are registered as restaurant owner.Press Ok to get respective app from Play Store")

                                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Continue with delete operation
                                                                    try {
                                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.map.hyderrresturentsideproject")));
                                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.map.hyderrresturentsideproject" )));
                                                                    }
                                                                }
                                                            })

                                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                                            .setNegativeButton(android.R.string.no, null)
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }


                                            }catch (Exception c){
                                                c.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });




                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    signuptext.setVisibility(View.VISIBLE);

                                }

                                // ...
                            }
                        });
            }
        });


        final LoginButton loginButton = findViewById(R.id.login_button_facebook);

        //Setting the permission that we need to read
//        loginButton.setReadPermissions("public_profile","email", "user_birthday");
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        //Registering callback!
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Sign in completed
                Log.i(TAG, "onSuccess: logged in successfully");

                //handling the token for Firebase Auth
                handleFacebookAccessToken(loginResult.getAccessToken());



            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        findViewById(R.id.facbook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.your_clint_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        progressBar.setVisibility(View.VISIBLE);
        signuptext.setVisibility(View.GONE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            Log.i(TAG, "onComplete: login completed with user: " + user.getDisplayName());

                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    // Application code
                                    Log.i(TAG, "onCompleted: response: " + response.toString());
                                    String email="";

                                    try {

//                            String birthday = object.getString("birthday");


//                            Log.i(TAG, "onCompleted: Birthday: " + birthday);
                                        String id=object.getString("id");
                                        try{
                                            email = object.getString("email");
                                            Log.i(TAG, "onCompleted: Email: " + email);
                                        }catch (Exception c){
                                            c.printStackTrace();
                                            email="test_"+id+"@test.com";
                                        }
                                        String name=object.getString("name");
//                            String email=object.getString("email"));
                                        JSONObject pictureObj=object.getJSONObject("picture");
                                        JSONObject pictureData=pictureObj.getJSONObject("data");
                                        final String ImageURL=pictureData.getString("url");
                                        String imagePath="http://graph.facebook.com/"+id+"/picture?type=large";
                                        Log.e(TAG,"resposne image "+ImageURL);
                                        imagePath=imagePath.replace("http:","https:");
                                        Log.e(TAG,"resposne image path "+imagePath);
                                        if(email.isEmpty()){

                                        }else{
                                            Map<String, Object> update = new HashMap<>();
                                            update.put("address", "address");
//                                            update.put("email", email);
                                            update.put("imageUrl", imagePath);
                                            update.put("fcmToken", fcmToken);
                                            update.put("name", name);
                                            update.put("type", "Client");

                                            SharedPreferences sharedpreferences = getSharedPreferences("MYREF", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor=sharedpreferences.edit();
                                            editor.putString("id",user.getUid()).apply();
                                            myRef.child("Clients").child(user.getUid()).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.GONE);
                                                    signuptext.setVisibility(View.VISIBLE);
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    progressBar.setVisibility(View.GONE);
                                                    signuptext.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
//                                        UserModel userModel=new UserModel(
//                                                name,
//                                                email,
//                                                "",
//                                                "",
//                                                imagePath,fcmToken);

//                                        myRef.child(user.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                progressDialog.dismiss();
//                                                startActivity(new Intent(LoginActivity.this, PlayerActivity.class));
//                                                finish();
//                                            }
//                                        }).addOnCanceledListener(new OnCanceledListener() {
//                                            @Override
//                                            public void onCanceled() {
//                                                progressDialog.dismiss();
//                                            }
//                                        });
////                            loginas="2";
////                                    Glide.with(SignUpActivity.this).load(getFacebookProfilePicture(id))
////                                            .asBitmap().override(300, 300).fitCenter().into(circle);
////                            SignUpActivity.this.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
//                            ImageView imageView=findViewById(R.id.icon);
//                                    Glide.with(LoginActivity.this).load(imagePath.replace("http:","https:")).into(imageView);
////                                    camera.setVisibility(View.GONE);
////                                    dialoginflateCall("Please add your address and password and click sign up to processed.",0);
////                                }
////                            });




                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.i(TAG, "onCompleted: JSON exception");
                                    }
                                }
                            });

                            Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender,birthday");
//                parameters.putString("fields", "id,name,email,picture");
                            parameters.putString("fields", "id,name,email,picture,gender,birthday");
                            request.setParameters(parameters);
                            request.executeAsync();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Log.e(TAG,"on actviity result  "+"handleSignInResult");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.e(TAG,"in add user called "+"handleSignInResult");


        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);


            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
            }

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
            firebaseAuthWithGoogle(acct.getIdToken(),acct);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
//            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String idToken,GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Log.e(TAG,"user created  ");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserTofirebase(user,account);

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void addUserTofirebase(FirebaseUser user,GoogleSignInAccount acct) {


     Log.e(TAG,"in add user called "+acct.getPhotoUrl());
        Map<String, Object> update = new HashMap<>();
        update.put("address", "address");
//                                            update.put("email", email);
        update.put("imageUrl", acct.getPhotoUrl().toString());
        update.put("fcmToken", fcmToken);
        update.put("name", acct.getDisplayName());
        update.put("type", "Client");

        SharedPreferences sharedpreferences = getSharedPreferences("MYREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.putString("id",user.getUid()).apply();
        myRef.child(user.getUid()).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                signuptext.setVisibility(View.VISIBLE);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressBar.setVisibility(View.GONE);
                signuptext.setVisibility(View.VISIBLE);
            }
        });
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


}