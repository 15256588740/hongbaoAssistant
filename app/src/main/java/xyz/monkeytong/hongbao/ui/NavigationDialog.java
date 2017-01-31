package xyz.monkeytong.hongbao.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import xyz.monkeytong.hongbao.R;

/**
 * Created by wsong on 2017/1/31.
 */

public class NavigationDialog extends Dialog {

    public interface OnQuickOptionformClick {
        void onQuickOptionClick();
    }

    private OnQuickOptionformClick mListener;


    public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
        mListener = lis;
    }

    public NavigationDialog(Context context) {
        this(context, R.style.navigation_dialog);
    }

    public NavigationDialog(Context context, int theme) {
        super(context, theme);
        View contentView = getLayoutInflater().inflate(
                R.layout.dialog_layout, null);
        contentView.findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onQuickOptionClick();
                }
                dismiss();
            }
        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//设置布局
        super.setContentView(contentView);
    }
}