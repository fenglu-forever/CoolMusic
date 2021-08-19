package com.luyuanyuan.musicplayer.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Lyric;

import java.util.ArrayList;
import java.util.List;

public class LyricListView extends ListView {
    private static final int ANIM_DURATION = 180;

    private List<Lyric.Line> mLineList = new ArrayList<>();
    private LayoutInflater mInflater;
    private LyricAdapter mAdapter;

    private int mSelectedLinePosition;

    private float mSelectedTextSize;
    private int mSelectedTextColor;

    private float mNormalTextSize;
    private int mNormalTextColor;

    private View mFooterView;
    private AnimatorSet mAnim;
    private boolean isTouch;

    public LyricListView(Context context) {
        this(context, null);
    }

    public LyricListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LyricListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);

        mAdapter = new LyricAdapter();
        setAdapter(mAdapter);

        mSelectedTextSize = spToPx(22);
        mSelectedTextColor = Color.parseColor("#EAFFFFFF");
        mNormalTextSize = spToPx(15);
        mNormalTextColor = Color.parseColor("#66FFFFFF");
        addHeaderView(generateHeadView());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mFooterView == null) {
            mFooterView = generateFooterView(h / 2);
            addFooterView(mFooterView);
        } else {
            AbsListView.LayoutParams lpFooter = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, h / 2);
            mFooterView.setLayoutParams(lpFooter);
        }
    }

    private View generateHeadView() {
        View headView = mInflater.inflate(R.layout.item_lyric_line, this, false);
        TextView headText = headView.findViewById(R.id.tvLine);
        headText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        headView.setVisibility(View.INVISIBLE);
        return headView;
    }

    private View generateFooterView(int footerHeight) {
        View view = new View(getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
        return view;
    }

    public void setLineList(List<Lyric.Line> lineList) {
        mSelectedLinePosition = 0;
        mLineList.clear();
        mLineList.addAll(lineList);
        mAdapter.notifyDataSetChanged();
    }

    public void setSelectedLine(int selLinePos) {
        if (mSelectedLinePosition != selLinePos) {
            int oldSelPos = mSelectedLinePosition;
            mSelectedLinePosition = selLinePos;
            startSelectedAnim(oldSelPos, selLinePos);
            if (!isTouch) {
                smoothScrollToPositionFromTop(selLinePos, 0, ANIM_DURATION);
            }
        }
    }

    private void startSelectedAnim(final int oldSelPos, final int newSelPos) {
        // As add a list head view, so fist visible child position need move one.
        int firstVisPos = getFirstVisiblePosition() - 1;
        final View oldSelChild = getChildAt(oldSelPos - firstVisPos);
        final View newSelChild = getChildAt(newSelPos - firstVisPos);
        if (oldSelChild != null && newSelChild != null) {
            if (mAnim != null) {
                mAnim.cancel();
            }
            oldSelChild.setSelected(false);
            newSelChild.setSelected(true);
            final TextView oldLine = oldSelChild.findViewById(R.id.tvLine);
            oldLine.setTextColor(mNormalTextColor);
            final TextView newLine = newSelChild.findViewById(R.id.tvLine);
            newLine.setTextColor(mSelectedTextColor);

            mAnim = new AnimatorSet();
            mAnim.setDuration(ANIM_DURATION);
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    oldLine.setTag(oldSelPos);
                    newLine.setTag(newSelPos);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    oldLine.setTag(null);
                    newLine.setTag(null);

                    oldLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
                    newLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);

                }
            });
            ValueAnimator oldSelAnim = ValueAnimator.ofFloat(mSelectedTextSize, mNormalTextSize);
            oldSelAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    oldLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) animation.getAnimatedValue());
                }
            });
            ValueAnimator newSelAnim = ValueAnimator.ofFloat(mNormalTextSize, mSelectedTextSize);
            newSelAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    newLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) animation.getAnimatedValue());
                }
            });
            mAnim.playTogether(oldSelAnim, newSelAnim);
            mAnim.start();
        } else {
            if (mAnim != null && mAnim.isStarted()) {
                mAnim.cancel();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        isTouch = action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_CANCEL;
        return super.onTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof LyricAdapter) {
            super.setAdapter(adapter);
        }
    }

    private int dpToPx(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int spToPx(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    private class LyricAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLineList.size();
        }

        @Override
        public Lyric.Line getItem(int position) {
            return mLineList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            final Lyric.Line line = mLineList.get(position);
            Integer doAnimPos = -1;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
                if (holder.tvLine.getTag() instanceof Integer) {
                    doAnimPos = (Integer) holder.tvLine.getTag();
                }
            }
            if (convertView == null || doAnimPos != -1) {
                view = mInflater.inflate(R.layout.item_lyric_line, parent, false);
                holder = new ViewHolder();
                holder.tvLine = view.findViewById(R.id.tvLine);
                holder.tvLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
                holder.tvLine.setTag(null);// disable xml set text view tag.
                view.setSelected(false);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            if (position == mSelectedLinePosition) {
                holder.tvLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
                holder.tvLine.setTextColor(mSelectedTextColor);
                view.setSelected(true);
            } else {
                holder.tvLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
                holder.tvLine.setTextColor(mNormalTextColor);
                view.setSelected(false);
            }
            holder.tvLine.setText(line.getText());
            return view;
        }
    }

    private static class ViewHolder {
        private TextView tvLine;
    }
}