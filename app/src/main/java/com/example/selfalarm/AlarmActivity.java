package com.example.selfalarm;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarm.fragment.BottomSheetFragment;
import com.example.selfalarm.fragment.EditDatetimeBottomSheet;

public class AlarmActivity extends AppCompatActivity {
    BottomSheetFragment bottomSheet = new BottomSheetFragment();
    EditDatetimeBottomSheet editBottomSheet=new EditDatetimeBottomSheet();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alarm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showBottomSheet(View view) {
        bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
    }
    public void editDateTime(View view) {
        editBottomSheet.show(getSupportFragmentManager(), "EditDateTimeBottomSheet");
    }
}
