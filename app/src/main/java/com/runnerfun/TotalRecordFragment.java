package com.runnerfun;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.RunTotalBean;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.tools.UITools;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class TotalRecordFragment extends Fragment {

    @BindView(R.id.total_hour)
    TextView totalHour;
    @BindView(R.id.total_distance)
    TextView totalDistance;
    @BindView(R.id.total_hot)
    TextView totalHot;
    @BindView(R.id.average_speed)
    TextView averageSpeed;
    @BindView(R.id.average_sec_speed)
    TextView averageSecSpeed;
    @BindView(R.id.max_speed)
    TextView maxSpeed;

    public TotalRecordFragment() {
    }

    public static TotalRecordFragment newInstance() {
        return new TotalRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_record, container, false);
        ButterKnife.bind(this, view);
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond-bold.otf");
        totalHour.setTypeface(boldTypeFace);
        totalDistance.setTypeface(boldTypeFace);
        totalHot.setTypeface(boldTypeFace);
        averageSpeed.setTypeface(boldTypeFace);
        averageSecSpeed.setTypeface(boldTypeFace);
        maxSpeed.setTypeface(boldTypeFace);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestData();
    }

    private void requestData() {
        NetworkManager.instance.getUserTRecordList(new Subscriber<RunTotalBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                showError(R.string.network_common_err);
            }

            @Override
            public void onNext(RunTotalBean runTotalBean) {
                if (runTotalBean != null) {
                    totalHour.setText(TimeStringUtils.getTime(Long.valueOf(runTotalBean.getSumTimes())));
                    float distance = Float.valueOf(runTotalBean.getSumDistance());
                    totalDistance.setText(UITools.numberFormat(distance/ 1000));
                    totalHot.setText(UITools.numberFormat(runTotalBean.getSumCalorie()));
                    averageSpeed.setText(UITools.numberFormat(runTotalBean.getSumTimes()));
                    averageSecSpeed.setText(UITools.numberFormat(runTotalBean.getSumDistance()));
                    maxSpeed.setText(UITools.numberFormat(runTotalBean.getMaxHighSpeed()));
                    return;
                }
                showError(R.string.network_no_data);
            }
        });
    }

    private void showError(int error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

}
