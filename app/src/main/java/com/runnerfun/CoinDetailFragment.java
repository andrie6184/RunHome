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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.Coin;
import com.runnerfun.beans.CoinBean;
import com.runnerfun.beans.CoinSummary;
import com.runnerfun.network.NetworkManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

import static com.runnerfun.network.NetworkManager.COMMON_PAGE_SIZE;

public class CoinDetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_COIN_TYPE = "ARG_COIN_TYPE";

    private int mCoinType; // 1.system, 2.paobu && paobu init whit 0, system init with 1;
    private List<Coin> mCoins;
    private CoinSummary mSummary;

    private boolean isLoading = false;
    private Typeface boldTypeFace;

    @BindView(R.id.coin_total)
    TextView mCoinTotal;
    @BindView(R.id.coin_gift)
    TextView mCoinGift;
    @BindView(R.id.coin_out)
    TextView mCoinOut;

    @BindView(R.id.coin_list_ptr_frame)
    SwipeRefreshLayout mPtrLayout;
    @BindView(R.id.coin_list_view)
    ListView mCoinList;

    private CoinListAdapter mAdapter;

    public CoinDetailFragment() {
    }

    public static CoinDetailFragment newInstance(int type) {
        CoinDetailFragment fragment = new CoinDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COIN_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCoinType = getArguments().getInt(ARG_COIN_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin_detail, container, false);
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
        mCoinTotal.setTypeface(boldTypeFace);
        mCoinGift.setTypeface(boldTypeFace);
        mCoinOut.setTypeface(boldTypeFace);

        mPtrLayout.setOnRefreshListener(this);
        mAdapter = new CoinListAdapter();
        mCoinList.setAdapter(mAdapter);
        mCoinList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        view.getLastVisiblePosition() == (view.getCount() - 1) && view.getCount() > 0
                        && !isLoading && mCoins != null && mCoins.size() >= COMMON_PAGE_SIZE) {
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
        int type = 1;
        if (mCoinType == 0) {
            type = 2;
        }
        int page = 0;
        if (requestMore && mCoins != null && mCoins.size() >= COMMON_PAGE_SIZE) {
            page = mCoins.size() / COMMON_PAGE_SIZE;
        }
        NetworkManager.instance.getUserCoins(type, page, new Subscriber<CoinBean>() {
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
            public void onNext(CoinBean coinBean) {
                if (coinBean != null) {
                    if (coinBean.getSummary() != null) {
                        mSummary = coinBean.getSummary();
                        showSummary();
                    }
                    if (coinBean.getList() != null && coinBean.getList().size() > 0) {
                        if (!requestMore) {
                            mCoins = coinBean.getList();
                        } else {
                            mCoins.addAll(coinBean.getList());
                        }
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
                showError(R.string.network_no_data);
            }
        });
    }

    private void showSummary() {
        mCoinTotal.setText(mSummary.getAmount());
        mCoinGift.setText(mSummary.getCanGive());
        mCoinOut.setText(mSummary.getOverdue());
    }

    private void showError(int error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private class CoinListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mCoins != null) {
                return mCoins.size();
            }
            return 0;
        }

        @Override
        public Coin getItem(int i) {
            if (mCoins != null && mCoins.size() > i) {
                return mCoins.get(i);
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
                convertView = mInflater.inflate(R.layout.layout_coin_list_item, null);

                viewHolder.coinValue = (TextView) convertView.findViewById(R.id.coin_value);
                viewHolder.coinValue.setTypeface(boldTypeFace);
                viewHolder.coinDetail = (TextView) convertView.findViewById(R.id.coin_detail);
                viewHolder.coinTime = (TextView) convertView.findViewById(R.id.coin_time);
                viewHolder.imageLocation = (ImageView) convertView.findViewById(R.id.image_location);
                if (mCoinType == 0) {
                    viewHolder.imageLocation.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.imageLocation.setVisibility(View.INVISIBLE);
                }

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Coin item = getItem(position);
            if (item != null) {
                viewHolder.coinValue.setText(item.getNum());
                viewHolder.coinDetail.setText(item.getTitle());
                viewHolder.coinTime.setText(item.getThe_time());
            }
            return convertView;
        }

        private class ViewHolder {
            TextView coinValue;
            TextView coinDetail;
            TextView coinTime;
            ImageView imageLocation;
        }

    }

}
