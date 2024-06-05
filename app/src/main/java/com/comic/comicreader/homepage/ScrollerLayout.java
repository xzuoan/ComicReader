package com.comic.comicreader.homepage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollerLayout extends LinearLayout {

    private final Scroller mScroller;
    int mStartEventX;
    int mEndEventX;
    int mFirstEventX;

    public ScrollerLayout(Context context) {
        this(context,null);
    }

    public ScrollerLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    /**
     * 开始滑动
     * @param distance 滑动的距离（注意这里不是坐标）
     */
    public void startScroll(int distance, int duration){
        mScroller.startScroll(0,0,distance,0, duration);
    }

    /**
     * 恢复初态
     */
    public void reset(){
        mScroller.startScroll(getScrollX(),0,-getScrollX(),0, 200);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
        }
        invalidate();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstEventX = mStartEventX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mEndEventX = (int) event.getRawX();
                int moveSpace = mEndEventX - mStartEventX;
                if (moveSpace < -10) {
                    startScroll(-(moveSpace)*5, 0);
                }
                mStartEventX = mEndEventX;
                break;
            case MotionEvent.ACTION_UP:
                mEndEventX = (int) event.getRawX();
                if (Math.abs(mStartEventX - mEndEventX) < 200) {
                    reset();
                }
                break;
        }
        return true;
    }
}
