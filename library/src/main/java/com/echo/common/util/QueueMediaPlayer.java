package com.echo.common.util;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Queue;

/**
 * Created by jiangecho on 16/4/29.
 */
public class QueueMediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private Queue<String> audios;
    private MediaPlayer mediaPlayer;
    private boolean isPaused;

    private boolean isAssetsAudio;
    private AssetManager assetManager;

    /**
     * @param audios audio's full path queue
     */
    public void play(Queue<String> audios) {
        this.audios = audios;
        this.isAssetsAudio = false;

        play();
    }

    public void play(AssetManager assetManager, Queue<String> assetsAudios) {
        this.assetManager = assetManager;
        this.isAssetsAudio = true;
        this.audios = assetsAudios;

        play();
    }

    private void play() {
        if (audios.isEmpty()) {
            return;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        }

        try {
            if (isAssetsAudio) {
                AssetFileDescriptor assetFileDescriptor = assetManager.openFd(audios.poll());
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                assetFileDescriptor.close();
            } else {
                mediaPlayer.setDataSource(audios.poll());
            }
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        play();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void pause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resume() {
        try {
            if (mediaPlayer != null && isPaused) {
                mediaPlayer.start();
                isPaused = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        audios.clear();
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
