package com.example.selfalarm.activity.messageActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        for (Object pdu : pdus) {
            String format = bundle.getString("format");
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            String sender = sms.getOriginatingAddress();
            String message = sms.getMessageBody();

            // Gửi broadcast nội bộ cho UI cập nhật
            Intent localIntent = new Intent("SMS_RECEIVED_ACTION");
            localIntent.putExtra("sender", sender);
            localIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);;

            // Gửi cho service xử lý nền
            Intent serviceIntent = new Intent(context, SmsService.class);
            serviceIntent.putExtra("sender", sender);
            serviceIntent.putExtra("message", message);
            context.startService(serviceIntent);
        }
    }
}