package xyz.monkeytong.hongbao.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.utils.PrefUtils;

public class SplashActivity extends Activity {

	private RelativeLayout ll_splash_root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ll_splash_root = (RelativeLayout) findViewById(R.id.ll_splash_root);
		// 旋转动画
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		// 缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		// 渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setFillAfter(true);
		// 动画集合
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 获取是否是第一次进入app
				boolean is_first_enter = PrefUtils.getBooleansp(
						getApplicationContext(), "is_first_enter", true);
				Intent intent;
				if (is_first_enter) {
					// 第一次打开app进入引导界面
					intent = new Intent(SplashActivity.this,
							GuideActivity.class);
				} else {
					// 进入主页面
					intent = new Intent(SplashActivity.this, MainActivity.class);
				}
				startActivity(intent);
				finish(); // 关闭splash页面
			}
		});
		ll_splash_root.startAnimation(animationSet);

	}

}
