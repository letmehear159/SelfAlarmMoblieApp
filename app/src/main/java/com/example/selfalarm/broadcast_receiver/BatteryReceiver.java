package com.example.selfalarm.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.example.selfalarm.helper.NotificationHelper;

public class BatteryReceiver extends BroadcastReceiver {

    private boolean isNotified = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (level * 100) / scale;

        // Kiểm tra nếu pin xuống 20% và chưa gửi thông báo
        if (batteryPct <= 20 && !isNotified) {
            NotificationHelper.showBatteryNotification(context);
            isNotified = true;
        } else if (batteryPct > 20) {
            isNotified = false; // Reset khi pin vượt 20%
        }
    }
}
