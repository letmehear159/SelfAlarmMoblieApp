package com.example.selfalarm.activity.messageActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.selfalarm.R;

public class NewMessageActivity extends AppCompatActivity {

    private EditText editTo, editMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_message_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTo = findViewById(R.id.editTo);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Gửi sang DetailMessageActivity
        btnSend.setOnClickListener(v -> {
            String to = editTo.getText().toString().trim();
            String message = editMessage.getText().toString().trim();

            if (TextUtils.isEmpty(to) || TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Please enter both recipient and message", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS
                }, 1);
                return;
            }

            // Mở màn chat, truyền thông tin người nhận và tin nhắn đầu tiên
            Intent intent = new Intent(this, DetailMessageActivity.class);
            intent.putExtra("sender", to);
            intent.putExtra("initialMessage", message); // gửi luôn sau khi mở
            startActivity(intent);

            finish();
        });
    }
}