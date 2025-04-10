package com.example.selfalarm.activity.messageActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarm.R;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private List<Message> originalMessageList = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.message_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnNewMessage).setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, NewMessageActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchMessages);
        if (searchView == null) {
            Toast.makeText(this, "SearchView không tồn tại trong layout", Toast.LENGTH_SHORT).show();
            return;
        }

        setupSearchView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
            }, 1);
        } else {
            loadSmsInbox();
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // không làm gì khi enter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    restoreOriginalMessages();
                } else {
                    filterMessages(newText);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            restoreOriginalMessages();
            return false;
        });
    }

    private void restoreOriginalMessages() {
        messageList.clear();
        messageList.addAll(originalMessageList);
        adapter.notifyDataSetChanged();
    }

    private void filterMessages(String query) {
        String keyword = query.toLowerCase();
        List<Message> filtered = new ArrayList<>();

        for (Message message : originalMessageList) {
            String sender = message.getSender().toLowerCase();
            String lastMsg = message.getLastMessage().toLowerCase();

            if (sender.contains(keyword) || lastMsg.contains(keyword)) {
                filtered.add(message);
            }
        }

        messageList.clear();
        messageList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    private void loadSmsInbox() {
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = getContentResolver().query(uri, null, null, null, "date DESC");
        if (cursor == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        HashMap<String, Message> messageMap = new HashMap<>();
        HashMap<String, Long> timeMap = new HashMap<>();

        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            String formattedTime = sdf.format(new Date(date));

            if (address == null) continue;

            boolean isSentByMe = (type == 2);
            String prefix = isSentByMe ? "Bạn: " : "Họ: ";
            String displayText = prefix + body;

            if (!timeMap.containsKey(address) || date > timeMap.get(address)) {
                messageMap.put(address, new Message(address, displayText, formattedTime));
                timeMap.put(address, date);
            }
        }

        cursor.close();

        List<Message> sorted = new ArrayList<>(messageMap.values());
        sorted.sort((m1, m2) -> {
            long t1 = timeMap.get(m1.getSender());
            long t2 = timeMap.get(m2.getSender());
            return Long.compare(t2, t1);
        });

        messageList.clear();
        messageList.addAll(sorted);
        originalMessageList.clear();
        originalMessageList.addAll(sorted);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted");
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền thông báo để hiển thị tin nhắn mới", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSmsInbox();
        }
    }
}
