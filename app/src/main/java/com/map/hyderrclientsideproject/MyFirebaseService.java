package com.map.hyderrclientsideproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class MyFirebaseService extends FirebaseMessagingService {
    String TAG="***service";
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void sendNotification(String title, String messageBody) {
        Log.e(TAG,"My Notificaiton called");
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("callType", "Firebase");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123456 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent viewOfferIntent = new Intent(this, SplashActivity.class);
        viewOfferIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        viewOfferIntent.setAction("View Offer");

        Intent resultIntent = new Intent(this, SplashActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(SplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent viewOfferPendingIntent =
                PendingIntent.getActivity(this, 123456, viewOfferIntent, 0);

        String channelId = "nearbyChannel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        //.addAction(android.R.drawable.ic_menu_sort_alphabetically, "View Offer", viewOfferPendingIntent)
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "nearbyChannel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(123456 /* ID of notification */, notificationBuilder.build());
//        try{
//            startActivity(new Intent(this,SplashActivity.class));
//        }catch (Exception c){
//            c.printStackTrace();
//        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        try{
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            myRef = FirebaseDatabase.getInstance().getReference("Users");
            myRef .child("Restaurants").child(firebaseUser.getUid()).child("fcmToken").child(token);
        }catch (Exception c){
            c.printStackTrace();
        }

    }

}