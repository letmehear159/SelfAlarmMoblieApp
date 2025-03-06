package com.example.selfalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_app);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);

        try {
            loadMusicFromRaw();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (songsList.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    private void loadMusicFromRaw() throws IOException {
        songsList.add(new AudioModel(R.raw.song1, "Song 1", getDuration(R.raw.song1)));
        songsList.add(new AudioModel(R.raw.song2, "Song 2", getDuration(R.raw.song2)));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null) {
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }
    private String getDuration(int resId) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Resources res = getResources();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);

        retriever.setDataSource(this, uri);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        retriever.release();
        return convertToMMSS(duration);
    }

    private String convertToMMSS(String duration) {
        if (duration == null) return "00:00";
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}





