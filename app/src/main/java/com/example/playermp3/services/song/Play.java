package com.example.playermp3.services.song;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.playermp3.MainActivity;

import java.io.IOException;

public class Play {
    private static MediaPlayer mediaPlayer;
    public static boolean isPlay = false;
    public static int position;

    public static void play(String url, String name) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isPlay = true;
                    MainActivity.nowSong.setText("Bài hát đang phát là: " + name);
                }
            });
            // OnCompletionListener tương tự
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        MainActivity.nowSong.setText("Chưa có bài hát nào đang phát!");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPlay = false;
        }
    }
}
