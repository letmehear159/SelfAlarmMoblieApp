package com.example.selfalarm.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.selfalarm.R;
import com.example.selfalarm.dao.ContactDao;
import com.example.selfalarm.model.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ContactDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText contactNameEdit;
    private TextInputEditText contactPhoneEdit;
    private MaterialButton saveButton;

    private ContactDao contactDao;
    private long contactId = -1; // -1 có nghĩa là thêm mới
    private Contact currentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contactDao = new ContactDao(this);

        // Khởi tạo views
        toolbar = findViewById(R.id.toolbar);
        contactNameEdit = findViewById(R.id.contact_name_edit);
        contactPhoneEdit = findViewById(R.id.contact_phone_edit);
        saveButton = findViewById(R.id.save_button);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Kiểm tra xem là thêm mới hay chỉnh sửa
        if (getIntent().hasExtra("contact_id")) {
            contactId = getIntent().getLongExtra("contact_id", -1);
            loadContactData();
            setTitle("Chỉnh sửa liên hệ");
        } else {
            setTitle("Thêm liên hệ mới");
        }

        // Xử lý sự kiện lưu
        saveButton.setOnClickListener(view -> saveContact());

        // Xử lý sự kiện back
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void loadContactData() {
        currentContact = contactDao.getContactById(contactId);
        if (currentContact != null) {
            contactNameEdit.setText(currentContact.getName());
            contactPhoneEdit.setText(currentContact.getPhoneNumber());
        }
    }

    private void saveContact() {
        String name = contactNameEdit.getText().toString().trim();
        String phone = contactPhoneEdit.getText().toString().trim();

        if (name.isEmpty()) {
            contactNameEdit.setError("Vui lòng nhập tên");
            return;
        }

        if (phone.isEmpty()) {
            contactPhoneEdit.setError("Vui lòng nhập số điện thoại");
            return;
        }

        if (contactId == -1) {
            // Thêm mới
            Contact newContact = new Contact(name, phone);
            long result = contactDao.addContact(newContact);
            if (result > 0) {
                Toast.makeText(this, "Đã thêm liên hệ mới", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể thêm liên hệ", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Cập nhật
            currentContact.setName(name);
            currentContact.setPhoneNumber(phone);
            int result = contactDao.updateContact(currentContact);
            if (result > 0) {
                Toast.makeText(this, "Đã cập nhật liên hệ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể cập nhật liên hệ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Chỉ hiển thị menu xóa nếu đang chỉnh sửa liên hệ hiện có
        if (contactId != -1) {
            getMenuInflater().inflate(R.menu.menu_contact_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            confirmDeleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteContact() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa liên hệ")
                .setMessage("Bạn có chắc muốn xóa liên hệ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteContact();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteContact() {
        if (contactId != -1) {
            int result = contactDao.deleteContact(contactId);
            if (result > 0) {
                Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể xóa liên hệ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}