package com.example.selfalarm.activity.messageActivity;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class SmsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.selfalarm.smsprovider";
    private static final String PATH_MESSAGES = "messages";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_MESSAGES);

    private static final int MESSAGES = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_MESSAGES, MESSAGES);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == MESSAGES) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"sender", "message", "time"});

            // Dữ liệu mẫu - bạn có thể thay bằng dữ liệu từ DB hoặc SMS thực tế
            cursor.addRow(new Object[]{"0123456789", "Hello from provider", "12:00 10/04/2025"});
            return cursor;
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        if (uriMatcher.match(uri) == MESSAGES) {
            return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".messages";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Chỉ thêm nếu bạn cần lưu dữ liệu tin nhắn
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}