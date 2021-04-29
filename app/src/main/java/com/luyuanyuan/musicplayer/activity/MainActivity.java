package com.luyuanyuan.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.adapter.MusicCategoryAdapter;
import com.luyuanyuan.musicplayer.fragment.AlbumFragment;
import com.luyuanyuan.musicplayer.fragment.CollectFragment;
import com.luyuanyuan.musicplayer.fragment.MusicFragment;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UiUtil.setStatusBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setNavigationBarColor(getWindow(), getColor(R.color.pager_background_color));
        UiUtil.setLightSystemBar(getWindow());
        initViews();
        initListeners();
        initAdapters();
    }

    private void initViews() {
        mViewPager = findViewById(R.id.viewPager);
        mRadioGroup = findViewById(R.id.radioGroup);
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
    }

    private void initAdapters() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MusicFragment());
        fragmentList.add(new AlbumFragment());
        fragmentList.add(new CollectFragment());
        mViewPager.setAdapter(new MusicCategoryAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT, fragmentList));
    }
}
