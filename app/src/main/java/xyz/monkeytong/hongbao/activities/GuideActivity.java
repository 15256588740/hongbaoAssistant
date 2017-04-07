package xyz.monkeytong.hongbao.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.utils.PrefUtils;

public class GuideActivity extends Activity {

	private ViewPager vp_viewpager;
	private List<ImageView> imageLists;
	private LinearLayout ll_guide_point;
	private LinearLayout.LayoutParams layoutParams;
	private ImageView iv_guide_redpoint;
	private int mPointDis;
	private Button btn_guide_entermain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		vp_viewpager = (ViewPager) findViewById(R.id.vp_viewpager);
		ll_guide_point = (LinearLayout) findViewById(R.id.ll_guide_point);
		iv_guide_redpoint = (ImageView) findViewById(R.id.iv_guide_redpoint);
		btn_guide_entermain = (Button) findViewById(R.id.btn_guide_entermain);
		init();
		vp_viewpager.setAdapter(new GuideAdapter());
		vp_viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 页面变化 显示/隐藏按钮
				if (position == imageLists.size() - 1)
					btn_guide_entermain.setVisibility(View.VISIBLE);
				else
					btn_guide_entermain.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// 滑动监听

				// 获取布局参数
				RelativeLayout.LayoutParams redPointParams = (RelativeLayout.LayoutParams) iv_guide_redpoint
						.getLayoutParams();
				// 修改布局参数
				redPointParams.leftMargin = (int) (mPointDis * positionOffset)
						+ position * mPointDis;
				// 重新设置布局参数
				iv_guide_redpoint.setLayoutParams(redPointParams);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		// 监听layout方法结束事件，位置确定好在获取移动距离
		// 因为redpoint 需要移动 ，所以用redpoint获取一个观察者
		iv_guide_redpoint.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						iv_guide_redpoint.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						// 计算两个原点之间的距离 移动距离 = 第二个原点left-第一个left
						mPointDis = ll_guide_point.getChildAt(1).getLeft()
								- ll_guide_point.getChildAt(0).getLeft();
					}
				});
		btn_guide_entermain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击按钮进入主页面
				PrefUtils.setBooleansp(getApplicationContext(),
						"is_first_enter", false);
				startActivity(new Intent(GuideActivity.this, MainActivity.class));
				finish();
			}
		});
	}

	// 初始化viewpager数据
	private void init() {
		int[] ImagesArray = { R.drawable.guide_1, R.drawable.guide_2,
				R.drawable.guide_3 };
		imageLists = new ArrayList<>();
		ImageView imageView;
		ImageView guide_gray_point;
		for (int i = 0; i < ImagesArray.length; i++) {
			imageView = new ImageView(getApplicationContext());
			imageView.setBackgroundResource(ImagesArray[i]);
			imageLists.add(imageView);
			guide_gray_point = new ImageView(getApplicationContext());
			guide_gray_point.setImageResource(R.drawable.gray_point_shape);
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (i > 0) { // 从第二个位置开始
				// 用该控件的父控件布局参数设置左边距
				layoutParams.leftMargin = 25;
			}
			guide_gray_point.setLayoutParams(layoutParams);
			ll_guide_point.addView(guide_gray_point);
		}

	}

	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageLists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = imageLists.get(position);
			container.addView(imageView);
			return imageView;
		}

	}



}
