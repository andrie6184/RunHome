package com.runnerfun;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.runnerfun.mock.TrackMocker;
import com.runnerfun.model.RecordModel;
import com.runnerfun.tools.PathImageCreator;
import com.runnerfun.tools.Triple;
import com.runnerfun.widget.ColorPickerDialog;
import com.runnerfun.widget.FeatureItemHolder;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareElemActivity extends BaseFragmentActivity {

    private static final String TEXT_PARAM = "text_param";
    private static final String IMAGE_PARAM = "image_param";

    public static Intent getStartIntent(Context c, String text, Uri imageUri) {
        Intent i = new Intent(c, ShareElemActivity.class);
        i.putExtra(TEXT_PARAM, text);
        if (imageUri != null) {
            i.putExtra(IMAGE_PARAM, imageUri);
        }
        return i;
    }

    public static interface OnStatusChanged {
        public void onShow(boolean enable);
    }

    private Uri mImageUri = null;
    private static final List<Triple<Integer, String, OnStatusChanged>> mFeatures = new ArrayList<>();

    @BindView(R.id.background_image)
    ImageView mBackgroundImageView;
    @BindView(R.id.path)
    ImageView mPathImageView;
    @BindView(R.id.location)
    TextView mLocationText;
    @BindView(R.id.speed)
    TextView mSpeedView;
    @BindView(R.id.distance)
    TextView mDistanceView;
    @BindView(R.id.hour)
    TextView mHourView;
    @BindView(R.id.content)
    TextView mContentTextView;
    @BindView(R.id.features)
    RecyclerView mRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_elem);
        if (getIntent().getExtras().containsKey(IMAGE_PARAM)) {
            mImageUri = getIntent().getParcelableExtra(IMAGE_PARAM);
        }

        ButterKnife.bind(this);

        if (mImageUri != null) {
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(mImageUri));
                mBackgroundImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }

        if (getIntent().getExtras().containsKey(TEXT_PARAM)) {
            mContentTextView.setText(getIntent().getStringExtra(TEXT_PARAM));
        }

        Typeface boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
        mSpeedView.setTypeface(boldTypeFace);
        mHourView.setTypeface(boldTypeFace);
        mDistanceView.setTypeface(boldTypeFace);

        String speed = new DecimalFormat("###.##").format(RecordModel.instance.getSpeed());
        mSpeedView.setText(speed + "/h");
        mDistanceView.setText(String.valueOf(RecordModel.instance.getDistance() / 1000) + "km");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        mHourView.setText(format.format(new Date(RecordModel.instance.getRecordTime())));

        SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
        String location = sp.getString("location", "");
        mLocationText.setText(location);

        mPathImageView.setImageBitmap(PathImageCreator.createBitmap(RecordModel.instance.readCache()));
        //TODO:init other view
        initActionList();
        // initActionBar();
        initFeatures();
    }

    private void initActionList() {
        mFeatures.clear();
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.speed_selector, "配速", new OnStatusChanged() {
            @Override
            public void onShow(boolean enable) {
                enableView(mSpeedView, enable);
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.location_selector, "定位", new OnStatusChanged() {

            @Override
            public void onShow(boolean enable) {
                enableView(mLocationText, enable);
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.time_selector, "时间", new OnStatusChanged() {
            @Override
            public void onShow(boolean enable) {
                enableView(mHourView, enable);
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.cal_selector, "卡路里", new OnStatusChanged() {
            @Override
            public void onShow(boolean enable) {
                //TODO 哪有卡路里
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.distance_selector, "里程", new OnStatusChanged() {
            @Override
            public void onShow(boolean enable) {
                enableView(mDistanceView, enable);
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.color_unchecked, "颜色设置", new OnStatusChanged() {

            @Override
            public void onShow(boolean enable) {
                selectColor();
            }
        }));
        mFeatures.add(new Triple<Integer, String, OnStatusChanged>(R.drawable.text_selector, "文字位置", new OnStatusChanged() {
            @Override
            public void onShow(boolean enable) {
                //TODO://文字怎么换?
            }
        }));
    }

    /*private void initActionBar() {
        ActionBar action = getSupportActionBar();
        action.setTitle("水印照片");
        action.setDisplayHomeAsUpEnabled(true);
    }*/

    private void initFeatures() {
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecycleView.setAdapter(new RecyclerView.Adapter<FeatureItemHolder>() {

            @Override
            public FeatureItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.feature_item, parent, false);
                return new FeatureItemHolder(v);
            }

            @Override
            public void onBindViewHolder(FeatureItemHolder holder, int position) {
                Log.d("BIND_VIEW", "on bind " + position + " holder = " + holder);
                Triple<Integer, String, OnStatusChanged> item = mFeatures.get(position);
                holder.setIcon(item.first);
                holder.setTitle(item.second);
                holder.setOnStatus(item.third);
            }

            @Override
            public int getItemCount() {
                return mFeatures.size();
            }
        });
    }

    private Bitmap getScreenSnapShot() {
        View v = findViewById(R.id.share_image);
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            Bitmap bitmap = getScreenSnapShot();
            SimpleDateFormat formatter = new SimpleDateFormat("跑步之家-yyyy年MM月dd日HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, str, "description");
            //TODO:
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

    @OnClick(R.id.save_btn)
    void onSaveClicked(View view) {
        Bitmap bitmap = getScreenSnapShot();
        SimpleDateFormat formatter = new SimpleDateFormat("跑步之家-yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, str, "description");
        Toast.makeText(this, "图片已保存", Toast.LENGTH_SHORT).show();
    }

    private void selectColor() {
        ColorPickerDialog dialog = new ColorPickerDialog();
        dialog.show(getSupportFragmentManager(), "color_picked");
        dialog.setColorListener(new ColorPickerDialog.ColorPickerListener() {
            @Override
            public void onSelect(int color) {
                mContentTextView.setTextColor(color);
                mHourView.setTextColor(color);
                mDistanceView.setTextColor(color);
                mSpeedView.setTextColor(color);
                mLocationText.setTextColor(color);
            }
        });
        dialog.setCancelable(true);
    }

    private void enableView(View v, boolean enable) {
        v.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

}
