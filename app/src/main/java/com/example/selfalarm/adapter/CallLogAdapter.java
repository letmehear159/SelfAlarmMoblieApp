package com.example.selfalarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.model.CallLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {
    private Context context;
    private List<CallLog> callLogList;

    public CallLogAdapter(Context context, List<CallLog> callLogList) {
        this.context = context;
        this.callLogList = callLogList;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo layout mới cho item call log vì layout item_phone_contact không phù hợp
        View view = LayoutInflater.from(context).inflate(R.layout.item_call_log, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLog callLog = callLogList.get(position);

        // Set name or phone number
        String displayName = callLog.getName() != null && !callLog.getName().isEmpty()
                ? callLog.getName()
                : callLog.getPhoneNumber();
        holder.callName.setText(displayName);

        // Set call type icon and color
        switch (callLog.getCallType()) {
            case CallLog.CALL_TYPE_INCOMING:
                holder.callTypeIcon.setImageResource(R.drawable.ic_call_received);
                holder.callTypeIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorIcon));
                break;
            case CallLog.CALL_TYPE_OUTGOING:
                holder.callTypeIcon.setImageResource(R.drawable.ic_call_made);
                holder.callTypeIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorIcon));
                break;
            case CallLog.CALL_TYPE_MISSED:
                holder.callTypeIcon.setImageResource(R.drawable.ic_call_missed);
                holder.callTypeIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorError));
                break;
        }

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateTime = sdf.format(new Date(callLog.getTimestamp()));
        holder.callTime.setText(dateTime);

        // Format duration
        if (callLog.getCallType() == CallLog.CALL_TYPE_MISSED) {
            holder.callDuration.setText(R.string.missed_call);
        } else {
            int duration = callLog.getDuration();
            String durationText = String.format(Locale.getDefault(),
                    "%02d:%02d", duration / 60, duration % 60);
            holder.callDuration.setText(durationText);
        }

        // Handle click to call back
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + callLog.getPhoneNumber()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }

    public void updateData(List<CallLog> newCallLogList) {
        this.callLogList = newCallLogList;
        notifyDataSetChanged();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        ImageView callTypeIcon;
        TextView callName;
        TextView callTime;
        TextView callDuration;

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            callTypeIcon = itemView.findViewById(R.id.call_type_icon);
            callName = itemView.findViewById(R.id.call_name);
            callTime = itemView.findViewById(R.id.call_time);
            callDuration = itemView.findViewById(R.id.call_duration);
        }
    }
}