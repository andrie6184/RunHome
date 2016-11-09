package com.runnerfun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.RunTotalBean;
import com.runnerfun.model.AccountModel;

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestData();
    }

    private void requestData() {
        AccountModel.instance.getUserTRecordList(new Subscriber<RunTotalBean>() {
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
                    totalHour.setText(runTotalBean.getSumTimes());
                    totalDistance.setText(runTotalBean.getSumTotalDistance());
                    totalHot.setText(runTotalBean.getSumCalorie());
                    // TODO
                    averageSpeed.setText(runTotalBean.getSumTimes());
                    averageSecSpeed.setText(runTotalBean.getSumDistance());
                    // ??? how to compute ???
                    maxSpeed.setText(runTotalBean.getMaxHighSpeed());
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
