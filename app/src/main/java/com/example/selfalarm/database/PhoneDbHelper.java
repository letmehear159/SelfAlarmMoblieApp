package com.example.selfalarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhoneDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "phone_app.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng Contacts
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_CONTACT_ID = "_id";
    public static final String COLUMN_CONTACT_NAME = "name";
    public static final String COLUMN_CONTACT_PHONE = "phone_number";
    public static final String COLUMN_CONTACT_PHOTO = "photo_uri";

    // Bảng Call Logs
    public static final String TABLE_CALL_LOGS = "call_logs";
    public static final String COLUMN_CALL_ID = "_id";
    public static final String COLUMN_CALL_PHONE = "phone_number";
    public static final String COLUMN_CALL_NAME = "name";
    public static final String COLUMN_CALL_TIMESTAMP = "timestamp";
    public static final String COLUMN_CALL_DURATION = "duration";
    public static final String COLUMN_CALL_TYPE = "call_type";

    // SQL tạo bảng Contacts
    private static final String SQL_CREATE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + " (" +
            COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CONTACT_NAME + " TEXT, " +
            COLUMN_CONTACT_PHONE + " TEXT, " +
            COLUMN_CONTACT_PHOTO + " TEXT)";

    // SQL tạo bảng Call Logs
    private static final String SQL_CREATE_CALL_LOGS = "CREATE TABLE " + TABLE_CALL_LOGS + " (" +
            COLUMN_CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CALL_PHONE + " TEXT, " +
            COLUMN_CALL_NAME + " TEXT, " +
            COLUMN_CALL_TIMESTAMP + " INTEGER, " +
            COLUMN_CALL_DURATION + " INTEGER, " +
            COLUMN_CALL_TYPE + " INTEGER)";

    public PhoneDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_CALL_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOGS);
        onCreate(db);
    }
}