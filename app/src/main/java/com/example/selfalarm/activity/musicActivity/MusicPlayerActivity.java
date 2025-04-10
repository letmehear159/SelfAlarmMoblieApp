package com.example.selfalarm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Notification;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

import com.example.selfalarm.activity.musicActivity.AudioModel;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    int x = 0;
    int resourceId = -1;
    String filePath = null;
    MusicService musicService;
    boolean serviceBound = false;
    private static final String CHANNEL_ID = "MusicChannel";
    private static final int NOTIFICATION_ID = 2;
    private ArrayList<AudioModel> songsList;
    private int currentPosition;
    private Handler handler = new Handler(); // Handler để cập nhật SeekBar

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            serviceBound = true;
            setupUI();
            if (musicService.isPlaying()) {
                showMusicPlayingNotification();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        titleTv.setSelected(true);

        Intent intent = getIntent();
        if (intent.hasExtra("RESOURCE_ID")) {
            resourceId = intent.getIntExtra("RESOURCE_ID", -1);
        } else if (intent.hasExtra("FILE_PATH")) {
            filePath = intent.getStringExtra("FILE_PATH");
        }
        songsList = (ArrayList<AudioModel>) intent.getSerializableExtra("SONGS_LIST");
        currentPosition = intent.getIntExtra("POSITION", 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("RESOURCE_ID", resourceId);
        serviceIntent.putExtra("FILE_PATH", filePath);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        pausePlay.setOnClickListener(v -> {
            if (serviceBound) {
                musicService.pausePlay();
                if (musicService.isPlaying()) {
                    showMusicPlayingNotification();
                }
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (serviceBound && songsList != null && !songsList.isEmpty()) {
                currentPosition = (currentPosition + 1) % songsList.size();
                playSongAtPosition(currentPosition);
            }
        });

        previousBtn.setOnClickListener(v -> {
            if (serviceBound && songsList != null && !songsList.isEmpty()) {
                currentPosition = (currentPosition - 1 < 0) ? songsList.size() - 1 : currentPosition - 1;
                playSongAtPosition(currentPosition);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (serviceBound && fromUser) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSongAtPosition(int position) {
        AudioModel song = songsList.get(position);
        resourceId = song.getResourceId();
        filePath = null;
        if (serviceBound) {
            musicService.playNewSong(resourceId, filePath); // Gọi hàm mới trong MusicService
            setupUI();
            showMusicPlayingNotification();
        }
    }

    private void setupUI() {
        if (resourceId != -1) {
            titleTv.setText(getResources().getResourceEntryName(resourceId));
        } else if (filePath != null) {
            titleTv.setText(new java.io.File(filePath).getName());
        }

        if (serviceBound) {
            seekBar.setMax(musicService.getDuration());
            totalTimeTv.setText(convertToMMSS(musicService.getDuration() + ""));
            updateSeekBar(); // Bắt đầu cập nhật SeekBar
        }
    }

    private void updateSeekBar() {
        if (serviceBound) {
            seekBar.setProgress(musicService.getCurrentPosition());
            currentTimeTv.setText(convertToMMSS(musicService.getCurrentPosition() + ""));

            if (musicService.isPlaying()) {
                pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                musicIcon.setRotation(x++);
            } else {
                pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                musicIcon.setRotation(0);
            }
        }
        handler.postDelayed(this::updateSeekBar, 100); // Cập nhật mỗi 100ms
    }

    private void showMusicPlayingNotification() {
        String title = (filePath != null) ? new java.io.File(filePath).getName() :
                (resourceId != -1 ? getResources().getResourceEntryName(resourceId) : "Unknown Track");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Playing")
                .setContentText("Now playing: " + title)
                .setSmallIcon(R.drawable.music_icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis) % java.util.concurrent.TimeUnit.HOURS.toMinutes(1),
                java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis) % java.util.concurrent.TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Dừng cập nhật SeekBar
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}