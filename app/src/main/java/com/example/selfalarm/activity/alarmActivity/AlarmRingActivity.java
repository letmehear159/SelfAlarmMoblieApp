package com.example.selfalarm.activity.alarmActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.selfalarm.R;
import com.example.selfalarm.broadcast_receiver.AlarmReceiver;

public class AlarmRingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        // Hiển thị màn hình ngay cả khi khóa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String content = getIntent().getStringExtra("content");
        TextView tvContent = findViewById(R.id.tvAlarmContent);
        tvContent.setText(content);

        Button btnDismiss = findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(v -> {
            AlarmReceiver.stopAlarmSound();
            finish();
        });
    }
}