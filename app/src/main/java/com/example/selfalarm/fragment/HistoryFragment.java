package com.example.selfalarm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.CallLogAdapter;
import com.example.selfalarm.dao.CallLogDao;
import com.example.selfalarm.model.CallLog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView callHistoryList;
    private ChipGroup filterChips;
    private Chip chipAllCalls, chipMissedCalls, chipOutgoingCalls, chipIncomingCalls;

    private CallLogAdapter callLogAdapter;
    private CallLogDao callLogDao;
    private List<CallLog> callLogs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_phone_call, container, false);

        callLogDao = new CallLogDao(requireContext());

        // Khởi tạo views
        callHistoryList = view.findViewById(R.id.call_history_list);
        filterChips = view.findViewById(R.id.call_filter_chips);
        chipAllCalls = view.findViewById(R.id.chip_all_calls);
        chipMissedCalls = view.findViewById(R.id.chip_missed_calls);
        chipOutgoingCalls = view.findViewById(R.id.chip_outgoing_calls);
        chipIncomingCalls = view.findViewById(R.id.chip_incoming_calls);

        // Setup RecyclerView
        callHistoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        callLogAdapter = new CallLogAdapter(requireContext(), callLogs);
        callHistoryList.setAdapter(callLogAdapter);

        // Setup filter chips
        filterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all_calls) {
                loadAllCallLogs();
            } else if (checkedId == R.id.chip_missed_calls) {
                loadCallLogsByType(CallLog.CALL_TYPE_MISSED);
            } else if (checkedId == R.id.chip_outgoing_calls) {
                loadCallLogsByType(CallLog.CALL_TYPE_OUTGOING);
            } else if (checkedId == R.id.chip_incoming_calls) {
                loadCallLogsByType(CallLog.CALL_TYPE_INCOMING);
            }
        });

        // Mặc định chọn "Tất cả"
        chipAllCalls.setChecked(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại fragment
        loadAllCallLogs();
    }

    private void loadAllCallLogs() {
        callLogs = callLogDao.getAllCallLogs();
        callLogAdapter.updateData(callLogs);
    }

    private void loadCallLogsByType(int callType) {
        callLogs = callLogDao.getCallLogsByType(callType);
        callLogAdapter.updateData(callLogs);
    }
}