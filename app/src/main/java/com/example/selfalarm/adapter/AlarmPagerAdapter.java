package com.example.selfalarm.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.fragment.DailyAlarmFragment;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;
import com.example.selfalarm.fragment.EventAlarmFragment;

import java.util.List;

public class AlarmPagerAdapter extends FragmentStateAdapter {
    private final List<Alarm> eventAlarmList;
    private final List<Alarm> dailyAlarmList;
    private final EditDatetimeBottomSheet editDatetimeBottomSheet;

    public AlarmPagerAdapter(FragmentActivity fragmentActivity, List<Alarm> eventAlarms, List<Alarm> dailyAlarms,
                             EditDatetimeBottomSheet editDatetimeBottomSheet) {
        super(fragmentActivity);
        this.eventAlarmList = eventAlarms;
        this.dailyAlarmList = dailyAlarms;
        this.editDatetimeBottomSheet = editDatetimeBottomSheet;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return EventAlarmFragment.newInstance(eventAlarmList,editDatetimeBottomSheet);
        } else {
            return DailyAlarmFragment.newInstance(dailyAlarmList,editDatetimeBottomSheet);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Hai tab: Events và Daily
    }
}