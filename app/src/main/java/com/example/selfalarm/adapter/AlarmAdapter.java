package com.example.selfalarm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.broadcast_receiver.AlarmReceiver;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private Context context;
    private FragmentManager fragmentManager;
    EditDatetimeBottomSheet editDatetimeBottomSheet;
    private AlarmDao alarmDao;

    public AlarmAdapter(List<Alarm> alarmList, Context context, FragmentManager fragmentManager, EditDatetimeBottomSheet editDatetimeBottomSheet) {
        this.alarmList = alarmList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.editDatetimeBottomSheet = editDatetimeBottomSheet;
        this.alarmDao = new AlarmDao(context);

    }

    @NonNull
    @Override
    //This function is used to create a new ViewHolder object with custom style for each object inn alarm List
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }


    //This function is used to bind the data to the ViewHolder
    //To be more concise, it means that for each of object in List, it will create a custom layout(item_alarm)
    //Then passes data to that layout and adds it to the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        // Get the alarm at the current position
        Alarm alarm = alarmList.get(position);

        // Hiển thị thời gian dựa trên isRepeating
        if (alarm.getIsRepeating() == 1) {
            // Daily alarm: Chỉ hiển thị giờ, ẩn ngày
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = timeFormat.format(new Date(alarm.getTimestamp()));
            holder.tvTime.setText(timeString);
            holder.tvDate.setVisibility(View.GONE); // Ẩn tvDate
        } else {
            // Event alarm: Hiển thị cả ngày và giờ
            holder.tvDate.setVisibility(View.VISIBLE); // Hiện tvDate
            setDateTimeToTextView(alarm.getTimestamp(), holder.tvDate, holder.tvTime);
        }

        // Bind nội dung và trạng thái
        holder.tvMessage.setText(alarm.getContent());
        holder.swSet.setChecked(alarm.getIsEnabled() == 1);

        // Bind data to EditDateTimeBottomSheet
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("id", alarm.getId());
            bundle.putLong("timestamp", alarm.getTimestamp());
            bundle.putString("content", alarm.getContent());
            bundle.putInt("isEnabled", alarm.getIsEnabled());
            bundle.putInt("position", position); // Để biết vị trí cần cập nhật
            bundle.putInt("isRepeating", alarm.getIsRepeating());

            editDatetimeBottomSheet.setArguments(bundle);
            editDatetimeBottomSheet.show(fragmentManager, "EditDatetimeBottomSheet");
        });

        // Xử lý sự kiện toggle switch
        holder.swSet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setIsEnabled(isChecked ? 1 : 0);
            // Cập nhật vào database
            alarmDao.updateAlarm(alarm);

            // Cập nhật alarm trong AlarmManager
            if (isChecked) {
                AlarmReceiver.setAlarm(context, alarm.getTimestamp(),
                        alarm.getContent(), (int) alarm.getId());
            } else {
                AlarmReceiver.cancelAlarm(context, (int) alarm.getId());
            }
        });
    }

    @Override
    //This function is used to get the size of alarm list, then the system uses it to calculate the number of items
    //Then passes each of it as position to take it out in onBindViewHolder
    public int getItemCount() {
        return alarmList.size();
    }

    // This class is created to store data for each item in the RecyclerView
    //Than the constructor is used to pass data to the ViewHolder
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;
        TextView tvMessage;
        TextView tvDate;
        com.google.android.material.switchmaterial.SwitchMaterial swSet;

        //Link the data to the ViewHolder
        //That means when we use holder.Set... the data on the ViewHolder(item_alarm) will be updated
        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            swSet = itemView.findViewById(R.id.swSet);
        }
    }

    public void setDateTimeToTextView(long timeStamp, TextView dateTextView, TextView timeTextView) {
        // Tạo đối tượng Date từ timestamp
        Date date = new Date(timeStamp);

        // Định dạng cho ngày (ví dụ: "07/03/2025")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(date);

        // Định dạng cho giờ (ví dụ: "14:30")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeString = timeFormat.format(date);

        // Truyền vào TextView
        dateTextView.setText(dateString);
        timeTextView.setText(timeString);
    }

    // Thêm phương thức để cập nhật dữ liệu
    public void updateData(List<Alarm> newList) {
//        this.alarmList.clear();
//        this.alarmList.addAll(newList);
        notifyDataSetChanged();
    }
}
