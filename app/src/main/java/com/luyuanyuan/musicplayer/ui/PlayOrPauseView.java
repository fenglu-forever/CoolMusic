package com.luyuanyuan.musicplayer.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.util.UiUtil;

public class PlayOrPauseView extends View {
    private Bitmap mPlayBitmap;
    private Bitmap mPauseBitmap;
    private Paint mPaint;

    public PlayOrPauseView(Context context) {
        this(context, null);
    }

    public PlayOrPauseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayOrPauseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayOrPauseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Resources resources = getResources();
        mPlayBitmap = UiUtil.drawableToBitmap(resources.getDrawable(R.drawable.ic_music_list_pager_play, null));
        mPauseBitmap = UiUtil.drawableToBitmap(resources.getDrawable(R.drawable.ic_music_list_pager_pause, null));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mPauseBitmap, (getWidth() - mPauseBitmap.getWidth()) / 2f, (getHeight() - mPauseBitmap.getHeight()) / 2f, mPaint);
    }
}
