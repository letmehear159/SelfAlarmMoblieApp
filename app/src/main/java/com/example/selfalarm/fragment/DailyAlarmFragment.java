package com.example.selfalarm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.AlarmAdapter;
import com.example.selfalarm.entity.Alarm;

import java.util.List;

public class DailyAlarmFragment extends Fragment {
    private List<Alarm> dailyAlarmList;
    private AlarmAdapter alarmAdapter;
    private EditDatetimeBottomSheet.OnAlarmUpdatedListener listener;

    public DailyAlarmFragment() {
        // Required empty public constructor
    }

    public static DailyAlarmFragment newInstance(List<Alarm> dailyAlarms, EditDatetimeBottomSheet.OnAlarmUpdatedListener listener) {
        DailyAlarmFragment fragment = new DailyAlarmFragment();
        fragment.dailyAlarmList = dailyAlarms;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvAlarmList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmAdapter = new AlarmAdapter(dailyAlarmList, getActivity(), getParentFragmentManager(), new EditDatetimeBottomSheet(), listener);
        recyclerView.setAdapter(alarmAdapter);
        return view;
    }

    public void updateList(List<Alarm> newList) {
        this.dailyAlarmList = newList;
        if (alarmAdapter != null) {
            alarmAdapter.updateData(dailyAlarmList);

        }
    }
}