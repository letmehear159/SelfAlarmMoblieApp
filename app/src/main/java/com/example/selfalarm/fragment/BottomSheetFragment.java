package com.example.selfalarm.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.selfalarm.R;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private OnAlarmAddedListener listener;
    AlarmDao alarmDao;


    Button btnCancel;
    Button btnSave;

    Button btnRemove;

    public BottomSheetFragment(AlarmDao alarmDao) {
        this.alarmDao = alarmDao;
    }

    public void setOnAlarmAddedListener(OnAlarmAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_alarm, container, false);
        addFunctionForButton(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels; //2k8
            behavior.setMaxHeight(screenHeight * 10 / 11);       // Chiều cao tối đa là toàn màn hình
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Mở rộng hoàn toàn

// Đảm bảo layout mở rộng đúng cách
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.requestLayout();
        }
    }

    public void addFunctionForButton(View view) {
        btnSave = view.findViewById(R.id.btnSave);

        btnCancel = view.findViewById(R.id.btnCancel);

        btnRemove = view.findViewById(R.id.btnDelete);
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnSave.setOnClickListener(v -> {

            TimePicker timePicker = view.findViewById(R.id.timePicker);

            DatePicker datePicker = view.findViewById(R.id.datePicker);

            long timeStamp = getTimestampFromPicker(timePicker, datePicker);

            String content = ((EditText) view.findViewById(R.id.etDescription)).getText().toString();

            Alarm alarm = new Alarm(timeStamp, content);

            alarmDao.addAlarm(alarm);
            if (listener != null) {
                listener.onAlarmAdded(alarm);
            }

            Snackbar snackbar = Snackbar.make(view, "Add new alarm successfully", Snackbar.LENGTH_LONG);

            snackbar.show();

            dismiss();
        });

        btnRemove.setVisibility(View.GONE);
        btnRemove.setEnabled(false);

    }

    public static long getTimestampFromPicker(TimePicker timePicker, DatePicker datePicker) {
        // Tạo instance của Calendar
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();

        // Đặt các giá trị từ DatePicker và TimePicker
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month); // Lưu ý: month bắt đầu từ 0 (0 = Jan, 11 = Dec)
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour); // Giờ 24h
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // Đặt giây về 0
        calendar.set(Calendar.MILLISECOND, 0); // Đặt mili giây về 0

        // Lấy timestamp (mili giây)
        return calendar.getTimeInMillis();
    }
}
