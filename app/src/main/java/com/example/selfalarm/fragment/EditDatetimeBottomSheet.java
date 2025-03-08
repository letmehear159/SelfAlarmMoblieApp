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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.selfalarm.R;
import com.example.selfalarm.dao.AlarmDao;
import com.example.selfalarm.entity.Alarm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

public class EditDatetimeBottomSheet extends BottomSheetDialogFragment {
    public interface OnAlarmUpdatedListener {
        void onAlarmUpdated(int position, Alarm updatedAlarm);

        void onAlarmDeleted(int position);
    }


    private EditText etDescription;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnSave, btnCancel, btnRemove;
    private OnAlarmUpdatedListener listener;
    private AlarmDao alarmDao;
    private Alarm alarm;

    private com.google.android.material.switchmaterial.SwitchMaterial swSet;

    public void setOnAlarmUpdatedListener(OnAlarmUpdatedListener listener) {
        this.listener = listener;
    }


    public EditDatetimeBottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_alarm, container, false);

        findViewsById(view);

        timePicker.setIs24HourView(false);

        if (getArguments() != null) {
            updateDataToEdit();
        }
        addFunctionToButton(view);


        return view;
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

    private void updateDataToEdit() {
        long timestamp = getArguments().getLong("timestamp");
        String content = getArguments().getString("content");
        int isEnabled = getArguments().getInt("isEnabled");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        etDescription.setText(content);
//        swSet.setChecked(isEnabled == 1);
    }

    public void findViewsById(View view) {
        alarmDao = new AlarmDao(getActivity());
        btnSave = view.findViewById(R.id.btnSave);
        btnRemove = view.findViewById(R.id.btnDelete);
        btnCancel = view.findViewById(R.id.btnCancel);
        etDescription = view.findViewById(R.id.etDescription);
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        swSet = view.findViewById(R.id.swSet);
    }

    public void addFunctionToButton(View view) {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> {
            long newTimeStamp = BottomSheetFragment.getTimestampFromPicker(timePicker, datePicker);
            String newContent = etDescription.getText().toString();
            long id = getArguments().getLong("id");
            int position = getArguments().getInt("position");
            int isEnable = getArguments().getInt("isEnabled");
            Alarm updatedAlarm = new Alarm(id, newTimeStamp, newContent, isEnable);
            alarmDao.updateAlarm(updatedAlarm);
            if (listener != null) {
                listener.onAlarmUpdated(position, updatedAlarm);
            }
            Snackbar.make(view, "Updated alarm successfully", Snackbar.LENGTH_LONG).show();
            dismiss();
        });
        btnRemove.setOnClickListener(v -> {
            long id = getArguments().getLong("id");
            int position = getArguments().getInt("position");
            alarmDao.deleteAlarm(id);
            if (listener != null) {
                listener.onAlarmDeleted(position);
            }
            Snackbar.make(view, "Deleted alarm successfully", Snackbar.LENGTH_LONG).show();
            dismiss();
        });
    }
}
