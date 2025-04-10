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

public class EventAlarmFragment extends Fragment {
    private List<Alarm> eventAlarmList;
    private AlarmAdapter alarmAdapter;

    private EditDatetimeBottomSheet editDatetimeBottomSheet;

    public EventAlarmFragment() {
        // Required empty public constructor
    }

    public static EventAlarmFragment newInstance(List<Alarm> eventAlarms, EditDatetimeBottomSheet editDatetimeBottomSheet) {
        EventAlarmFragment fragment = new EventAlarmFragment();
        fragment.eventAlarmList = eventAlarms;
        fragment.editDatetimeBottomSheet = editDatetimeBottomSheet;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvAlarmList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmAdapter = new AlarmAdapter(eventAlarmList, getActivity(), getParentFragmentManager(), editDatetimeBottomSheet);
        recyclerView.setAdapter(alarmAdapter);
        return view;
    }

    public void updateList(List<Alarm> newList) {
        this.eventAlarmList = newList;
        if (alarmAdapter != null) {
            alarmAdapter.updateData(eventAlarmList);
        }
    }
}
