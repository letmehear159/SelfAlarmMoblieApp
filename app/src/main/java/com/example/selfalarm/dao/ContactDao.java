package com.example.selfalarm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.selfalarm.database.PhoneDbHelper;
import com.example.selfalarm.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactDao {
    private PhoneDbHelper dbHelper;
    private SQLiteDatabase db;

    public ContactDao(Context context) {
        dbHelper = new PhoneDbHelper(context);
    }

    public long addContact(Contact contact) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhoneDbHelper.COLUMN_CONTACT_NAME, contact.getName());
        values.put(PhoneDbHelper.COLUMN_CONTACT_PHONE, contact.getPhoneNumber());
        values.put(PhoneDbHelper.COLUMN_CONTACT_PHOTO, contact.getPhotoUri());

        long id = db.insert(PhoneDbHelper.TABLE_CONTACTS, null, values);
        db.close();
        return id;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PhoneDbHelper.TABLE_CONTACTS,
                null, null, null, null, null,
                PhoneDbHelper.COLUMN_CONTACT_NAME + " ASC");

        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_NAME)));
            contact.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHONE)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHOTO)));
            contacts.add(contact);
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public List<Contact> searchContacts(String query) {
        List<Contact> contacts = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String selection = PhoneDbHelper.COLUMN_CONTACT_NAME + " LIKE ? OR " +
                PhoneDbHelper.COLUMN_CONTACT_PHONE + " LIKE ?";
        String[] selectionArgs = new String[] { "%" + query + "%", "%" + query + "%" };

        Cursor cursor = db.query(
                PhoneDbHelper.TABLE_CONTACTS,
                null, selection, selectionArgs, null, null,
                PhoneDbHelper.COLUMN_CONTACT_NAME + " ASC");

        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_NAME)));
            contact.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHONE)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHOTO)));
            contacts.add(contact);
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public Contact getContactById(long id) {
        db = dbHelper.getReadableDatabase();

        String selection = PhoneDbHelper.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(
                PhoneDbHelper.TABLE_CONTACTS,
                null, selection, selectionArgs, null, null, null);

        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_NAME)));
            contact.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHONE)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHOTO)));
        }

        cursor.close();
        db.close();
        return contact;
    }

    public Contact getContactByPhone(String phoneNumber) {
        db = dbHelper.getReadableDatabase();

        String selection = PhoneDbHelper.COLUMN_CONTACT_PHONE + " = ?";
        String[] selectionArgs = new String[] { phoneNumber };

        Cursor cursor = db.query(
                PhoneDbHelper.TABLE_CONTACTS,
                null, selection, selectionArgs, null, null, null);

        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_NAME)));
            contact.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHONE)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDbHelper.COLUMN_CONTACT_PHOTO)));
        }

        cursor.close();
        db.close();
        return contact;
    }

    public int updateContact(Contact contact) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PhoneDbHelper.COLUMN_CONTACT_NAME, contact.getName());
        values.put(PhoneDbHelper.COLUMN_CONTACT_PHONE, contact.getPhoneNumber());
        values.put(PhoneDbHelper.COLUMN_CONTACT_PHOTO, contact.getPhotoUri());

        String selection = PhoneDbHelper.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(contact.getId()) };

        int rowsUpdated = db.update(
                PhoneDbHelper.TABLE_CONTACTS,
                values, selection, selectionArgs);

        db.close();
        return rowsUpdated;
    }

    public int deleteContact(long id) {
        db = dbHelper.getWritableDatabase();

        String selection = PhoneDbHelper.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };

        int rowsDeleted = db.delete(
                PhoneDbHelper.TABLE_CONTACTS,
                selection, selectionArgs);

        db.close();
        return rowsDeleted;
    }

    public void createDemoData() {
        // Kiểm tra xem đã có dữ liệu chưa
        if (getAllContacts().size() == 0) {
            // Thêm một số liên hệ demo
            addContact(new Contact("Nguyễn Văn An", "0912345678"));
            addContact(new Contact("Trần Thị Bình", "0987654321"));
            addContact(new Contact("Lê Văn Cường", "0932156784"));
            addContact(new Contact("Phạm Thị Dung", "0905123456"));
            addContact(new Contact("Hoàng Văn Em", "0976543210"));
        }
    }
}