package com.luyuanyuan.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.adapter.MusicDetailAdapter;
import com.luyuanyuan.musicplayer.entity.Lyric;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.fragment.LyricFragment;
import com.luyuanyuan.musicplayer.fragment.SongFragment;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.LyricHelper;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBackground;
    private ImageView ivBack;
    private ViewPager mViewPager;
    private RadioGroup mGadioGroup;
    private Music mSelectedMusic;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private BroadcastReceiver mReceiver;
    private LyricHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_music_detail);
        mHelper = new LyricHelper();
        mHelper.setCallback(new LyricHelper.Callback() {
            @Override
            public void onLyricChange(@Nullable Lyric lyric) {
                SongFragment songFragment = (SongFragment) mFragmentList.get(0);
                songFragment.updateLyric(lyric);

                LyricFragment lyricFragment = (LyricFragment) mFragmentList.get(1);
                lyricFragment.updateLyric(lyric);
            }

            @Override
            public void onLineUpdate(@NonNull Lyric.Line line) {
                SongFragment songFragment = (SongFragment) mFragmentList.get(0);
                songFragment.updateLine(line);

                LyricFragment lyricFragment = (LyricFragment) mFragmentList.get(1);
                lyricFragment.updateLine(line);
            }
        });
        mSelectedMusic = (Music) getIntent().getSerializableExtra(Constant.EXTRA_MUSIC);
        initViews();
        initListeners();
        initAdapters();
        bindData();
        UiUtil.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        UiUtil.setNavigationBarColor(getWindow(), Color.TRANSPARENT);
        UiUtil.expandStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        UiUtil.expandNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        mReceiver = new UpdateMusicBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UPDATE_MUSIC);
        filter.addAction(Constant.ACTION_UPDATE_PROGRESS);
        filter.addAction(Constant.ACTION_UPDATE_PLAYING_POSITION);
        filter.addAction(Constant.ACTION_UPDATE_DETAIL_COLLECT_STATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mHelper.stop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UiUtil.expandStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
            UiUtil.expandNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        }
    }

    private void initViews() {
        ivBackground = findViewById(R.id.ivBackground);
        ivBack = findViewById(R.id.ivBack);
        mViewPager = findViewById(R.id.viewPager);
        mGadioGroup = findViewById(R.id.radioGroup);
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
        mGadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtnSong:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rbtnLyric:
                        mViewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mGadioGroup.check(R.id.rbtnSong);
                        break;
                    case 1:
                        mGadioGroup.check(R.id.rbtnLyric);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initAdapters() {
        SongFragment songFragment = new SongFragment();
        songFragment.updateSelectedMusic(mSelectedMusic);
        int progress = getIntent().getIntExtra(Constant.EXTRA_MUSIC_PROGRESS, 0);
        int currentDuration = getIntent().getIntExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, 0);
        songFragment.updateMusicProgress(progress, currentDuration);
        songFragment.updatePlayingPosition(currentDuration);
        mFragmentList.add(songFragment);
        mFragmentList.add(new LyricFragment());
        mViewPager.setAdapter(new MusicDetailAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT,
                mFragmentList));
        if (mSelectedMusic != null) {
            mHelper.setMusic(mSelectedMusic);
            mHelper.start(currentDuration);
            if (!mSelectedMusic.isPlaying()) {
                mHelper.stop();
            }
        }
    }

    private void bindData() {
        if (mSelectedMusic != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(MusicUtil.getAlbumPicUri(mSelectedMusic.getAlbumId()))
                    .placeholder(R.drawable.ic_default_music_album_pic)
                    .error(R.drawable.ic_default_music_album_pic)
                    .into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap blurBitmap = UiUtil.blurBitmap(MusicDetailActivity.this, resource, 25);
                            ivBackground.setImageBitmap(blurBitmap);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            ivBackground.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.muisc_detail_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            default:
                break;
        }
    }

    public void updateLine(int startDuration) {
        mHelper.start(startDuration);
    }

    private class UpdateMusicBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            switch (action) {
                case Constant.ACTION_UPDATE_MUSIC:
                    Music music = (Music) intent.getSerializableExtra(Constant.EXTRA_MUSIC);
                    if (music != null) {
                        if (mSelectedMusic == null || mSelectedMusic.getId() != music.getId()) {
                            mHelper.setMusic(music);
                        }
                        mSelectedMusic = music;
                        if (mSelectedMusic.isPlaying()) {
                            int currentDuration = intent.getIntExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, 0);
                            mHelper.start(currentDuration);
                        } else {
                            mHelper.stop();
                        }
                        bindData();
                        SongFragment songFragment = (SongFragment) mFragmentList.get(0);
                        songFragment.updateSelectedMusic(mSelectedMusic);
                    }
                    break;
                case Constant.ACTION_UPDATE_PROGRESS:
                    int progress = intent.getIntExtra(Constant.EXTRA_MUSIC_PROGRESS, 0);
                    int currentDuration = intent.getIntExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, 0);
                    SongFragment songFrag = (SongFragment) mFragmentList.get(0);
                    songFrag.updateMusicProgress(progress, currentDuration);
                    break;
                case Constant.ACTION_UPDATE_PLAYING_POSITION:
                    int playingPosition = intent.getIntExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, 0);
                    SongFragment sFragment = (SongFragment) mFragmentList.get(0);
                    sFragment.updatePlayingPosition(playingPosition);
                    break;
                case Constant.ACTION_UPDATE_DETAIL_COLLECT_STATE:
                    SongFragment sgFragment = (SongFragment) mFragmentList.get(0);
                    sgFragment.updateCollectIcon();
                    break;
                default:
                    break;
            }
        }
    }
}
