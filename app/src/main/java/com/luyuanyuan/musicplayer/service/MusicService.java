package com.luyuanyuan.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.activity.ForegroundActivity;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;

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

    private Music mPlayingMusic;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayingMusic == null) {
                    return;
                }
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

        mHandler.removeCallbacks(mUpdatePlyingPositionTask);
    }

    private void notifyUpdateProgress() {
        Intent intent = new Intent(Constant.ACTION_UPDATE_PROGRESS);
        int progress = 100 * mPlayer.getCurrentPosition() / mPlayer.getDuration();
        intent.putExtra(Constant.EXTRA_MUSIC_PROGRESS, progress);
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

    private void notifyUpdatePlayingPosition() {
        Intent intent = new Intent(Constant.ACTION_UPDATE_PLAYING_POSITION);
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //如果是低优先级通知发出来无声音
            NotificationChannel channel = new NotificationChannel(MUSIC_CHANNEL, "音乐更新", NotificationManager.IMPORTANCE_MIN);
            manager.createNotificationChannel(channel);
        }
    }

    private void updateMusicNotification(Music music) {
        final RemoteViews rvLarge = new RemoteViews(getPackageName(), R.layout.music_notify_large);
        final RemoteViews rvMin = new RemoteViews(getPackageName(), R.layout.music_notify_min);
        Intent intentLunch = new Intent(this, ForegroundActivity.class);
        intentLunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent lunchPi = PendingIntent.getActivity(this, 0, intentLunch, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(this, MUSIC_CHANNEL)
                .setSmallIcon(R.drawable.ic_default_music_album_pic)
                .setCustomBigContentView(rvLarge)
                .setContentIntent(lunchPi)
                .setContent(rvMin)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build();
        Glide.with(this)
                .asBitmap()
                .error(R.drawable.ic_default_music_album_pic)
                .load(MusicUtil.getAlbumPicUri(music.getAlbumId()))
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        updateNotifyPicture(rvLarge, resource);
                        updateNotifyPicture(rvMin, resource);
                        startForeground(1, notification);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        updateNotifyPicture(rvLarge, R.drawable.ic_default_music_album_pic);
                        updateNotifyPicture(rvMin, R.drawable.ic_default_music_album_pic);
                        startForeground(1, notification);
                    }
                });
        updateNotifyBaseInfo(rvLarge, music);
        updateNotifyBaseInfo(rvMin, music);
        startForeground(1, notification);
    }

    private void updateNotifyBaseInfo(RemoteViews remoteViews, Music music) {
        remoteViews.setTextViewText(R.id.tvName, music.getName());
        remoteViews.setTextViewText(R.id.tvArtist, music.getArtist());
        remoteViews.setImageViewResource(R.id.btnPlayOrPause, music.isPlaying() ? R.drawable.ic_notify_pause : R.drawable.ic_notify_play);
        remoteViews.setImageViewResource(R.id.ivCollect, MusicUtil.isCollect(music) ? R.drawable.ic_notify_collect_selected : R.drawable.ic_notify_collect_normal);
        Intent previousIntent = new Intent(Constant.ACTION_PREVIOUS_MUSIC);
        PendingIntent previousPi = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnPrevious, previousPi);

        Intent nextIntent = new Intent(Constant.ACTION_NEXT_MUSIC);
        PendingIntent nextPi = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnNext, nextPi);

        Intent playOrPauseIntent = new Intent(Constant.ACTION_PLAY_OR_PAUSE_MUSIC);
        PendingIntent playOrPausePi = PendingIntent.getBroadcast(this, 0, playOrPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnPlayOrPause, playOrPausePi);

        Intent cancelMusicNotifyIntent = new Intent(Constant.ACTION_CANCEL_MUSIC_NOTIFICATION);
        PendingIntent cancelMusicNotifyPi = PendingIntent.getBroadcast(this, 0, cancelMusicNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ivClose, cancelMusicNotifyPi);

        Intent musicCollectIntent = new Intent(Constant.ACTION_UPDATE_MUSIC_COLLECT_STATE);
        PendingIntent musicCollectIntentPi = PendingIntent.getBroadcast(this, 0, musicCollectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ivCollect, musicCollectIntentPi);
    }

    private void updateNotifyPicture(RemoteViews remoteViews, Bitmap bitmap) {
        remoteViews.setImageViewBitmap(R.id.ivPic, bitmap);
    }

    private void updateNotifyPicture(RemoteViews remoteViews, int resId) {
        remoteViews.setImageViewResource(R.id.ivPic, resId);
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
                mPlayingMusic = music;
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
                if (mPlayingMusic != null) {
                    updateMusicNotification(mPlayingMusic);
                }
                notifyUpdateProgress();
            }
        }

        public void requestSetCurrentPosition(int position) {
            mCurrentPosition = position;
        }

        public int requestPlayingPosition() {
            return mPlayer.getCurrentPosition();
        }

        public void requestCancelMusicNotifycation() {
            stopForeground(true);
        }

        public void requestUpdateMusicNotifycation(Music music) {
            updateMusicNotification(music);
        }
    }
}
