package com.example.selfalarm.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.CallLogAdapter;
import com.example.selfalarm.dao.CallLogDao;
import com.example.selfalarm.model.CallLog;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView callHistoryList;
    private CallLogAdapter callLogAdapter;
    private CallLogDao callLogDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        callLogDao = new CallLogDao(this);
        callHistoryList = findViewById(R.id.call_history_list);

        // Setup RecyclerView
        callHistoryList.setLayoutManager(new LinearLayoutManager(this));
        loadCallHistory();
    }

    private void loadCallHistory() {
        List<CallLog> callLogs = callLogDao.getAllCallLogs(); // Ensure this method exists in your CallLogDao
        callLogAdapter = new CallLogAdapter(this, callLogs);
        callHistoryList.setAdapter(callLogAdapter);
    }
}