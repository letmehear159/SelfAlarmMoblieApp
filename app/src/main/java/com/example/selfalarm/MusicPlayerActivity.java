package com.example.selfalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    MediaPlayer mediaPlayer;
    int x = 0;
    int resourceId = -1; // ID của file trong raw
    String filePath = null; // Đường dẫn file nhạc (nếu có)

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

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent.hasExtra("RESOURCE_ID")) {
            resourceId = intent.getIntExtra("RESOURCE_ID", -1);
        } else if (intent.hasExtra("FILE_PATH")) {
            filePath = intent.getStringExtra("FILE_PATH");
        }

        setResourcesWithMusic();

        // Cập nhật SeekBar theo thời gian phát nhạc
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    } else {
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    void setResourcesWithMusic() {
        if (resourceId != -1) {
            // Nếu có RESOURCE_ID, phát từ raw
            titleTv.setText(getResources().getResourceEntryName(resourceId));
            playMusic(resourceId);
        } else if (filePath != null) {
            // Nếu có FILE_PATH, phát từ bộ nhớ
            File file = new File(filePath);
            titleTv.setText(file.getName());
            playMusic(filePath);
        }

        pausePlay.setOnClickListener(v -> pausePlay());
    }

    private void playMusic(int resourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, resourceId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            totalTimeTv.setText(convertToMMSS(mediaPlayer.getDuration() + ""));
        }
    }

    private void playMusic(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(filePath));
            mediaPlayer.prepare();
            mediaPlayer.start();

            seekBar.setMax(mediaPlayer.getDuration());
            totalTimeTv.setText(convertToMMSS(mediaPlayer.getDuration() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pausePlay() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
