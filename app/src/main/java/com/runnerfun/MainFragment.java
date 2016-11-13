package com.runnerfun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lixiaoyang on 16/10/2016.
 */

public class MainFragment extends Fragment {

    private Fragment mMapFragment = new MapFragment();
    private Fragment mDataFragment = new RunFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        switchFragment(mDataFragment);
    }

    public void switchToMap(){
        switchFragment(mMapFragment);

    }

    public void switchToData(){
        switchFragment(mDataFragment);
    }

    private void switchFragment(Fragment f){
        FragmentTransaction ft =  getChildFragmentManager().beginTransaction();
        ft.replace(R.id.content, f);
        ft.commitAllowingStateLoss();
    }

}
