package com.example.dogsapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dogsapp.BuildConfig;
import com.example.dogsapp.R;
import com.example.dogsapp.view.MainActivity;

public class NotificationHelper {

    public static final String CHANNEL_ID="Dogs channel id";
    public static final int NOTIFICAION_ID=123;

    private static NotificationHelper instance;
    private Context context;

    private NotificationHelper(Context context){
        this.context=context;
    }

    public static NotificationHelper getInstance(Context context){
        if (instance==null)
            instance=new NotificationHelper(context);
        return instance;
    }

    public void createNotification(){
        createNotificationChannel();
        Intent intent=new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,00,intent,0);

        Bitmap icon= BitmapFactory.decodeResource(context.getResources(), R.drawable.dog);

        Notification notification=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.dog_icon)
                .setLargeIcon(icon)
                .setContentTitle("Dogs retrived")
                .setContentText("This is a notification")
                .setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(icon)
                                .bigLargeIcon(null)
                )
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICAION_ID,notification);


    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name=CHANNEL_ID;
            String description="Dogs retrived notifications channel";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
