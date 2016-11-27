package com.runnerfun;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.runnerfun.widget.FeatureItemHolder;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareElemActivity extends AppCompatActivity {
    private static final String TEXT_PARAM = "text_param";
    private static final String IMAGE_PARAM = "image_param";
    public static Intent getStartIntent(Context c, String text, Uri imageUri){
        Intent i = new Intent(c, ShareElemActivity.class);
        i.putExtra(TEXT_PARAM, text);
        if(imageUri != null) {
            i.putExtra(IMAGE_PARAM, imageUri);
        }
        return i;
    }

    private Uri mImageUri = null;
    private static final List<Pair<Integer, String>> mFeatures = new ArrayList<>();
    {
        mFeatures.clear();
        mFeatures.add(new Pair<>(R.drawable.speed_selector, "配速"));
        mFeatures.add(new Pair<>(R.drawable.location_selector, "定位"));
        mFeatures.add(new Pair<>(R.drawable.time_selector, "时间"));
        mFeatures.add(new Pair<>(R.drawable.cal_selector, "卡路里"));
        mFeatures.add(new Pair<>(R.drawable.distance_selector, "里程"));
        mFeatures.add(new Pair<>(R.drawable.color_selector, "颜色设置"));
        mFeatures.add(new Pair<>(R.drawable.text_selector, "文字位置"));
    }

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
        if(getIntent().getExtras().containsKey(IMAGE_PARAM)) {
            mImageUri = getIntent().getParcelableExtra(IMAGE_PARAM);
        }

        ButterKnife.bind(this);

        if(mImageUri != null){
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(mImageUri));
                mBackgroundImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }

        if (getIntent().getExtras().containsKey(TEXT_PARAM)) {
            mContentTextView.setText(getIntent().getStringExtra(TEXT_PARAM));
        }
        //TODO:init other view
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecycleView.setAdapter(new RecyclerView.Adapter<FeatureItemHolder>(){

            @Override
            public FeatureItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.feature_item, parent, false);
                return new FeatureItemHolder(v);
            }

            @Override
            public void onBindViewHolder(FeatureItemHolder holder, int position) {
                Log.d("BIND_VIEW", "on bind " + position + " holder = " + holder);
                Pair<Integer, String> item = mFeatures.get(position);
                holder.setIcon(item.first);
                holder.setTitle(item.second);
            }

            @Override
            public int getItemCount() {
                return mFeatures.size();
            }
        });
    }

    private Bitmap getScreenSnapShot(){
        View v = findViewById(R.id.share_image);
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save){
            Bitmap bitmap = getScreenSnapShot();
            SimpleDateFormat formatter = new SimpleDateFormat("跑步之家-yyyy年MM月dd日HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, str, "description");
            //TODO:
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectColor(){
    }

}
