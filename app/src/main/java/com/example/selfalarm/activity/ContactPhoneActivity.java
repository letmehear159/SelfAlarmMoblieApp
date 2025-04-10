package com.example.selfalarm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.TabAdapter;
import com.example.selfalarm.fragment.ContactsFragment;
import com.example.selfalarm.fragment.HistoryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class ContactPhoneActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_phone);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup ViewPager và TabLayout
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        setupViewPager();

        // FAB action để thêm liên hệ mới hoặc quay số
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nếu đang ở tab danh bạ thì mở màn hình thêm liên hệ,
                // nếu ở tab lịch sử cuộc gọi thì mở màn hình quay số
                if (viewPager.getCurrentItem() == 0) {
                    // Mở màn hình thêm liên hệ mới
                    Intent intent = new Intent(ContactPhoneActivity.this, ContactDetailActivity.class);
                    startActivity(intent);
                } else {
                    // Mở màn hình quay số
                    Intent intent = new Intent(ContactPhoneActivity.this, PhoneCallActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupViewPager() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactsFragment(), "Danh bạ");
        adapter.addFragment(new HistoryFragment(), "Lịch sử");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Cập nhật lại icon FAB khi chuyển tab
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    // Tab danh bạ: hiển thị icon thêm liên hệ
                    fab.setImageResource(android.R.drawable.ic_input_add);
                } else {
                    // Tab lịch sử: hiển thị icon bàn phím quay số
                    fab.setImageResource(android.R.drawable.ic_menu_call);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_dial) {
            // Mở màn hình quay số
            Intent intent = new Intent(this, PhoneCallActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}