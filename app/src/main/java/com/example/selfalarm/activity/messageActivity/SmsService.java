package com.example.selfalarm.activity.messageActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.selfalarm.R;

public class SmsService extends Service {

    private static final String TAG = "SmsService";
    private static final String CHANNEL_ID = "sms_channel_id";
    private static final String CHANNEL_NAME = "SMS Notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra("sender");
        String message = intent.getStringExtra("message");

        if (sender != null && message != null) {
            Log.d(TAG, "New SMS from " + sender + ": " + message);
            showNotification(sender, message);
        }

        return START_NOT_STICKY;
    }

    private void showNotification(String sender, String message) {
        Intent intent = new Intent(this, DetailMessageActivity.class);
        intent.putExtra("sender", sender);
        intent.putExtra("initialMessage", ""); // Mở mà không gửi lại
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.messagenoti) // icon tuỳ bạn
                .setContentTitle("Tin nhắn mới từ " + sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh thông báo cho tin nhắn SMS");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}