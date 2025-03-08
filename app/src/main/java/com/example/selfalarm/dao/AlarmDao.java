package com.example.selfalarm.dao;

import android.content.ContentValues;

import com.example.selfalarm.entity.Alarm;
import com.example.selfalarm.sqlHelper.DatabaseHelper;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class AlarmDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public AlarmDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addAlarm(Alarm alarm) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, alarm.getTimestamp());
        values.put(DatabaseHelper.COLUMN_CONTENT, alarm.getContent());
        values.put(DatabaseHelper.COLUMN_IS_ENABLED, alarm.getIsEnabled());
        // Không cần put COLUMN_ID, SQLite sẽ tự động gán
        long id = db.insert(DatabaseHelper.TABLE_NAME, null, values);  // Trả về id mới
        db.close();
        return id;
    }

    public List<Alarm> getAllAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            alarm.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP)));
            alarm.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT)));
            alarm.setIsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ENABLED)));
            alarms.add(alarm);
        }
        cursor.close();
        db.close();
        return alarms;
    }

    public int deleteAlarm(long id) {
        db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(
                DatabaseHelper.TABLE_NAME,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsDeleted;
    }

    public int updateAlarm(Alarm alarm) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, alarm.getTimestamp());
        values.put(DatabaseHelper.COLUMN_CONTENT, alarm.getContent());
        values.put(DatabaseHelper.COLUMN_IS_ENABLED, alarm.getIsEnabled());
        int rowsUpdated = db.update(
                DatabaseHelper.TABLE_NAME,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(alarm.getId())}
        );
        db.close();
        return rowsUpdated;
    }
}
