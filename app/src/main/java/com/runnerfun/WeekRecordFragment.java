package com.runnerfun;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.RunWeekBean;
import com.runnerfun.model.AccountModel;
import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.ScalePageTransformer;
import com.runnerfun.widget.TransformViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import timber.log.Timber;

public class WeekRecordFragment extends Fragment {

    private TextView mSelected;
    private ArrayList<RunWeekBean> mRecords;
    private RecordAdapter mPagerAdapter;

    @BindView(R.id.week_one)
    TextView weekOne;
    @BindView(R.id.week_two)
    TextView weekTwo;
    @BindView(R.id.week_three)
    TextView weekThree;
    @BindView(R.id.week_four)
    TextView weekFour;
    @BindView(R.id.week_five)
    TextView weekFive;
    @BindView(R.id.week_six)
    TextView weekSix;
    @BindView(R.id.week_seven)
    TextView weekSeven;

    @BindView(R.id.week_viewpager)
    TransformViewPager viewPager;

    public WeekRecordFragment() {
    }

    public static WeekRecordFragment newInstance() {
        return new WeekRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_record, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mSelected = weekOne;
        final TextView[] views = {weekOne, weekTwo, weekThree, weekFour, weekFive, weekSix, weekSeven};
        final List<TextView> viewList = Arrays.asList(views);
        Observable.from(views).subscribe(new Action1<TextView>() {
            @Override
            public void call(TextView textView) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSelected.setTextColor(Color.parseColor("#808080"));
                        ((TextView) view).setTextColor(Color.parseColor("#E60012"));
                        mSelected = ((TextView) view);
                        viewPager.setCurrentItem(viewList.indexOf(mSelected));
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "init error");
            }
        });

        viewPager.setPageTransformer(true, new ScalePageTransformer());
        mPagerAdapter = new RecordAdapter(getActivity());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(7);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mSelected.setTextColor(Color.parseColor("#808080"));
                viewList.get(position).setTextColor(Color.parseColor("#E60012"));
                mSelected = viewList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AccountModel.instance.getUserWRecordList(new Subscriber<ArrayList<RunWeekBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), R.string.network_common_err, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ArrayList<RunWeekBean> runWeekBeen) {
                mRecords = runWeekBeen;
                // // TODO: 16/11/9
                mPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnTouch(R.id.viewpager_container)
    boolean onViewTouch(View view, MotionEvent event) {
        return viewPager.dispatchTouchEvent(event);
    }

    public class RecordAdapter extends RecyclingPagerAdapter {

        private final Context mContext;

        public RecordAdapter(Context context) {
            mContext = context;
        }

        public RunWeekBean getItem(int position) {
            if (mRecords != null && mRecords.size() > position) {
                return mRecords.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.layout_week_record_item, null);

                viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.user_avatar);
                viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.recordDistance = (TextView) convertView.findViewById(R.id.record_distance);
                viewHolder.recordEvaluate = (TextView) convertView.findViewById(R.id.record_evaluate);
                viewHolder.recordTime = (TextView) convertView.findViewById(R.id.record_time);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final RunWeekBean item = getItem(position);
            if (item != null) {
                // RunApplication.getAppContex().picasso.load(item).into(viewHolder.userAvatar);
                // viewHolder.userName.setText(item.getNum());
                viewHolder.recordDistance.setText(item.getDistance());
                // ?? TODO viewHolder.recordEvaluate.setText(item.getThe_time());
                viewHolder.recordTime.setText(item.getDate());
            }
            return convertView;
//            ImageView imageView = null;
//            if (convertView == null) {
//                imageView = new ImageView(mContext);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//            imageView.setTag(position);
//            imageView.setImageResource(R.drawable.icon_avatar);
//            return imageView;
        }

        private class ViewHolder {
            ImageView userAvatar;
            TextView userName;
            TextView recordDistance;
            TextView recordEvaluate;
            TextView recordTime;
        }

    }

}
