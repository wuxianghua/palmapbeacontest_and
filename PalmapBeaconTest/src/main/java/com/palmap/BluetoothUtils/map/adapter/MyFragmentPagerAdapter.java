package com.palmap.BluetoothUtils.map.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by zhang on 2015/10/15.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
  private List<Fragment> mFragmentList;

  public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
    super(fm);
    this.mFragmentList = fragmentList;
  }

  @Override
  public Fragment getItem(int i) {
    return mFragmentList.get(i);
  }

  @Override
  public int getCount() {
    return mFragmentList.size();
  }



}
