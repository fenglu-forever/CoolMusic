package com.luyuanyuan.musicplayer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatRadioButton;

public class IndicatorRadioButton extends AppCompatRadioButton {
    private TextPaint mTextPaint;
    private Paint mPaint;
    private int mBgXPadding;
    private int mBgYPadding;

    public IndicatorRadioButton(Context context) {
        super(context);
        init();
    }

    public IndicatorRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#263FC89C"));
        mBgXPadding = dpToPx(16);
        mBgYPadding = dpToPx(4);
        mTextPaint.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChecked()) {
            String text = "";
            if (getText() != null) {
                text = getText().toString();
            }
            float cx = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
            float cy = getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) / 2f;
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textW = mTextPaint.measureText(text);
            float textH = fontMetrics.bottom - fontMetrics.top;
            float halfBgW = textW / 2f + mBgXPadding;
            float halfBgH = textH / 2f + mBgYPadding;
            canvas.drawRoundRect(cx - halfBgW,
                    cy - halfBgH,
                    cx + halfBgW,
                    cy + halfBgH, halfBgH, halfBgH, mPaint);
        }
    }

    private int dpToPx(float dpValue) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics()));
    }
}
