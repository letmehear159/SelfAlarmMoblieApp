package com.example.selfalarm.activity.alarmActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.AlarmAdapter;
import com.example.selfalarm.adapter.AlarmPagerAdapter;
import com.example.selfalarm.broadcast_receiver.AlarmReceiver;
import com.example.selfalarm.broadcast_receiver.BatteryReceiver;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.fragment.BottomSheetFragment;
import com.example.selfalarm.fragment.DailyAlarmFragment;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;
import com.example.selfalarm.fragment.EventAlarmFragment;
import com.example.selfalarm.helper.NotificationHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private static final int REQUEST_CALENDAR_PERMISSION = 100;
    private AlarmDao alarmDao = new AlarmDao(this);
    BottomSheetFragment bottomSheet = new BottomSheetFragment(alarmDao);
    EditDatetimeBottomSheet editDatetimeBottomSheet = new EditDatetimeBottomSheet();
    private List<Alarm> eventAlarmList = new ArrayList<>();
    private List<Alarm> dailyAlarmList = new ArrayList<>();
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AlarmPagerAdapter pagerAdapter;
    private Button btnDeleteAll;
    private BatteryReceiver batteryReceiver;

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

        // Khởi tạo TabLayout và ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Load dữ liệu từ database
        loadAlarmsFromDatabase();


        // Xử lý thêm báo thức từ BottomSheet
        bottomSheet.setOnAlarmAddedListener(newAlarm -> {
            if (newAlarm.getTimestamp() < System.currentTimeMillis() && newAlarm.getIsRepeating() == 1) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(newAlarm.getTimestamp());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                newAlarm.setTimestamp(calendar.getTimeInMillis());
            }
            addAlarmToList(newAlarm);
            updateFragments();
        });

        // Xử lý cập nhật và xóa từ EditDatetimeBottomSheet
        // Thiết lập ViewPager2 và TabLayout
        editDatetimeBottomSheet.setOnAlarmUpdatedListener(new EditDatetimeBottomSheet.OnAlarmUpdatedListener() {
            @Override
            public void onAlarmUpdated(int position, Alarm updatedAlarm) {
                updateAlarmInList(position, updatedAlarm);
                updateFragments();
                Log.d("AlarmActivity", "Alarm updated at position: " + position);
            }

            @Override
            public void onAlarmDeleted(int position) {
                removeAlarmFromList(position);
                updateFragments();
                Log.d("AlarmActivity", "Alarm deleted at position: " + position);
            }
        });
//        EditDatetimeBottomSheet.OnAlarmUpdatedListener listener = new EditDatetimeBottomSheet.OnAlarmUpdatedListener() {
//            @Override
//            public void onAlarmUpdated(int position, Alarm updatedAlarm) {
//                updateAlarmInList(position, updatedAlarm);
//                updateFragments();
//                Log.d("AlarmActivity", "Alarm updated at position: " + position);
//            }
//
//            @Override
//            public void onAlarmDeleted(int position) {
//                removeAlarmFromList(position);
//                updateFragments();
//                Log.d("AlarmActivity", "Alarm deleted at position: " + position);
//            }
//        };
        pagerAdapter = new AlarmPagerAdapter(this, eventAlarmList, dailyAlarmList, editDatetimeBottomSheet); // Truyền listener
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Event");
            } else {
                tab.setText("Daily");
            }
        }).attach();

        // Battery saver
        NotificationHelper.createNotificationChannel(this);
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

        // Nút Delete All
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(v -> {
            alarmDao.deleteAllAlarms();
            eventAlarmList.clear();
            dailyAlarmList.clear();
            updateFragments();
        });

        // Nút Import Calendar Events
        Button btnImportCalendar = findViewById(R.id.btnImportCalendar);
        btnImportCalendar.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                fetchCalendarEvents();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        REQUEST_CALENDAR_PERMISSION);
            }
        });
    }

    public void showBottomSheet(View view) {
        bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALENDAR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCalendarEvents();
            } else {
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAlarmsFromDatabase() {
        List<Alarm> allAlarms = alarmDao.getAllAlarms();
        eventAlarmList.clear();
        dailyAlarmList.clear();
        for (Alarm alarm : allAlarms) {
            if (alarm.getIsRepeating() == 1) {
                dailyAlarmList.add(alarm);
            } else {
                eventAlarmList.add(alarm);
            }
        }
        sortAlarmList(eventAlarmList);
        sortAlarmList(dailyAlarmList);
    }

    private void addAlarmToList(Alarm alarm) {
        if (alarm.getIsRepeating() == 1) {
            dailyAlarmList.add(alarm);
            sortAlarmList(dailyAlarmList);
        } else {
            eventAlarmList.add(alarm);
            sortAlarmList(eventAlarmList);
        }
    }

    private void updateAlarmInList(int position, Alarm updatedAlarm) {
        // Xác định alarm thuộc danh sách nào dựa trên position và cập nhật
        long alarmId = updatedAlarm.getId();
        boolean found = false;
        // Tìm trong eventAlarmList
        for (int i = 0; i < eventAlarmList.size(); i++) {
            if (eventAlarmList.get(i).getId() == alarmId) {
                found = true;
                eventAlarmList.remove(i); // Xóa báo thức cũ
                if (updatedAlarm.getIsRepeating() == 1) {
                    // Chuyển từ event sang daily
                    dailyAlarmList.add(updatedAlarm);
                    sortAlarmList(dailyAlarmList);
                    Log.d("AlarmActivity", "Moved alarm from event to daily, id: " + alarmId);
                } else {
                    // Giữ trong event
                    eventAlarmList.add(updatedAlarm);
                    sortAlarmList(eventAlarmList);
                    Log.d("AlarmActivity", "Updated alarm in event list, id: " + alarmId);
                }
                break;
            }
        }

        // Nếu không tìm thấy trong eventAlarmList, tìm trong dailyAlarmList
        if (!found) {
            for (int i = 0; i < dailyAlarmList.size(); i++) {
                if (dailyAlarmList.get(i).getId() == alarmId) {
                    dailyAlarmList.remove(i); // Xóa báo thức cũ
                    if (updatedAlarm.getIsRepeating() == 1) {
                        // Giữ trong daily
                        dailyAlarmList.add(updatedAlarm);
                        sortAlarmList(dailyAlarmList);
                        Log.d("AlarmActivity", "Updated alarm in daily list, id: " + alarmId);
                    } else {
                        // Chuyển từ daily sang event
                        eventAlarmList.add(updatedAlarm);
                        sortAlarmList(eventAlarmList);
                        Log.d("AlarmActivity", "Moved alarm from daily to event, id: " + alarmId);
                    }
                    break;
                }
            }
        }
    }

    private void removeAlarmFromList(int position) {
        int eventSize = eventAlarmList.size();
        if (position < eventSize) {
            eventAlarmList.remove(position);
        } else {
            dailyAlarmList.remove(position - eventSize);
        }
    }

    private void sortAlarmList(List<Alarm> list) {
        Collections.sort(list, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm a1, Alarm a2) {
                return Long.compare(a1.getTimestamp(), a2.getTimestamp());
            }
        });
    }

    private void updateFragments() {
        Fragment eventFragment = getSupportFragmentManager().findFragmentByTag("f0");
        if (eventFragment instanceof EventAlarmFragment) {
            ((EventAlarmFragment) eventFragment).updateList(eventAlarmList);
        }
        Fragment dailyFragment = getSupportFragmentManager().findFragmentByTag("f1");
        if (dailyFragment instanceof DailyAlarmFragment) {
            ((DailyAlarmFragment) dailyFragment).updateList(dailyAlarmList);
        }
    }

    private void fetchCalendarEvents() {
        List<Alarm> calendarAlarms = new ArrayList<>();
        List<Long> userCalendarIds = fetchUserCalendars();
        if (userCalendarIds.isEmpty()) {
            Toast.makeText(this, "No user calendars found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] projection = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DESCRIPTION
        };

        StringBuilder selection = new StringBuilder();
        selection.append(CalendarContract.Events.CALENDAR_ID + " IN (");
        for (int i = 0; i < userCalendarIds.size(); i++) {
            selection.append("?");
            if (i < userCalendarIds.size() - 1) selection.append(",");
        }
        selection.append(")");

        String[] selectionArgs = new String[userCalendarIds.size()];
        for (int i = 0; i < userCalendarIds.size(); i++) {
            selectionArgs[i] = String.valueOf(userCalendarIds.get(i));
        }

        Cursor cursor = getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection.toString(),
                selectionArgs,
                CalendarContract.Events.DTSTART + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                String title = cursor.getString(1);
                long startTime = cursor.getLong(2);
                String description = cursor.getString(3);

                Alarm alarm = new Alarm(startTime, title != null ? title : "Event", 0);
                alarm.setId(eventId);
                alarm.setIsEnabled(1);
                calendarAlarms.add(alarm);
            }
            cursor.close();
        }

        convertCalendarEventsToAlarms(calendarAlarms);
    }

    private void convertCalendarEventsToAlarms(List<Alarm> calendarAlarms) {
        long currentTime = System.currentTimeMillis();
        for (Alarm alarm : calendarAlarms) {
            boolean exists = false;
            for (Alarm existingAlarm : eventAlarmList) {
                if (existingAlarm.getId() == alarm.getId() ||
                        existingAlarm.getTimestamp() == alarm.getTimestamp()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                long alarmId = alarmDao.addAlarm(alarm);
                alarm.setId(alarmId);
                if (alarm.getTimestamp() > currentTime) {
                    AlarmReceiver.setAlarm(this, alarm.getTimestamp(), alarm.getContent(), (int) alarmId);
                }
                eventAlarmList.add(alarm);
                sortAlarmList(eventAlarmList);
            }
        }
        updateFragments();
    }

    private List<Long> fetchUserCalendars() {
        List<Long> userCalendarIds = new ArrayList<>();
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.IS_PRIMARY
        };

        Cursor cursor = getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long calendarId = cursor.getLong(0);
                String accountName = cursor.getString(1);
                String displayName = cursor.getString(2);
                int isPrimary = cursor.getInt(3);

                if (isPrimary == 1) {
                    userCalendarIds.add(calendarId);
                }

                Log.d("CalendarInfo", "ID: " + calendarId + ", Name: " + displayName +
                        ", Account: " + accountName + ", Primary: " + isPrimary);
            }
            cursor.close();
        }
        return userCalendarIds;
    }
}