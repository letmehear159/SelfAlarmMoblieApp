package com.example.selfalarm.broadcast_receiver;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.example.selfalarm.R;
import com.example.selfalarm.activity.alarmActivity.AlarmActivity;
import com.example.selfalarm.activity.alarmActivity.AlarmRingActivity;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.helper.DatabaseHelper;

import java.util.Calendar;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "AlarmChannel";
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        int alarmId = intent.getIntExtra("alarmId", 0);

        // Đánh thức màn hình
        wakeUpScreen(context);

        // Hiển thị notification
        showNotification(context, content, alarmId);

        // Phát nhạc chuông
        playAlarmSound(context);

        Intent ringIntent = new Intent(context, AlarmRingActivity.class);
        ringIntent.putExtra("content", content);
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ringIntent);


        AlarmDao alarmDao = new AlarmDao(context);
        List<Alarm> alarms = alarmDao.getAllAlarms();
        for (Alarm alarm : alarms) {
            if ((int) alarm.getId() == alarmId && alarm.getIsRepeating() == 1) {
                // Tính timestamp cho ngày tiếp theo
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(alarm.getTimestamp());
                calendar.add(Calendar.DAY_OF_YEAR, 1); // +1 ngày
                long nextTimestamp = calendar.getTimeInMillis();
                alarm.setTimestamp(nextTimestamp);
                alarmDao.updateAlarm(alarm); // setAlarm đã được gọi trong updateAlarm
                break;
            }
        }
    }

    // Đánh thức màn hình
    private void wakeUpScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "SelfAlarm:WakeLock");
        wakeLock.acquire(10 * 60 * 1000L /*10 phút*/); // Thời gian giữ màn hình sáng
    }

    // Hiển thị notification
    private void showNotification(Context context, String content, int alarmId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for alarm notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Intent để mở Activity khi click notification
        Intent notificationIntent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, alarmId, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Thay bằng icon của bạn
                .setContentTitle("Alarm")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(alarmId, builder.build());
    }

    // Phát nhạc chuông
    private void playAlarmSound(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true); // Lặp lại nhạc chuông
        mediaPlayer.start();
    }

    // Dừng nhạc chuông (gọi khi cần)
    public static void stopAlarmSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Phương thức setAlarm đã cập nhật
    public static void setAlarm(Context context, long timestamp, String content, int alarmId) {
        android.app.AlarmManager alarmManager =
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("content", content);
        intent.putExtra("alarmId", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(
                android.app.AlarmManager.RTC_WAKEUP,
                timestamp,
                pendingIntent);
    }

    public static void cancelAlarm(Context context, int alarmId) {
        android.app.AlarmManager alarmManager =
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        stopAlarmSound();
    }
}
