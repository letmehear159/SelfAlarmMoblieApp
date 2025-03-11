package com.example.selfalarm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.selfalarm.database.PhoneDbHelper;
import com.example.selfalarm.model.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogDao {
    private PhoneDbHelper dbHelper;
    private SQLiteDatabase db;

    public CallLogDao(Context context) {
        dbHelper = new PhoneDbHelper(context);
    }

    public long addCallLog(CallLog callLog) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhoneDbHelper.COLUMN_CALL_PHONE, callLog.getPhoneNumber());
        values.put(PhoneDbHelper.COLUMN_CALL_NAME, callLog.getName());
        values.put(PhoneDbHelper.COLUMN_CALL_TIMESTAMP, callLog.getTimestamp());
        values.put(PhoneDbHelper.COLUMN_CALL_DURATION, callLog.getDuration());
        values.put(PhoneDbHelper.COLUMN_CALL_TYPE, callLog.getCallType());

        long id = db.insert(PhoneDbHelper.TABLE_CALL_LOGS, null, values);
        db.close();
        return id;
    }

    public List<CallLog> getAllCallLogs() {
        return getCallLogs(null, null);
    }

    public List<CallLog> getCallLogsByType(int callType) {
        String selection = PhoneDbHelper.COLUMN_CALL_TYPE + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(callType) };

        return getCallLogs(selection, selectionArgs);
    }

    private List<CallLog> getCallLogs(String selection, String[] selectionArgs) {
        List<CallLog> callLogs = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PhoneDbHelper.TABLE_CALL_LOGS,
                null, selection, selectionArgs, null, null,
                PhoneDbHelper.COLUMN_CALL_TIMESTAMP + " DESC");

        while (cursor.moveToNext()) {
            CallLog callLog = new CallLog();
            callLog.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_ID)));
            callLog.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_PHONE)));
            callLog.setName(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_NAME)));
            callLog.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_TIMESTAMP)));
            callLog.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_DURATION)));
            callLog.setCallType(cursor.getInt(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CALL_TYPE)));
            callLogs.add(callLog);
        }

        cursor.close();
        db.close();
        return callLogs;
    }

    public int deleteCallLog(long id) {
        db = dbHelper.getWritableDatabase();

        String selection = PhoneDbHelper.COLUMN_CALL_ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };

        int rowsDeleted = db.delete(
                PhoneDbHelper.TABLE_CALL_LOGS,
                selection, selectionArgs);

        db.close();
        return rowsDeleted;
    }

    public int clearCallLogs() {
        db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(PhoneDbHelper.TABLE_CALL_LOGS, null, null);
        db.close();
        return rowsDeleted;
    }
}