package com.example.selfalarm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.selfalarm.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class EditDatetimeBottomSheet extends BottomSheetDialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        TimePicker timePicker = view.findViewById(R.id.timePicker);
        DatePicker datePicker = view.findViewById(R.id.datePicker);

        timePicker.setHour(3);
        timePicker.setMinute(36);
        datePicker.init(
                2025, 3, 4,
                null
        );
        return view;
    }
}
