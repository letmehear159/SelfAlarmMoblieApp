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
    private final EditDatetimeBottomSheet.OnAlarmUpdatedListener listener;

    public AlarmPagerAdapter(FragmentActivity fragmentActivity, List<Alarm> eventAlarms, List<Alarm> dailyAlarms,
                             EditDatetimeBottomSheet.OnAlarmUpdatedListener listener) {
        super(fragmentActivity);
        this.eventAlarmList = eventAlarms;
        this.dailyAlarmList = dailyAlarms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return EventAlarmFragment.newInstance(eventAlarmList,listener);
        } else {
            return DailyAlarmFragment.newInstance(dailyAlarmList,listener);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Hai tab: Events và Daily
    }
}