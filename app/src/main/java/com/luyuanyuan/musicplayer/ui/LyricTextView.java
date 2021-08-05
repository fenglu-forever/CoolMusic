package com.luyuanyuan.musicplayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Lyric;

public class LyricTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Lyric.Line mLine;
    private String mEmptyText = "";
    private int mPlayingDuration;
    private Path mPath;
    private int mForegroundTextColor;
    private int mBackgroundTextColor;

    public LyricTextView(Context context) {
        this(context, null);
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LyricTextView);
        mEmptyText = typedArray.getString(R.styleable.LyricTextView_emptyText);
        if (mEmptyText == null) {
            mEmptyText = "";
        }
        mForegroundTextColor = typedArray.getColor(R.styleable.LyricTextView_foregroundTextColor, Color.RED);
        mBackgroundTextColor = typedArray.getColor(R.styleable.LyricTextView_backgroundTextColor, Color.BLACK);
        typedArray.recycle();
        mPath = new Path();
        setTextColor(mBackgroundTextColor);
        setText(mEmptyText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Layout layout = getLayout();
        if (layout == null || mLine == null) {
            super.onDraw(canvas);
        } else {
            int lineCount = layout.getLineCount();
            float totalLength = 0;
            for (int i = 0; i < lineCount; i++) {
                totalLength += (layout.getLineRight(i) - layout.getLineLeft(i));
            }
            int lineDuration = mLine.getNextDuration() - mLine.getCurrentDuration();
            if (lineDuration > 0) {
                mPath.reset();
                int deltaDuration = Math.max(mPlayingDuration - mLine.getCurrentDuration(), 0);
                float percent = 1f * deltaDuration / lineDuration;
                if (percent > 1) {
                    percent = 1;
                }
                float percentLength = percent * totalLength;
                for (int i = 0; i < lineCount; i++) {
                    if (percentLength <= 0) {
                        continue;
                    }
                    float lineLeft = layout.getLineLeft(i);
                    float lineTop = layout.getLineTop(i);
                    float lineRight = layout.getLineRight(i);
                    float lineBottom = layout.getLineBottom(i);
                    float lineLength = lineRight - lineLeft;
                    if (percentLength > lineLength) {
                        mPath.addRect(lineLeft, lineTop, lineRight, lineBottom, Path.Direction.CCW);
                        percentLength -= lineLength;
                    } else {
                        mPath.addRect(lineLeft, lineTop, percentLength, lineBottom, Path.Direction.CCW);
                        percentLength = 0;
                    }
                }
                canvas.save();
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
                super.onDraw(canvas);
                canvas.restore();

                canvas.save();
                getPaint().setColor(mForegroundTextColor);
                canvas.clipPath(mPath, Region.Op.INTERSECT);
                layout.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void setLyricLine(Lyric.Line line) {
        if (line == null) {
            mLine = line;
            setText(mEmptyText);
        } else if (!TextUtils.isEmpty(line.getText())) {
            mLine = line;
            setText(line.getText());
        }
    }

    public void setPlayingDuration(int duration) {
        if (duration >= 0 && mPlayingDuration != duration) {
            mPlayingDuration = duration;
            invalidate();
        }
    }

    public void setForegroundTextColor(int color) {
        mForegroundTextColor = color;
        invalidate();
    }

    public void setBackgroundTextColor(int color) {
        mBackgroundTextColor = color;
        setTextColor(mBackgroundTextColor);
    }

    public void setEmptyText(String emptyText) {
        mEmptyText = emptyText == null ? "" : emptyText;
        setLyricLine(mLine);
    }
}