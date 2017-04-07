package xyz.monkeytong.hongbao.ui;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱币的管理类
 * Created by 玉光 on 2016-8-29.
 */
public class MoneyManager {
    /**
     * 钱币的管理集合
     */
    public static List<Money> moneys ;
    public static int number = 0;

    /**
     * 产生储存十个钱对象的集合
     * @param x
     * @param y
     * @return
     */
    public static List<Money> createMoney(float x, float y) {
        moneys = new ArrayList<>();
        Money money = null;
        for (int i = 0; i < 10; i++) {
            money = new Money(x, y);
            moneys.add(money);

        }
        return moneys;
    }

    /**
     * 更新储存钱对象的集合
     * @param moneys
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static List<Money> updateMoneys(List<Money> moneys, View view, float x, float y) {
        List<Money> moneyList=new ArrayList<>();
        int width =view.getMeasuredWidth();
        int height =view.getMeasuredHeight();
        //更新集合里面每个对象的坐标
        for (Money money : moneys) {
            money.updateLocation();
            //如果对象的范围还在控件内，则添加，不在则扔掉
            if (money.locationX>0&&money.locationX<width&&money.locationY>0&&money.locationY<height){
                moneyList.add(money);

            }
        }
        //用于控制动画时间的长短，往里面添加，就会有新的对象产生，就会产生源源不断的现象，不添加，则撒钱完毕
        if (number<10) {
            //在添加五个进去
            moneyList.addAll(createMoney(x, y));
            number++;
        }
        return moneyList;
    }


}
