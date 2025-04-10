package com.example.selfalarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarm.R;
//import com.example.selfalarm.activity.alarmActivity.AlarmActivity;
import com.example.selfalarm.activity.alarmActivity.AlarmActivity;
import com.example.selfalarm.activity.messageActivity.MessageActivity;
import com.example.selfalarm.activity.musicActivity.MusicActivity;
import com.example.selfalarm.activity.phoneActivity.PhoneCallActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void switchToAlarm(View view) {
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
    }

    public void switchToMessage(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    public void switchToMusic(View view) {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    public void switchToPhone(View view) {
        Intent intent = new Intent(this, PhoneCallActivity.class);
        startActivity(intent);
    }
}