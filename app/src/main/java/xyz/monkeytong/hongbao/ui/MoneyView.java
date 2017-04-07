package xyz.monkeytong.hongbao.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import xyz.monkeytong.hongbao.R;

/**
 * 撒钱的试图
 * Created by 玉光 on 2016-8-29.
 */
public class MoneyView extends View {

    private int width;
    private int height;
    private int moneyCationHeight;
    private int moneyCationWidth;
    private int moneyWidth;
    private int moneyHeight;
    private List<Money> moneys;
    private Paint paint;
    private Bitmap moneyCation;
    private Bitmap money;
    private Bitmap money1;
    private Bitmap money2;
    private Rect src;
    private Rect dst;
    private Bitmap moneyCationScale;

    public MoneyView(Context context) {
        super(context);
        //初始化数据
        init();
    }


    public MoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoneyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {

        //获取钱袋的宽高
        moneyCation = BitmapFactory.decodeResource(getResources(), R.drawable.moneycation);
        moneyCationHeight = moneyCation.getHeight();
        moneyCationWidth = moneyCation.getWidth();
        //改变图片的大小
        moneyCationScale = Bitmap.createScaledBitmap(moneyCation, moneyCationWidth / 5, moneyCationHeight / 5, false);
        moneyCation.recycle();
        //获取钱币的宽和高
        money = BitmapFactory.decodeResource(getResources(), R.drawable.money);
        money1 = BitmapFactory.decodeResource(getResources(), R.drawable.money1);
        money2 = BitmapFactory.decodeResource(getResources(), R.drawable.money2);
        moneyWidth = money.getWidth();
        moneyHeight = money.getHeight();

        paint = new Paint();


        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //初始化集合
        moneys = MoneyManager.createMoney(width / 2, height / 2 - moneyCationHeight / 10);


    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画钱袋
        canvas.drawBitmap(moneyCationScale, width / 2 - moneyCationWidth / 10, height / 2 - moneyCationHeight / 10, paint);
        //如果集合里没有内容就不在进行绘制
        if (moneys.size() <= 0) {
            //添加动画结束监听
            if (listener != null) {
                listener.onEnd();
            }
        } else {
            //如果集合里有内容字绘制集合，
            for (int i = 0; i < moneys.size(); i++) {
                moneys.get(i).drawMoney(canvas, money, money1, money2, paint);
            }
            //更新集合里面钱的坐标位置
            moneys = MoneyManager.updateMoneys(moneys, this, width / 2 - moneyWidth / 2, height / 2 - moneyCationHeight / 10 - moneyHeight / 2);
            //延时50毫秒重新绘制
            postInvalidateDelayed(50);
        }
    }

    public void start() {
        MoneyManager.number = 0;
        moneys = MoneyManager.createMoney(width / 2, height / 2 - moneyCationHeight / 10);
        invalidate();
    }

    /**
     * 结束监听的接口
     */
    public interface OnEndListener {
        void onEnd();
    }

    private OnEndListener listener;

    /**
     * 设置结束监听
     *
     * @param listener
     */
    public void SetOnEndListener(OnEndListener listener) {
        this.listener = listener;

    }
}
