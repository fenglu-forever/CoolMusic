package com.luyuanyuan.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.adapter.MusicCategoryAdapter;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.fragment.AlbumFragment;
import com.luyuanyuan.musicplayer.fragment.BaseFragment;
import com.luyuanyuan.musicplayer.fragment.CollectFragment;
import com.luyuanyuan.musicplayer.fragment.MusicFragment;
import com.luyuanyuan.musicplayer.service.MusicService;
import com.luyuanyuan.musicplayer.ui.PlayOrPauseView;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.PreferenceUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private ImageView mSelectedMusicImg;
    private TextView mSelectedMusicText;
    private PlayOrPauseView btnPlayOrPause;
    private ImageView btnNext;
    private ImageView ivBack;

    private Music mSelectedMusic;
    private ObjectAnimator mRotateAnim;
    private List<BaseFragment> mFragmentList = new ArrayList<>();


    private MusicService.MusicBinder mMusicServer;
    private MusicBroadcastReceiver mMusicReceiver;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicServer = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UiUtil.setStatusBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setNavigationBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setLightStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        UiUtil.setLightNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        initViews();
        initListeners();
        initAdapters();
        bindMusicService();
        registerMusicBroadcast();
    }

    private void registerMusicBroadcast() {
        mMusicReceiver = new MusicBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_NEXT_MUSIC);
        filter.addAction(Constant.ACTION_PREVIOUS_MUSIC);
        filter.addAction(Constant.ACTION_UPDATE_PROGRESS);
        filter.addAction(Constant.ACTION_PLAY_MUSIC);
        filter.addAction(Constant.ACTION_PAUSE_MUSIC);
        filter.addAction(Constant.ACTION_SEEK_MUSIC);
        filter.addAction(Constant.ACTION_MUSIC_PLAY_COMPLETE);
        registerReceiver(mMusicReceiver, filter);
    }

    private void unregisterMusicBroadcast() {
        unregisterReceiver(mMusicReceiver);
    }

    private void bindMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService() {
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRotateAnim.cancel();
        unbindMusicService();
        unregisterMusicBroadcast();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSelectedMusicText.requestFocus();
            UiUtil.setLightStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
            UiUtil.setLightNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        }
    }

    private void initViews() {
        mViewPager = findViewById(R.id.viewPager);
        mRadioGroup = findViewById(R.id.radioGroup);
        mSelectedMusicImg = findViewById(R.id.selectedMusicImg);
        mRotateAnim = ObjectAnimator.ofFloat(mSelectedMusicImg, "rotation", 0, 360);
        mRotateAnim.setDuration(24 * 1000);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        UiUtil.roundView(mSelectedMusicImg, getResources().getDimension(R.dimen.selected_music_pic_conner));
        mSelectedMusicText = findViewById(R.id.selectedMusicText);
        btnPlayOrPause = findViewById(R.id.btnPlayOrPause);
        btnNext = findViewById(R.id.btnNext);
        ivBack = findViewById(R.id.ivBack);
    }

    private void initListeners() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.rbtnMusic);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.rbtnAlbum);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.rbtnCollect);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtnMusic:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rbtnAlbum:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rbtnCollect:
                        mViewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
            }
        });
        btnPlayOrPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        mSelectedMusicImg.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void initAdapters() {
        mFragmentList.add(new MusicFragment());
        mFragmentList.add(new AlbumFragment());
        mFragmentList.add(new CollectFragment());
        mViewPager.setAdapter(new MusicCategoryAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT, mFragmentList));
    }

    public void playMusic(Music music) {
        if (mSelectedMusic == null || mSelectedMusic.getId() != music.getId()) {
            mRotateAnim.cancel();
            if (mMusicServer != null) {
                mMusicServer.requestSetCurrentPosition(0);
            }
        } else {
            if (mSelectedMusic.isPlaying()) {
                return;
            }
        }
        // 1.让 MediaPlayer执行
        if (mMusicServer != null) {
            mMusicServer.requestPlayMusic(music);
        }
        // 2.把选中歌曲的信息同步到底部栏
        onMusicSelected(music);
        //3.把播放状态同步到底部栏
        onPlayStateChanged(true, music);
    }

    private void seekMusic(int seekPositionDuration) {
        if (mMusicServer != null) {
            mMusicServer.requestSetCurrentPosition(seekPositionDuration);
        }
        // 1.让 MediaPlayer执行
        if (mMusicServer != null) {
            mMusicServer.requestPlayMusic(mSelectedMusic);
        }
        // 2.把选中歌曲的信息同步到底部栏
        onMusicSelected(mSelectedMusic);
        //3.把播放状态同步到底部栏
        onPlayStateChanged(true, mSelectedMusic);
    }

    private void onMusicPlayCompleted() {
        int playMode = PreferenceUtil.getInt(Constant.PREF_KEY_PLAY_MODE, Constant.PLAY_MODE_SEQUENCE);
        switch (playMode) {
            case Constant.PLAY_MODE_SEQUENCE:
                nextMusic();
                break;
            case Constant.PLAY_MODE_SINGLE:
                if (mSelectedMusic != null) {
                    seekMusic(0);
                }
                break;
            case Constant.PLAY_MODE_RANDOM:
                randomMusic();
                break;
            default:
                break;
        }
    }

    public void pauseMusic(Music music) {
        // 1.让MediaPlayer执行暂停
        if (mMusicServer != null) {
            mMusicServer.requestPauseMusic();
        }
        // 2.把歌曲的播放状态同步更新底部栏
        onPlayStateChanged(false, music);
    }

    private void nextMusic() {
        Music nextMusic = mFragmentList.get(mViewPager.getCurrentItem()).getNextMusic();
        if (nextMusic != null) {
            playMusic(nextMusic);
        }
    }

    private void previousMusic() {
        Music previousMusic = mFragmentList.get(mViewPager.getCurrentItem()).getPreviousMusic();
        if (previousMusic != null) {
            playMusic(previousMusic);
        }
    }

    private void randomMusic() {
        Music randomMusic = mFragmentList.get(mViewPager.getCurrentItem()).getRandomMusic();
        if (randomMusic != null) {
            mSelectedMusic = randomMusic;
            seekMusic(0);
        }
    }

    private void onMusicSelected(Music music) {
        mSelectedMusic = music;
        Glide.with(this)
                .load(MusicUtil.getAlbumPicUri(music.getAlbumId()))
                .placeholder(R.drawable.ic_default_music_album_pic)
                .error(R.drawable.ic_default_music_album_pic)
                .into(mSelectedMusicImg);
        mSelectedMusicText.setText(music.getName() + " - " + music.getArtist());
    }

    private void onPlayStateChanged(boolean isPlaying, Music music) {
        music.setPlaying(isPlaying);
        if (isPlaying) {
            btnPlayOrPause.setPlay(true);
            if (!mRotateAnim.isStarted()) {
                mRotateAnim.start();
            } else {
                mRotateAnim.resume();
            }
        } else {
            btnPlayOrPause.setPlay(false);
            mRotateAnim.pause();
        }
        Intent intent = new Intent(Constant.ACTION_UPDATE_MUSIC);
        int currentDuration = 0;
        if (mMusicServer != null) {
            currentDuration = mMusicServer.requestPlayingPosition();
        }
        intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, currentDuration);
        intent.putExtra(Constant.EXTRA_MUSIC, music);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayOrPause:
                if (mSelectedMusic != null) {
                    if (mSelectedMusic.isPlaying()) {
                        pauseMusic(mSelectedMusic);
                    } else {
                        playMusic(mSelectedMusic);
                    }
                }
                break;
            case R.id.btnNext:
                nextMusic();
                break;
            case R.id.selectedMusicImg:
                Intent intent = new Intent(this, MusicDetailActivity.class);
                int currentDuration = 0;
                if (mMusicServer != null) {
                    currentDuration = mMusicServer.requestPlayingPosition();
                }
                intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, currentDuration);
                intent.putExtra(Constant.EXTRA_MUSIC_PROGRESS, btnPlayOrPause.getProgress());
                intent.putExtra(Constant.EXTRA_MUSIC, mSelectedMusic);
                startActivity(intent);
                overridePendingTransition(R.anim.muisc_detail_enter, 0);
                break;
            case R.id.ivBack:
                finish();
                break;
            default:
                break;
        }
    }

    public Music getSelectedMusic() {
        return mSelectedMusic;
    }

    private class MusicBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                switch (action) {
                    case Constant.ACTION_NEXT_MUSIC:
                        nextMusic();
                        break;
                    case Constant.ACTION_PREVIOUS_MUSIC:
                        previousMusic();
                        break;
                    case Constant.ACTION_UPDATE_PROGRESS:
                        int progress = intent.getIntExtra(Constant.EXTRA_MUSIC_PROGRESS, 0);
                        btnPlayOrPause.setProgress(progress);
                        break;
                    case Constant.ACTION_PLAY_MUSIC:
                        playMusic(mSelectedMusic);
                        break;
                    case Constant.ACTION_PAUSE_MUSIC:
                        pauseMusic(mSelectedMusic);
                        break;
                    case Constant.ACTION_SEEK_MUSIC:
                        int seekDuration = intent.getIntExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, 0);
                        if (mSelectedMusic != null) {
                            seekMusic(seekDuration);
                        }
                        break;
                    case Constant.ACTION_MUSIC_PLAY_COMPLETE:
                        onMusicPlayCompleted();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
