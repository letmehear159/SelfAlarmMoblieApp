package com.example.selfalarm.activity.messageActivity;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarm.R;

public class DetailMessageActivity extends AppCompatActivity {
//    private TextView tvTitle;
//    private RecyclerView recyclerView;
//    private ChatAdapter chatAdapter;
//    private List<ChatMessage> chatMessages;
//    private TextView tvSenderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_message_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

//        String sender = getIntent().getStringExtra("sender");
//        if (sender != null) {
//            tvTitle.setText(sender);
//        }

//        String senderName = getIntent().getStringExtra("sender");
//        tvSenderName.setText(senderName);
//
//        // Hard code lịch sử chat
//        loadChatHistory();
//
//        chatAdapter = new ChatAdapter(chatMessages);
//        recyclerView.setAdapter(chatAdapter);
    }

//    private void loadChatHistory() {
//        chatMessages = new ArrayList<>();
//        chatMessages.add(new ChatMessage("Hey, how are you?", false));
//        chatMessages.add(new ChatMessage("I'm good, what about you?", true));
//        chatMessages.add(new ChatMessage("I'm fine too. Any plans for today?", false));
//    }
}