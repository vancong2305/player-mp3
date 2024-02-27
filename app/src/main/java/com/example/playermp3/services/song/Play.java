package com.example.playermp3.services.song;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class Play {
    private static MediaPlayer mediaPlayer;
    public static boolean isPlay = false;

    public static void play(String url) {
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
                }
            });
            // OnCompletionListener tương tự
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
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
