package com.luyuanyuan.musicplayer.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private float mBgStroke;
    private int mBgStrokeColor;
    private boolean isPlay;
    private int mProgress;

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
        mPaint.setStyle(Paint.Style.STROKE);
        mBgStroke = resources.getDimension(R.dimen.play_or_pause_circle_stroke);
        mPaint.setStrokeWidth(mBgStroke);
        mBgStrokeColor = Color.parseColor("#CCCCCC");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float halfStroke = mBgStroke / 2f;
        canvas.translate(cx, cy);
        mPaint.setColor(mBgStrokeColor);
        canvas.drawCircle(0, 0, cx - halfStroke, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(-cx + halfStroke,
                -cy + halfStroke,
                cx - halfStroke,
                cy - halfStroke,
                270,
                mProgress / 100f * 360, false, mPaint);
        if (isPlay) {
            canvas.drawBitmap(mPlayBitmap, -mPlayBitmap.getWidth() / 2f, -mPlayBitmap.getHeight() / 2f, mPaint);
        } else {
            canvas.drawBitmap(mPauseBitmap, -mPauseBitmap.getWidth() / 2f, -mPauseBitmap.getHeight() / 2f, mPaint);
        }
        canvas.restore();
    }

    public void setPlay(boolean play) {
        isPlay = play;
        invalidate();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }
}
