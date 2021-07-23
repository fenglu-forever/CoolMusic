package com.luyuanyuan.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.Constant;

import java.io.IOException;

public class MusicService extends Service {
    private MediaPlayer mPlayer;
    private int mCurrentPosition;
    private Handler mHandler = new Handler();
    private Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            notifyUpdateProgress();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(Constant.ACTION_MUSIC_PLAY_COMPLETE);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayer.stop();
            mPlayer.release();
        }
        mHandler.removeCallbacks(mUpdateProgressTask);
    }

    private void notifyUpdateProgress() {
        Intent intent = new Intent(Constant.ACTION_UPDATE_PROGRESS);
        int progress = 100 * mPlayer.getCurrentPosition() / mPlayer.getDuration();
        intent.putExtra(Constant.EXTRA_MUSIC_PROGRESS, progress);
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public class MusicBinder extends Binder {
        public void requestPlayMusic(Music music) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(music.getUrl());
                mPlayer.prepare();
                mPlayer.seekTo(mCurrentPosition);
                mPlayer.start();
                mHandler.removeCallbacks(mUpdateProgressTask);
                mHandler.postDelayed(mUpdateProgressTask, 1000);
                notifyUpdateProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void requestPauseMusic() {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                mCurrentPosition = mPlayer.getCurrentPosition();
                mHandler.removeCallbacks(mUpdateProgressTask);
                notifyUpdateProgress();
            }
        }

        public void requestSetCurrentPosition(int position) {
            mCurrentPosition = position;
        }
    }
}
