package xyz.monkeytong.hongbao.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by wsong on 2016/12/28.
 */

public class SlideMenu extends FrameLayout {

    private View mainView;
    private View menuView;
    private ViewDragHelper mDragHelper;
    private int mWidth;
    private int dragRange;
    private FloatEvaluator floatEvaluator;
    private IntEvaluator intEvaluator;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mDragHelper = ViewDragHelper.create(this, mCallback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //异常处理
        if (getChildCount() > 2) {
            throw new IllegalArgumentException("SlideMenu only have 2 children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        dragRange = (int) (mWidth * 0.6);
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        /**
         * 获取拖拽水平距离 ,不能限定边界,返回值目前用在手指抬起的时候view缓慢移动的时候，
         * 动画时间计算上面 一般不介意返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragRange;
        }

        /**
         * 控制子控件水平方向移动 left=child.getleft()+dx
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0)
                    left = 0;
                if (left > dragRange) left = dragRange;
            }
            return left;
        }

        /**
         * 当child位置改变时候执行 ,一般用来做其他子View的伴随移动
         *changedView:位置改变的child
         *left:child最新的top dx:本次水平移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > dragRange) newLeft = dragRange;
                mainView.layout(newLeft, mainView.getTop() + dy,
                        newLeft + mainView.getMeasuredWidth(), mainView.getBottom() + dy);
            }
            // 计算view移动的百分比
            float fraction = mainView.getLeft() / (float)dragRange;
            // 执行伴随动画
            executeAnim(fraction);
        }

        /**
         * 手指抬起时执行 xvel/yvel：速度 正->右 负->左
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < dragRange / 2) {
                //左半边
                mDragHelper.smoothSlideViewTo(mainView, 0, 0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            } else {
                //右半边
                mDragHelper.smoothSlideViewTo(mainView, dragRange, 0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }
        }
    };

    private void executeAnim(float fraction) {
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
        //移动menuView
        ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        //放大menuView
        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction,0.5f,1f));
        //改变menuView的透明度
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction,0.3f,1f));
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }
}
