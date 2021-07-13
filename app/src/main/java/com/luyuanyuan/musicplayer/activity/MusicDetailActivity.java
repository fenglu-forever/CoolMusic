package com.luyuanyuan.musicplayer.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.fragment.LyricFragment;
import com.luyuanyuan.musicplayer.fragment.SongFragment;
import com.luyuanyuan.musicplayer.util.Constant;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_music_detail);
        mSelectedMusic = (Music) getIntent().getSerializableExtra(Constant.EXTRA_MUSIC);
        initViews();
        initListeners();
        initAdapters();
        bindData();
        UiUtil.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        UiUtil.setNavigationBarColor(getWindow(), Color.TRANSPARENT);
        UiUtil.expandStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        UiUtil.expandNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
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
        List<Fragment> fragmentList = new ArrayList<>();
        SongFragment songFragment = new SongFragment();
        songFragment.updateSelectedMusic(mSelectedMusic);
        fragmentList.add(songFragment);
        fragmentList.add(new LyricFragment());
        mViewPager.setAdapter(new MusicDetailAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT,
                fragmentList));
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
}
