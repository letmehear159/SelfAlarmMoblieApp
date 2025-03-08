package com.example.selfalarm.activity.alarmActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.AlarmAdapter;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.fragment.BottomSheetFragment;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {
    private AlarmDao alarmDao = new AlarmDao(this);

    BottomSheetFragment bottomSheet = new BottomSheetFragment(alarmDao);

    EditDatetimeBottomSheet editDatetimeBottomSheet = new EditDatetimeBottomSheet();

    private List<Alarm> alarmList;


    private RecyclerView alarmRecyclerView;

    private AlarmAdapter alarmAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alarm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        alarmList = alarmDao.getAllAlarms();

        alarmAdapter = new AlarmAdapter(alarmList, this, getSupportFragmentManager(), editDatetimeBottomSheet);

        alarmRecyclerView = findViewById(R.id.rvReminderList);

        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmRecyclerView.setAdapter(alarmAdapter);

        bottomSheet.setOnAlarmAddedListener(newAlarm -> {
            alarmList.add(newAlarm);
            alarmAdapter.notifyDataSetChanged();
        });

        editDatetimeBottomSheet.setOnAlarmUpdatedListener(new EditDatetimeBottomSheet.OnAlarmUpdatedListener() {
            @Override
            public void onAlarmUpdated(int position, Alarm updatedAlarm) {
                alarmList.set(position, updatedAlarm);
                alarmAdapter.notifyItemChanged(position);
            }

            @Override
            public void onAlarmDeleted(int position) {
                alarmList.remove(position);
                alarmAdapter.notifyItemRemoved(position);

            }
        });


    }


    public void showBottomSheet(View view) {
        bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
    }


}
