package com.example.selfalarm.helper;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.selfalarm.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "BatteryChannel";

    // Tạo kênh thông báo (dùng cho Android 8.0+)
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Battery Alerts";
            String description = "Thông báo về mức pin";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Hiển thị thông báo khi pin yếu
    public static void showBatteryNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo intent để chuyển hướng đến cài đặt tiết kiệm pin
        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS);
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(context, 0, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo thông báo với nút hành động
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Thay bằng icon của bạn
                .setContentTitle("Pin yếu")
                .setContentText("Pin của bạn còn 20%. Chuyển đến cài đặt để bật tiết kiệm pin?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.notification, "Cài đặt", settingsPendingIntent) // Nút chuyển hướng
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
