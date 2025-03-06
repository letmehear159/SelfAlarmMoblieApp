package com.example.selfalarm.activity.messageActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.sender.setText(message.getSender());
        holder.lastMessage.setText(message.getLastMessage());
        holder.time.setText(message.getTime());

        // Khi nhấn vào tin nhắn, mở DetailMessageActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMessageActivity.class);
            intent.putExtra("sender", message.getSender()); // Truyền tên thuê bao
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView sender, lastMessage, time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.tvSender);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}