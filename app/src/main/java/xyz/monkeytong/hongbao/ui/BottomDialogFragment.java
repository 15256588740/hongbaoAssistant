package xyz.monkeytong.hongbao.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import xyz.monkeytong.hongbao.R;


/**
 * Description:
 */
public class BottomDialogFragment extends DialogFragment {

    private String[] mItems;
    private Context mContext;
    private String mTitleText;
    private ListView mListView;
    private int mCheckedItem = 0;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }



    public interface OnItemClickListener {
        void OnItemClick(int which);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_dialog_list, null);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(mItems[position]);
            if (position == mCheckedItem) {
                holder.text.setTextColor(Color.parseColor("#FF0000"));
            } else {
                holder.text.setTextColor(Color.GRAY);
            }
            return convertView;
        }

        public final class ViewHolder {
            public TextView text;
        }
    }


    public void load(String titleText, String[] items, int checkedItem) {
        this.mTitleText = titleText;
        this.mItems = items;
        this.mCheckedItem = checkedItem;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题并且指定style
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_dialog, null);
        ((TextView) view.findViewById(R.id.title)).setText(mTitleText);
        mListView = (ListView) view.findViewById(R.id.list_view);
        initListView();
        return view;
    }

    private void initListView() {
        final MyAdapter adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCheckedItem = position;
                adapter.notifyDataSetChanged();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClick(position);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}
