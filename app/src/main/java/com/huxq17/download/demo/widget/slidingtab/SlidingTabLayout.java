package com.huxq17.download.demo.widget.slidingtab;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.huxq17.download.demo.R;

/**
 * 自定义 tab 布局内容的 viewPager indicator
 * 水平滑动
 *
 * @author huxiaoqian
 */
public class SlidingTabLayout extends HorizontalScrollView {
    private ViewPager mViewPager;
    private final SlidingTabView mTabStrip;
    private int dividerWidth;
    private int animatorDuration = 250;
    private static final int DEFAULT_INDICATOR_HEIGHT_DP = 2;
    private TabAdapter tabAdapter;
    private int currentPosition;
    private int indicatorLeftMargin, indicatorRightMargin;
    private int indicatorWidth, indicatorHeight;
    private int indicatorLeft, indicatorRight;
    private Paint indicatorPaint;
    private boolean showIndicator;
    public final int MODE_FIXED = 0x1;
    public final int MODE_SCROLLABLE = 0x2;
    private int tabMode = MODE_FIXED;

    private Drawable rightDrawable;
    private Rect rightRect = new Rect();


    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingTabLayout);
        indicatorWidth = (int) typedArray.getDimension(R.styleable.SlidingTabLayout_indicatorWidth, 0);
        indicatorHeight = (int) typedArray.getDimension(R.styleable.SlidingTabLayout_indicatorHeight, dp2px(DEFAULT_INDICATOR_HEIGHT_DP));
        indicatorLeftMargin = (int) typedArray.getDimension(R.styleable.SlidingTabLayout_indicatorLeftMargin, 0);
        indicatorRightMargin = (int) typedArray.getDimension(R.styleable.SlidingTabLayout_indicatorRightMargin, 0);
        int indicatorColor = typedArray.getColor(R.styleable.SlidingTabLayout_indicatorColor, 0);
        showIndicator = indicatorColor != 0;
        typedArray.recycle();
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        mTabStrip = new SlidingTabView(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicatorColor);

    }

    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, 0);
    }

    public void setDividerWidth(int width) {
        dividerWidth = width;
    }

    public void setAnimatorDuration(int duration) {
        animatorDuration = duration;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        if (rightDrawable != null) {
            setWillNotDraw(false);
            this.rightDrawable = rightDrawable;
        }
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showIndicator) {
            drawIndicator(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawRight(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        int indicatorBottom = getHeight();
        int indicatorTop = indicatorBottom - indicatorHeight;
        canvas.drawRect(indicatorLeft, indicatorTop, indicatorRight, indicatorBottom, indicatorPaint);
    }

    private void drawRight(Canvas canvas) {
        if (rightDrawable == null) return;
        if (rightDrawable.getBounds().width() == 0 || rightDrawable.getBounds().height() == 0) {
            rightRect.set(getWidth() - rightDrawable.getIntrinsicWidth(), 0, getWidth(), getHeight());
            rightDrawable.setBounds(rightRect);
        }
        canvas.save();
        canvas.translate(getScrollX(), 0);
        rightDrawable.draw(canvas);
        canvas.restore();
    }

    private InternalViewPagerListener viewPagerListener;

    public void setViewPager(ViewPager viewPager, final int selectPosition) {
        mTabStrip.removeAllViews();
        currentPosition = selectPosition;
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPagerListener = new InternalViewPagerListener();
            viewPager.addOnPageChangeListener(viewPagerListener);
            populateTabStrip();
        }
        post(new Runnable() {
            @Override
            public void run() {
                scrollToTab(selectPosition, 0);
            }
        });
    }

    public void setAdapter(TabAdapter tabAdapter) {
        this.tabAdapter = tabAdapter;
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();
        tabAdapter.onAttach(this);
        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = tabAdapter.onCreateView();
            tabView.setOnClickListener(tabClickListener);
            tabAdapter.onBind(tabView, i);
            ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = getLayoutParams();
            }
            MarginLayoutParams tabViewLayoutParams = (MarginLayoutParams) layoutParams;
            if (i != 0) {
                tabViewLayoutParams.leftMargin = dividerWidth;
            }
            mTabStrip.addView(tabView, tabViewLayoutParams);
        }
    }

    public LinearLayout.LayoutParams generateLayoutParams(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    public LinearLayout.LayoutParams generateLayoutParams() {
        return generateLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public CharSequence getTitle(int position) {
        return mViewPager.getAdapter().getPageTitle(position);
    }

    public int getCurrentItem() {
        return currentPosition;
    }

    private void scrollToTab(int tabIndex, float positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        final int paddingLeft = getPaddingLeft();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        View nextChild = mTabStrip.getChildAt(tabIndex + 1);
        int rootWidth = getWidth();
        if (selectedChild != null) {
            int distance = 0;
            int width = selectedChild.getWidth();
            int nextRight = 0;
            int nextLeft = 0;
            if (nextChild != null) {
                distance = nextChild.getLeft() + nextChild.getWidth() / 2 - selectedChild.getLeft() - width / 2;
                nextRight = nextChild.getRight();
                nextLeft = nextChild.getLeft();
                if (indicatorWidth != 0) {
                    int offset = (nextRight - nextLeft - indicatorWidth) / 2;
                    nextLeft = nextLeft + offset;
                    nextRight = nextRight - offset;
                }
            }
            int targetScrollX = selectedChild.getLeft() + paddingLeft;
            float centerOffset = (rootWidth - width) / 2.0f;
            targetScrollX = (int) (targetScrollX - centerOffset + positionOffset * distance);
            scrollTo(targetScrollX, 0);
            if (showIndicator) {
                indicatorLeft = selectedChild.getLeft();
                indicatorRight = selectedChild.getRight();
                if (indicatorWidth != 0) {
                    int offset = (indicatorRight - indicatorLeft - indicatorWidth) / 2;
                    indicatorLeft = indicatorLeft + offset;
                    indicatorRight = indicatorRight - offset;
                }
                if (nextChild != null) {
                    indicatorRight = (int) (indicatorRight + (nextRight - indicatorRight) * positionOffset);
                    indicatorLeft = (int) (indicatorLeft + (nextLeft - indicatorLeft) * positionOffset);
                }
                indicatorRight -= indicatorRightMargin;
                indicatorLeft += indicatorLeftMargin;
                indicatorLeft += paddingLeft;
                indicatorRight += paddingLeft;
                invalidate();
            }
        }
    }


    private void notifyPageChanged(View child, int position, float offset) {
        tabAdapter.onPageChanged(child, position, offset);
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int scrollState = ViewPager.SCROLL_STATE_IDLE;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }
            int childCount = mTabStrip.getChildCount();
            int width = getWidth();
            for (int i = 0; i < childCount; i++) {
                View child = mTabStrip.getChildAt(i);
                int childLeft = child.getLeft() - getScrollX();
                int childWidth = child.getWidth();
                if (childLeft + childWidth > 0 && childLeft < width) {//屏幕内可见的view
                    if (i != position && i != position + 1) {
                        notifyPageChanged(child, i, 0);
                    }
                }
                if (i == position + 1) {
                    notifyPageChanged(child, i, positionOffset);
                }
                if (i == position) {
                    notifyPageChanged(child, position, 1 - positionOffset);
                }
            }
            scrollToTab(position, positionOffset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                scrollToTab(position, 0);
            }
        }

    }


    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            final int position = mTabStrip.indexOfChild(v);
            final int currentPosition = mViewPager.getCurrentItem();
            final View lastSelectedView = mTabStrip.getChildAt(currentPosition);
            if (position != currentPosition) {
                mViewPager.setCurrentItem(position, false);
                if (animatorDuration == 0) {
                    notifyPageChanged(v, position, 1);
                    notifyPageChanged(lastSelectedView, currentPosition, 0);
                } else {
                    ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0, 1);
                    scaleAnimator.setDuration(animatorDuration);
                    scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float fraction = animation.getAnimatedFraction();
                            notifyPageChanged(v, position, fraction);
                            notifyPageChanged(lastSelectedView, currentPosition, 1 - fraction);
                        }
                    });
                    scaleAnimator.start();
                }
            }
        }
    }

    public int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
