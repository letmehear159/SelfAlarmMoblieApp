package com.example.selfalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;

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

        // Load tin nhắn mẫu
        loadSampleMessages();
        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(adapter);
    }

    private void loadSampleMessages() {
        messageList = new ArrayList<>();
        messageList.add(new Message("John Doe", "Hey, how are you?", "10:30 AM"));
        messageList.add(new Message("Alice", "Are you coming to the party?", "9:45 AM"));
        messageList.add(new Message("Bob", "Don't forget our meeting!", "Yesterday"));
    }
}