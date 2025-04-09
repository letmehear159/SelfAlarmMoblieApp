package com.example.selfalarm.activity.alarmActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.widget.Toast;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.AlarmAdapter;
import com.example.selfalarm.broadcast_receiver.AlarmReceiver;
import com.example.selfalarm.broadcast_receiver.BatteryReceiver;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.fragment.BottomSheetFragment;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;
import com.example.selfalarm.helper.NotificationHelper;

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

    private List<Alarm> alarmList;
    private Button btnDeleteAll;


    private RecyclerView alarmRecyclerView;

    private AlarmAdapter alarmAdapter;
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



        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(v -> {
            alarmDao.deleteAllAlarms();
            alarmList.clear();          // Xóa tất cả dữ liệu trong danh sách hiện tại
            alarmAdapter.notifyDataSetChanged(); // Th
        });

        alarmList = alarmDao.getAllAlarms();

        alarmAdapter = new AlarmAdapter(alarmList, this, getSupportFragmentManager(), editDatetimeBottomSheet);

        alarmRecyclerView = findViewById(R.id.rvReminderList);

        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmRecyclerView.setAdapter(alarmAdapter);

        bottomSheet.setOnAlarmAddedListener(newAlarm -> {
            alarmList.add(newAlarm);
            sortAlarmList();
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

        // Battery saver
        NotificationHelper.createNotificationChannel(this);
        // Khởi tạo và đăng ký BroadcastReceiver
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

        // Kiểm tra và yêu cầu quyền
        // Calendar event to alarm
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
                // Quyền được cấp, có thể lấy events
                fetchCalendarEvents();
            } else {
                // Quyền bị từ chối, thông báo người dùng
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchCalendarEvents() {
        List<Alarm> calendarAlarms = new ArrayList<>();
        List<Long> userCalendarIds = fetchUserCalendars();
        if (userCalendarIds.isEmpty()) {
            Toast.makeText(this, "No user calendars found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Các cột cần truy vấn
        String[] projection = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DESCRIPTION
        };

        // Thời gian lọc
        long now = System.currentTimeMillis();
        long oneWeekLater = now + 7 * 24 * 60 * 60 * 1000;

        // Tạo điều kiện lọc dựa trên CALENDAR_ID
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

        // Truy vấn sự kiện từ lịch với bộ lọc
        Cursor cursor = getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection.toString(), // Áp dụng bộ lọc CALENDAR_ID
                selectionArgs,
                CalendarContract.Events.DTSTART + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                String title = cursor.getString(1);
                long startTime = cursor.getLong(2);
                String description = cursor.getString(3);

                Alarm alarm = new Alarm(startTime, title != null ? title : "Event");
                alarm.setId(eventId);
                alarm.setIsEnabled(1);
                calendarAlarms.add(alarm);
            }
            cursor.close();
        }

        convertCalendarEventsToAlarms(calendarAlarms);
    }


    private void convertCalendarEventsToAlarms(List<Alarm> calendarAlarms) {
        for (Alarm alarm : calendarAlarms) {
            // Kiểm tra xem alarm đã tồn tại chưa (dựa trên ID hoặc timestamp)
            boolean exists = false;
            for (Alarm existingAlarm : alarmList) {
                if (existingAlarm.getId() == alarm.getId() ||
                        existingAlarm.getTimestamp() == alarm.getTimestamp()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                // Thêm vào database
                long alarmId = alarmDao.addAlarm(alarm);
                alarm.setId(alarmId); // Cập nhật ID từ database

                // Đặt báo thức với AlarmManager
                if (alarm.getTimestamp() > System.currentTimeMillis()) {
                    AlarmReceiver.setAlarm(this, alarm.getTimestamp(), alarm.getContent(), (int) alarmId);
                }

                // Thêm vào danh sách UI
                alarmList.add(alarm);
            }
        }
        alarmAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
    }


    private List<Long> fetchUserCalendars() {
        List<Long> userCalendarIds = new ArrayList<>();

        String[] projection = new String[] {
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

                // Lọc lịch cá nhân (primary hoặc do bạn sở hữu)
                if (isPrimary == 1 ) { // Thay bằng email của bạn
                    userCalendarIds.add(calendarId);
                }

                Log.d("CalendarInfo", "ID: " + calendarId + ", Name: " + displayName +
                        ", Account: " + accountName + ", Primary: " + isPrimary);
            }
            cursor.close();
        }

        return userCalendarIds;
    }

    private void sortAlarmList() {
        Collections.sort(alarmList, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm a1, Alarm a2) {
                return Long.compare(a1.getTimestamp(), a2.getTimestamp());
            }
        });
    }
}
