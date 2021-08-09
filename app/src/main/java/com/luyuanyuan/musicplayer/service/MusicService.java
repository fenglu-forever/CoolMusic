package com.luyuanyuan.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.BroadcastUtil;
import com.luyuanyuan.musicplayer.util.Constant;

import java.io.IOException;

public class MusicService extends Service {
    private static final int DELAY_TIME_UPDATE_PROGRESS = 1000;
    private static final int DELAY_TIME_UPDATE_PLAYING_POSITION = 60;
    private static final String MUSIC_CHANNEL = "music_channel";

    private MediaPlayer mPlayer;
    private int mCurrentPosition;
    private Handler mHandler = new Handler();
    private Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, DELAY_TIME_UPDATE_PROGRESS);
            notifyUpdateProgress();
        }
    };

    private Runnable mUpdatePlyingPositionTask = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, DELAY_TIME_UPDATE_PLAYING_POSITION);
            notifyUpdatePlayingPosition();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(Constant.ACTION_MUSIC_PLAY_COMPLETE);
                BroadcastUtil.postBroadcast(intent);
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

        mHandler.removeCallbacks(mUpdatePlyingPositionTask);
    }

    private void notifyUpdateProgress() {
        Intent intent = new Intent(Constant.ACTION_UPDATE_PROGRESS);
        int progress = 100 * mPlayer.getCurrentPosition() / mPlayer.getDuration();
        intent.putExtra(Constant.EXTRA_MUSIC_PROGRESS, progress);
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mPlayer.getCurrentPosition());
        BroadcastUtil.postBroadcast(intent);
    }

    private void notifyUpdatePlayingPosition() {
        Intent intent = new Intent(Constant.ACTION_UPDATE_PLAYING_POSITION);
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mPlayer.getCurrentPosition());
        BroadcastUtil.postBroadcast(intent);
    }

    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MUSIC_CHANNEL, "音乐更新", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    private void updateMusicNotification(Music music) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.music_notify);
        remoteViews.setTextViewText(R.id.tvName, music.getName());
        Notification notification = new NotificationCompat.Builder(this, MUSIC_CHANNEL)
                .setSmallIcon(R.drawable.ic_default_music_album_pic)
                .setCustomBigContentView(remoteViews)
                .build();
        startForeground(1, notification);
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
                mHandler.postDelayed(mUpdateProgressTask, DELAY_TIME_UPDATE_PROGRESS);
                notifyUpdateProgress();

                mHandler.removeCallbacks(mUpdatePlyingPositionTask);
                mHandler.postDelayed(mUpdatePlyingPositionTask, DELAY_TIME_UPDATE_PLAYING_POSITION);
                notifyUpdatePlayingPosition();

                updateMusicNotification(music);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void requestPauseMusic() {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                mCurrentPosition = mPlayer.getCurrentPosition();

                mHandler.removeCallbacks(mUpdateProgressTask);

                mHandler.removeCallbacks(mUpdatePlyingPositionTask);
                notifyUpdateProgress();
            }
        }

        public void requestSetCurrentPosition(int position) {
            mCurrentPosition = position;
        }

        public int requestPlayingPosition() {
            return mPlayer.getCurrentPosition();
        }
    }
}
