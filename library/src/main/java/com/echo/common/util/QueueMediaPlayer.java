package com.echo.common.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.Queue;

/**
 * Created by jiangecho on 16/4/29.
 */
public class QueueMediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "QueueMediaPlayer";
    private Queue<String> audios;
    private MediaPlayer mediaPlayer;
    private boolean isPaused;
    private boolean stopWhenCompletion;

    private boolean isAssetsAudio;
    private Context context;

    /**
     * @param audios audio's full path queue
     */
    public void play(Context context, Queue<String> audios) {
        this.audios = audios;
        this.isAssetsAudio = false;
        this.context = context;

        play();
    }

    public void playAsset(Context context, Queue<String> assetsAudios) {
        this.context = context;
        this.isAssetsAudio = true;
        this.audios = assetsAudios;

        play();
    }

    private void play() {
        Log.i(TAG, "play");
        if (audios.isEmpty()) {
            return;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } else {
            mediaPlayer.reset();
        }

        try {
            if (isAssetsAudio) {
                AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(audios.poll());
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
        Log.i(TAG, "onCompletion");
        mp.reset();
        // at few special case, onCompletion called after pause
        // FYI: http://stackoverflow.com/questions/6165507/mediaplayer-completes-after-pausing
        if (!stopWhenCompletion && !isPaused) {
            play();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onError");
        mp.reset();
        // return false leads to onCompletion been called
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared");
        mp.start();
    }

    public void pause() {
        Log.i(TAG, "pause");
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
            } else {
                stopWhenCompletion = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopWhenCompletion = true;
            mediaPlayer.reset();
        }

    }

    public void resume() {
        Log.i(TAG, "resume");
        if (mediaPlayer == null) {
            return;
        }

        try {
            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
            } else {
                stopWhenCompletion = false;
                play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Log.i(TAG, "stop");
        audios.clear();
        isPaused = false;
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
