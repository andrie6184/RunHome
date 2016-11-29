package com.runnerfun;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.ScalePageTransformer;
import com.runnerfun.widget.TransformViewPager;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class RunRecordActivity extends Activity {

    @BindView(R.id.info_viewpager)
    TransformViewPager viewPager;

    private RecordAdapter mPagerAdapter;
    private Typeface boldTypeFace;

    private Map<String, String> runInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_record);
        ButterKnife.bind(this);
        boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
        init();
    }

    private void init() {
        viewPager.setPageTransformer(true, new ScalePageTransformer(1.2f, 0.8f));
        mPagerAdapter = new RecordAdapter(this);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(7);
        viewPager.setCurrentItem(5000);
    }

    @OnTouch(R.id.viewpager_container)
    boolean onViewTouch(View view, MotionEvent event) {
        return viewPager.dispatchTouchEvent(event);
    }

    public class RecordAdapter extends RecyclingPagerAdapter {

        private final Context mContext;

        public RecordAdapter(Context context) {
            mContext = context;

            //// for test !!!!!!
            runInfo = new HashMap<>();
            runInfo.put("总消耗", "45kcal");
            runInfo.put("平均配速", "12km/s");
            runInfo.put("总里程", "13km");
            runInfo.put("总用时", "20:12");
        }

        public InfoValue getItem(int position) {
            int offset = position % 4;
            String[] key = new String[4];
            key = runInfo.keySet().toArray(key);
            return new InfoValue(key[offset], runInfo.get(key[offset]));
        }

        @Override
        public int getCount() {
            return 10001;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.layout_run_record_item, null);

                viewHolder.attrName = (TextView) convertView.findViewById(R.id.attr_name);
                viewHolder.attrValue = (TextView) convertView.findViewById(R.id.attr_value);
                viewHolder.attrValue.setTypeface(boldTypeFace);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final InfoValue item = getItem(position);
            if (item != null) {
                viewHolder.attrName.setText(item.key);
                viewHolder.attrValue.setText(item.value);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView attrName;
            TextView attrValue;
        }

        private class InfoValue {

            public InfoValue(String s, String v) {
                key = s;
                value = v;
            }

            public String key;
            public String value;
        }

    }

}
