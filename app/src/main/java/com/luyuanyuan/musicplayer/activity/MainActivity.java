package com.luyuanyuan.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
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
import com.luyuanyuan.musicplayer.ui.PlayOrPauseView;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private ImageView mSelectedMusicImg;
    private TextView mSelectedMusicText;
    private PlayOrPauseView btnPlayOrPause;
    private ImageView btnNext;

    private MediaPlayer mPlayer;
    private Music mSelectedMusic;
    private int mCurrentPosition;
    private ObjectAnimator mRotateAnim;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private Runnable mMusicProgressTak = new Runnable() {
        @Override
        public void run() {
            updateMusicProgress(true);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                requestNextMusic();
            }
        });
        UiUtil.setStatusBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setNavigationBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setLightSystemBar(getWindow());
        initViews();
        initListeners();
        initAdapters();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSelectedMusicText.requestFocus();
            UiUtil.setLightSystemBar(getWindow());
        }
    }

    private void initViews() {
        mViewPager = findViewById(R.id.viewPager);
        mRadioGroup = findViewById(R.id.radioGroup);
        mSelectedMusicImg = findViewById(R.id.selectedMusicImg);
        mRotateAnim = ObjectAnimator.ofFloat(mSelectedMusicImg, "rotation", 0, 360);
        mRotateAnim.setDuration(24 * 1000);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        UiUtil.roundView(mSelectedMusicImg, getResources().getDimension(R.dimen.selected_music_pic_conner));
        mSelectedMusicText = findViewById(R.id.selectedMusicText);
        btnPlayOrPause = findViewById(R.id.btnPlayOrPause);
        btnNext = findViewById(R.id.btnNext);
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
    }

    private void initAdapters() {
        mFragmentList.add(new MusicFragment());
        mFragmentList.add(new AlbumFragment());
        mFragmentList.add(new CollectFragment());
        mViewPager.setAdapter(new MusicCategoryAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT, mFragmentList));
    }

    public void requestPlayMusic(Music music) {
        if (mSelectedMusic != music) {
            mRotateAnim.cancel();
            mCurrentPosition = 0;
        } else {
            if (mSelectedMusic.isPlaying()) {
                return;
            }
        }
        // 1.让 MediaPlayer执行
        playMusic(music);
        // 2.把选中歌曲的信息同步到底部栏
        onMusicSelected(music);
        //3.把播放状态同步到底部栏
        onPlayStateChanged(true, music);
    }

    public void requestPauseMusic(Music music) {
        // 1.让MediaPlayer执行暂停
        pauseMusic();
        // 2.把歌曲的播放状态同步更新底部栏
        onPlayStateChanged(false, music);
    }

    private void requestNextMusic() {
        Music nextMusic = mFragmentList.get(mViewPager.getCurrentItem()).getNextMusic();
        if (nextMusic != null) {
            requestPlayMusic(nextMusic);
        }
    }

    private void playMusic(Music music) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getUrl());
            mPlayer.prepare();
            mPlayer.seekTo(mCurrentPosition);
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mCurrentPosition = mPlayer.getCurrentPosition();
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
        updateMusicProgress(isPlaying);
    }

    private void updateMusicProgress(boolean isPlaying) {
        int progress = 100 * mPlayer.getCurrentPosition() / mPlayer.getDuration();
        btnPlayOrPause.setProgress(progress);
        if (isPlaying) {
            btnPlayOrPause.removeCallbacks(mMusicProgressTak);
            btnPlayOrPause.postDelayed(mMusicProgressTak, 1000);
        } else {
            btnPlayOrPause.removeCallbacks(mMusicProgressTak);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayOrPause:
                if (mSelectedMusic != null) {
                    if (mSelectedMusic.isPlaying()) {
                        requestPauseMusic(mSelectedMusic);
                    } else {
                        requestPlayMusic(mSelectedMusic);
                    }
                }
                break;
            case R.id.btnNext:
                requestNextMusic();
                break;
            default:
                break;
        }
    }
}
