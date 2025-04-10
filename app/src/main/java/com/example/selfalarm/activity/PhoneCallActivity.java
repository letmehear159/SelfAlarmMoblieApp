package com.example.selfalarm.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.selfalarm.R;
import com.example.selfalarm.dao.CallLogDao;
import com.example.selfalarm.dao.ContactDao;
import com.example.selfalarm.model.CallLog;
import com.example.selfalarm.model.Contact;
import com.google.android.material.button.MaterialButton;

public class PhoneCallActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView dialedNumber;
    private ImageButton backspaceButton;
    private MaterialButton callButton;
    private ImageButton backButton;
    private MaterialButton contactsButton;
    private MaterialButton historyButton;

    private ContactDao contactDao;
    private CallLogDao callLogDao;

    private StringBuilder phoneNumber = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call);

        contactDao = new ContactDao(this);
        callLogDao = new CallLogDao(this);

        // Khởi tạo views
        dialedNumber = findViewById(R.id.dialed_number);
        backspaceButton = findViewById(R.id.backspace_button);
        callButton = findViewById(R.id.call_button);
        backButton = findViewById(R.id.back_button);
        contactsButton = findViewById(R.id.contacts_button); // Ensure this ID matches
        historyButton = findViewById(R.id.history_button); // Ensure this ID matches

        // Set listeners
        backButton.setOnClickListener(view -> finish());
        backspaceButton.setOnClickListener(view -> {
            if (phoneNumber.length() > 0) {
                phoneNumber.deleteCharAt(phoneNumber.length() - 1);
                dialedNumber.setText(phoneNumber.toString());
            }
        });

        // Long press để xóa hết
        backspaceButton.setOnLongClickListener(view -> {
            phoneNumber.setLength(0);
            dialedNumber.setText("");
            return true;
        });

        callButton.setOnClickListener(view -> {
            if (phoneNumber.length() > 0) {
                makeCall(phoneNumber.toString());
            }
        });

        // Setup các nút bàn phím
        setupKeypadButtons();

        // Nếu có số được chuyển từ màn hình khác
        String initialNumber = getIntent().getStringExtra("phone_number");
        if (initialNumber != null && !initialNumber.isEmpty()) {
            phoneNumber.append(initialNumber);
            dialedNumber.setText(phoneNumber);
        }

        // Set click listeners for new buttons
        contactsButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactPhoneActivity.class);
            startActivity(intent);
        });

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, HistoryActivity.class); // Ensure this is the correct activity
            startActivity(intent);
        });
    }

    private void setupKeypadButtons() {
        int[] buttonIds = {
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9, R.id.button_star, R.id.button_hash
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String digit = "";

        if (id == R.id.button_0)
            digit = "0";
        else if (id == R.id.button_1)
            digit = "1";
        else if (id == R.id.button_2)
            digit = "2";
        else if (id == R.id.button_3)
            digit = "3";
        else if (id == R.id.button_4)
            digit = "4";
        else if (id == R.id.button_5)
            digit = "5";
        else if (id == R.id.button_6)
            digit = "6";
        else if (id == R.id.button_7)
            digit = "7";
        else if (id == R.id.button_8)
            digit = "8";
        else if (id == R.id.button_9)
            digit = "9";
        else if (id == R.id.button_star)
            digit = "*";
        else if (id == R.id.button_hash)
            digit = "#";

        phoneNumber.append(digit);
        dialedNumber.setText(phoneNumber.toString());

        if (id == R.id.contacts_button) {
            Intent intent = new Intent(this, ContactPhoneActivity.class);
            startActivity(intent);
        }
    }

    private void makeCall(String number) {
        // Lưu vào lịch sử cuộc gọi
        String contactName = "";
        Contact contact = contactDao.getContactByPhone(number);
        if (contact != null) {
            contactName = contact.getName();
        }

        CallLog newCall = new CallLog(
                number,
                contactName,
                System.currentTimeMillis(),
                0,
                CallLog.CALL_TYPE_OUTGOING);
        callLogDao.addCallLog(newCall);

        // Kiểm tra quyền CALL_PHONE trước khi thực hiện cuộc gọi
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    1); // requestCode = 1
        } else {
            // Đã có quyền, thực hiện cuộc gọi
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, thực hiện lại cuộc gọi
                makeCall(phoneNumber.toString());
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để thực hiện cuộc gọi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}