package com.luyuanyuan.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.adapter.MusicManagerAdapter;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicMangerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBack;
    private ImageView ivSelectedAll;
    private FloatingActionButton fabDelete;
    private ListView listView;
    private MusicManagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_manger);
        UiUtil.setStatusBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setNavigationBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setLightStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        UiUtil.setLightNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        initViews();
        initListeners();
        initAdapters();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UiUtil.setLightStatusBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
            UiUtil.setLightNavigationBar(getWindow(), getWindow().getDecorView().getSystemUiVisibility());
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivSelectedAll = findViewById(R.id.ivSelectedAll);
        fabDelete = findViewById(R.id.fabDelete);
        listView = findViewById(R.id.music_list);
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
        ivSelectedAll.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = mAdapter.getItem(position);
                music.setSelected(!music.isSelected());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initAdapters() {
        mAdapter = new MusicManagerAdapter(this, MusicUtil.getCollectMusicList());
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivSelectedAll:
                List<Music> musicList = mAdapter.getMusicList();
                ivSelectedAll.setSelected(!ivSelectedAll.isSelected());
                for (Music music : musicList) {
                    music.setSelected(ivSelectedAll.isSelected());
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.fabDelete:
                List<Music> deleteMusicList = new ArrayList<>();
                List<Music> musicListAll = mAdapter.getMusicList();
                for (Music music : musicListAll) {
                    if (music.isSelected()) {
                        deleteMusicList.add(music);
                    }
                }
                if (deleteMusicList.size() > 0) {
                    MusicUtil.deleteCollectMusic(deleteMusicList);
                    musicListAll.removeAll(deleteMusicList);
                    mAdapter.notifyDataSetChanged();
                    ivSelectedAll.setSelected(false);
                    Intent intent = new Intent(Constant.ACTION_UPDATE_MUSIC_LIST_COLLECT_STATE);
                    sendBroadcast(intent);
                }
                break;
            default:
                break;
        }
    }
}
