package com.runnerfun;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.RunRecordBean;
import com.runnerfun.model.AccountModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

import static com.runnerfun.model.AccountModel.COMMON_PAGE_SIZE;

public class PersonalRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<RunRecordBean> mRecords;
    private boolean isLoading = false;
    private Typeface boldTypeFace;

    @BindView(R.id.precord_list_ptr_frame)
    SwipeRefreshLayout mPtrLayout;
    @BindView(R.id.precord_list_view)
    ListView mRecordList;

    private RecordListAdapter mAdapter;

    public PersonalRecordFragment() {
    }

    public static PersonalRecordFragment newInstance() {
        return new PersonalRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_record, container, false);
        ButterKnife.bind(this, view);
        boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond-bold.otf");
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestCoinInfo(false);
    }

    private void init() {
        mPtrLayout.setOnRefreshListener(this);
        mAdapter = new RecordListAdapter();
        mRecordList.setAdapter(mAdapter);
        mRecordList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        view.getLastVisiblePosition() == (view.getCount() - 1) && view.getCount() > 0
                        && !isLoading && mRecords != null && mRecords.size() >= COMMON_PAGE_SIZE) {
                    requestCoinInfo(true);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    @Override
    public void onRefresh() {
        requestCoinInfo(false);
    }

    private void requestCoinInfo(final boolean requestMore) {
        isLoading = true;
        int page = 0;
        if (requestMore && mRecords != null && mRecords.size() >= COMMON_PAGE_SIZE) {
            page = mRecords.size() / COMMON_PAGE_SIZE;
        }
        AccountModel.instance.getUserPRecordList(page, new Subscriber<ArrayList<RunRecordBean>>() {
            @Override
            public void onCompleted() {
                isLoading = false;
                mPtrLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                showError(R.string.network_common_err);
                mPtrLayout.setRefreshing(false);
            }

            @Override
            public void onNext(ArrayList<RunRecordBean> records) {
                if (records != null) {
                    if (records.size() > 0) {
                        if (!requestMore) {
                            mRecords = records;
                        } else {
                            mRecords.addAll(records);
                        }
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
                showError(R.string.network_no_data);
            }
        });
    }

    private void showError(int error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private class RecordListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mRecords != null) {
                return mRecords.size();
            }
            return 0;
        }

        @Override
        public RunRecordBean getItem(int i) {
            if (mRecords != null && mRecords.size() > i) {
                return mRecords.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(getActivity());
                convertView = mInflater.inflate(R.layout.layout_precord_list_item, null);

                viewHolder.lengthValue = (TextView) convertView.findViewById(R.id.record_distance);
                viewHolder.lengthValue.setTypeface(boldTypeFace);
                viewHolder.recordDate = (TextView) convertView.findViewById(R.id.record_time);
                viewHolder.recordAddress = (TextView) convertView.findViewById(R.id.record_location);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final RunRecordBean item = getItem(position);
            if (item != null) {
                viewHolder.lengthValue.setText(item.getDistance());
                viewHolder.recordDate.setText(item.getStartTime());
                viewHolder.recordAddress.setText(item.getLocation());
            }
            return convertView;
        }

        private class ViewHolder {
            TextView lengthValue;
            TextView recordDate;
            TextView recordAddress;
        }

    }

}
