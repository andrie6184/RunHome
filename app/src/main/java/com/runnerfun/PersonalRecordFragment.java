package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.PersonalRecordBean;
import com.runnerfun.beans.PersonalRunRecordBean;
import com.runnerfun.beans.RunRecordBean;
import com.runnerfun.beans.RunTrackBean;
import com.runnerfun.beans.RunUploadDB;
import com.runnerfun.model.RecordModel;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.tools.UITools;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import rx.Subscriber;

import static com.runnerfun.network.NetworkManager.COMMON_PAGE_SIZE;

public class PersonalRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TRACKS_REFRESH_ACTION = "TRACKS_REFRESH_ACTION";

    private ArrayList<PersonalRunRecordBean> mRecords = new ArrayList<>();
    private boolean isLoading = false;
    private Typeface boldTypeFace;

    private PersonalRunRecordBean mDeleteBean;
    private boolean mIsDeleting;
    private boolean mIsJumping;
    private boolean mHasMoreData;
    private int totalSize;
    private int localSize;

    @BindView(R.id.precord_list_ptr_frame)
    SwipeRefreshLayout mPtrLayout;
    @BindView(R.id.precord_list_view)
    ListView mRecordList;

    private RecordListAdapter mAdapter;

    private LocalBroadcastManager mLocalManager;
    private RecordRefreshReceiver mReceiver;

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

        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(TRACKS_REFRESH_ACTION);
        mReceiver = new RecordRefreshReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // requestCoinInfo(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
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
                        && !isLoading && mRecords != null && mHasMoreData) {
                    requestCoinInfo(true);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        mRecordList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "删除");
            }
        });

        requestCoinInfo(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (mDeleteBean.getRid().equals("-1")) {
                DataSupport.delete(RunUploadDB.class, mDeleteBean.getId());
            } else {
                NetworkManager.instance.deleteRunRecord(mDeleteBean.getRid(), new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        mIsDeleting = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsDeleting = false;
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object s) {
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        mRecords.remove(mDeleteBean);
                        mAdapter.notifyDataSetChanged();
                        totalSize -= 1;
                        mHasMoreData = totalSize <= 0 || totalSize > (mRecords.size() - localSize);
                    }
                });
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onRefresh() {
        requestCoinInfo(false);
    }

    @OnItemClick(R.id.precord_list_view)
    void onRecordItemClicked(AdapterView<?> parent, View view, int position, long id) {
        if (mIsJumping) {
            return;
        }
        mIsJumping = true;
        final PersonalRunRecordBean item = mRecords.get(position);
        if (!TextUtils.isEmpty(item.getTrack())) {
            openMapActivity(item.getTrack(), item.getTotal_distance(), item.getTotal_time(), item.getDistance(),
                    item.getCalorie(), "0", "-1");
            mIsJumping = false;
        } else {
            final String rid = item.getRid();
            NetworkManager.instance.getRunTrack(rid, new Subscriber<RunTrackBean>() {
                @Override
                public void onCompleted() {
                    mIsJumping = false;
                }

                @Override
                public void onError(Throwable e) {
                    mIsJumping = false;
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(RunTrackBean runTrackBean) {
                    openMapActivity(runTrackBean.getTrack(), item.getTotal_distance(), runTrackBean.getTotal_time()
                            , runTrackBean.getDistance(), item.getCalorie(), rid, item.getGet_score());
                }
            });
        }
    }

    @OnItemLongClick(R.id.precord_list_view)
    boolean onRecordItemLongClicked(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsDeleting) {
            mIsDeleting = true;
            mDeleteBean = mRecords.get(position);
            mRecordList.showContextMenu();
            return true;
        }
        return false;
    }

    private void requestCoinInfo(final boolean requestMore) {
        isLoading = true;
        int page = 1;
        if (requestMore && mRecords != null && mRecords.size() >= COMMON_PAGE_SIZE) {
            page = mRecords.size() / COMMON_PAGE_SIZE + 1;
        }
        NetworkManager.instance.getUserPRecordList(page, new Subscriber<PersonalRecordBean>() {
            @Override
            public void onCompleted() {
                isLoading = false;
                mPtrLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                List<RunUploadDB> localRecords = DataSupport.findAll(RunUploadDB.class);
                if (localRecords != null && localRecords.size() > 0) {
                    for (RunUploadDB db : localRecords) {
                        mRecords.add(new PersonalRunRecordBean(db));
                    }
                } else {
                    showError(R.string.network_common_err);
                }
                mPtrLayout.setRefreshing(false);
            }

            @Override
            public void onNext(PersonalRecordBean records) {
                if (records != null) {
                    try {
                        totalSize = Integer.valueOf(records.getCnt());
                    } catch (Exception e) {
                        totalSize = -1;
                    }
                    if (records.getList().size() > 0) {
                        localSize = 0;
                        if (!requestMore) {
                            mRecords.clear();

                            List<RunUploadDB> localRecords = DataSupport.findAll(RunUploadDB.class);
                            if (localRecords != null && localRecords.size() > 0) {
                                for (RunUploadDB db : localRecords) {
                                    mRecords.add(new PersonalRunRecordBean(db));
                                }
                                localSize = localRecords.size();
                            }
                        }
                        for (RunRecordBean bean : records.getList()) {
                            mRecords.add(new PersonalRunRecordBean(bean));
                        }
                        mHasMoreData = totalSize <= 0 || totalSize > (mRecords.size() - localSize);
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

    private void openMapActivity(String track, String totalDis, String totalTime, String distance,
                                 String calorie, String score, String rid) {
        List<TimeLatLng> lls = RecordModel.parseStringToLatLng(track);
        RecordModel.instance.initRecord(lls);
        String dis = UITools.numberFormat(Float.valueOf(totalDis)) + "km";

        String speed = "0'00\"";
        if (Float.valueOf(totalTime) > 0 && Float.valueOf(distance) > 0) {
            float seconds = Float.valueOf(totalTime) / (Float.valueOf(distance));
            int minutes = (int) seconds / 60;
            speed = String.format(Locale.getDefault(), "%d'%d\"", minutes, (int) (seconds % 60));
        }

        String cal = UITools.numberFormat(Float.valueOf(calorie) / 1000) + "kcal";
        String time = TimeStringUtils.getTime(Long.valueOf(totalTime) * 1000);
        MapActivity.startWithDisplayMode(getActivity(), dis, speed, time, cal, rid, score);
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
        public PersonalRunRecordBean getItem(int i) {
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
                viewHolder.warning = (TextView) convertView.findViewById(R.id.warning);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final PersonalRunRecordBean item = getItem(position);
            if (item != null) {
                viewHolder.lengthValue.setText(UITools.numberFormat(item.getTotal_distance()));
                viewHolder.recordDate.setText(item.getStartTime().split(" ")[0]);
                viewHolder.recordAddress.setText(item.getPosition());

                if (Double.valueOf(item.getTotal_distance()) > Double.valueOf(item.getDistance())) {
                    viewHolder.warning.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.warning.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView lengthValue;
            TextView recordDate;
            TextView recordAddress;
            TextView warning;
        }

    }

    private class RecordRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestCoinInfo(false);
        }
    }

}
