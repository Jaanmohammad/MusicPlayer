package com.jksurajpuriya.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jksurajpuriya.musicplayer.databinding.ActivityMusicPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    ActivityMusicPlayerBinding binding;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourceWithMusic();
        binding.title.setSelected(true);
        SeekBaarAndThread();

    }

    private void SeekBaarAndThread() {
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    binding.currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if (mediaPlayer.isPlaying()) {
                        binding.pausePlay.setImageResource(R.drawable.baseline_pause_24);
                        binding.musicIcon.setRotation(x++);
                    } else {
                        binding.pausePlay.setImageResource(R.drawable.baseline_play_arrow_24);
                        binding.musicIcon.setRotation(0);

                    }
                }
                new Handler().postDelayed(this, 100);
            }

        });
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourceWithMusic() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);

        binding.totalTime.setText(convertToMMSS(currentSong.getDuration()));
        binding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.title.setMarqueeRepeatLimit(-1);
        binding.title.setText(currentSong.getTitle());

        binding.pausePlay.setOnClickListener(v -> {
            pausePlay();
        });
        binding.next.setOnClickListener(v -> {
            playNextSong();
        });
        binding.previous.setOnClickListener(v -> {
            playPreviousSong();
        });
        playMusic();
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(mediaPlayer -> playNextSong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}