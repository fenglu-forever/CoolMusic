package com.luyuanyuan.musicplayer.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

public class MusicDetailActivity extends AppCompatActivity {
    private ImageView ivBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_music_detail);
        initViews();
        initData();
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
    }

    private void initData() {
        Music music = (Music) getIntent().getSerializableExtra(Constant.EXTRA_MUSIC);
        if (music != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(MusicUtil.getAlbumPicUri(music.getAlbumId()))
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
}
