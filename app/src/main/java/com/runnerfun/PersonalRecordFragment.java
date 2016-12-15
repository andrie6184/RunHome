package com.runnerfun;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.amap.api.maps.model.LatLng;
import com.runnerfun.beans.PersonalRecordBean;
import com.runnerfun.beans.RunRecordBean;
import com.runnerfun.beans.RunTrackBean;
import com.runnerfun.model.RecordModel;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.UITools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import rx.Subscriber;

import static com.runnerfun.network.NetworkManager.COMMON_PAGE_SIZE;

public class PersonalRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<RunRecordBean> mRecords;
    private boolean isLoading = false;
    private Typeface boldTypeFace;

    private RunRecordBean mDeleteBean;
    private boolean mIsDeleting;
    private boolean mIsJumping;

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
        // requestCoinInfo(false);
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
                }
            });
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
        final RunRecordBean item = mRecords.get(position);
        String rid = item.getRid();
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
                List<LatLng> lls = RecordModel.parseStringToLatLng(runTrackBean.getTrack());
                RecordModel.instance.initRecord(lls);
                String dis = UITools.numberFormat(Float.valueOf(item.getDistance()) / 1000) + "km";
                String speed = UITools.numberFormat(Float.valueOf(item.getSpeed())) + "km/h";
                String cal = UITools.numberFormat(Float.valueOf(item.getCalorie()) / 1000) + "kcal";
                MapActivity.startWithDisplayMode(getActivity(), dis, speed, item.getStartTime().split(" ")[0], cal);
            }
        });
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
                showError(R.string.network_common_err);
                mPtrLayout.setRefreshing(false);
            }

            @Override
            public void onNext(PersonalRecordBean records) {
                if (records != null) {
                    if (records.getList().size() > 0) {
                        if (!requestMore) {
                            mRecords = records.getList();
                        } else {
                            mRecords.addAll(records.getList());
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
                viewHolder.lengthValue.setText(UITools.numberFormat(item.getDistance()));
                viewHolder.recordDate.setText(item.getStartTime().split(" ")[0]);
                viewHolder.recordAddress.setText(item.getPosition());
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
