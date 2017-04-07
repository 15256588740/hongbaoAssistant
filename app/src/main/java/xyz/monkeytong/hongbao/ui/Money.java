package xyz.monkeytong.hongbao.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * 钱的对象
 * Created by 玉光 on 2016-8-29.
 */
public class Money {
    /**
     * 钱币的x坐标
     */
    public float locationX;
    /**
     * 钱币的y坐标
     */
    public float locationY;
    /**
     * 钱币的类型
     */
    public int type;
    /**
     * 移动的距离
     */
    public int distance;
    /**
     * 抛物线的顶点
     */
    public Point fixedPoint;
    /**
     * 确定抛物线的路线的参数，即 y=a*(x-b)^2+c 里面的参数a
     */
    public float a;

    public Money(float x, float y) {
        fixedPoint = new Point();
        this.locationX = x;
        this.locationY = y;
        type = (int) (Math.random() * 3);
        fixedPoint.x =(int) ((Math.random() + 0.5f) * x);
        fixedPoint.y = (int) ((Math.random() + 1) * y / 3);
        distance = (int) ((fixedPoint.x - x) / 5);
        if (distance==0){
            updateDistance();
        }
        //因为 y=a*(x-b)^2+c  ，b就等于抛物线定点x的左边，c就等于抛物线顶点y的坐标，所以a=(y-c)/(x-b）^2
        a = (y - fixedPoint.y) / ((x - fixedPoint.x) * (x - fixedPoint.x));
    }

    /**
     * 更新distance
     */
    private void updateDistance() {
        fixedPoint.x =(int) ( 0.7f * locationX);
        distance = (int) ((fixedPoint.x - locationX) / 5);

    }


    /**
     * 更新图片坐标的方法，每次x坐标移动distance，y经过抛物线算出
     */
    public void updateLocation() {
        locationX += distance;
        locationY = a * (locationX - fixedPoint.x) * (locationX - fixedPoint.x) + fixedPoint.y;
    }

    /**
     * 画钱币的函数
     *
     * @param canvas
     * @param a
     * @param b
     * @param c
     * @param paint
     */
    public void drawMoney(Canvas canvas, Bitmap a, Bitmap b, Bitmap c, Paint paint) {
        switch (type) {
            case 0:
                canvas.drawBitmap(a, locationX, locationY, paint);
                break;
            case 1:
                canvas.drawBitmap(b, locationX, locationY, paint);
                break;
            case 2:
                canvas.drawBitmap(c, locationX, locationY, paint);
                break;
        }

    }

}
