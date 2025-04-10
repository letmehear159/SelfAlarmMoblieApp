package com.example.selfalarm.activity.messageActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.selfalarm.R;
import java.util.ArrayList;
import java.util.List;

public class DetailMessageActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private String sender;
    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_message);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_message_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        EditText editMessage = findViewById(R.id.editMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        chatRecycler = findViewById(R.id.recyclerChatHistory);

        sender = getIntent().getStringExtra("sender");
        String initialMessage = getIntent().getStringExtra("initialMessage");

        if (sender != null) tvTitle.setText(sender);
        else tvTitle.setText("Chat");

        chatAdapter = new ChatAdapter(chatMessages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String text = editMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendSms(text);
                editMessage.setText("");
            }
        });

        checkSmsPermissions();

        if (sender != null && !sender.isEmpty() && initialMessage != null && !initialMessage.isEmpty()) {
            sendSms(initialMessage);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void sendSms(String message) {
        SmsManager.getDefault().sendTextMessage(sender, null, message, null, null);
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecycler.scrollToPosition(chatMessages.size() - 1);
    }

    private void loadSmsThread(String address) {
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = getContentResolver().query(uri, null, "address=?", new String[]{address}, "date ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                chatMessages.add(new ChatMessage(body, type == 2));
            }
            cursor.close();
            chatAdapter.notifyDataSetChanged();
            chatRecycler.scrollToPosition(chatMessages.size() - 1);
        }
    }

    private void checkSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_PHONE_STATE
            }, 1);
        } else {
            loadSmsThread(sender);
            registerSmsReceiver();
        }
    }

    private void registerSmsReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String from = intent.getStringExtra("sender");
                String body = intent.getStringExtra("message");
                if (from != null && from.equals(sender)) {
                    chatMessages.add(new ChatMessage(body, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecycler.scrollToPosition(chatMessages.size() - 1);
                }
            }
        };

        IntentFilter filter = new IntentFilter("SMS_RECEIVED_ACTION");
        LocalBroadcastManager.getInstance(this).registerReceiver(smsReceiver, filter);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted");
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền thông báo để hiển thị tin nhắn mới", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSmsThread(sender);
            registerSmsReceiver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        }
    }
}