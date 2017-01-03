package com.palmap.BluetoothUtils.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhang on 2015/10/19.
 */
public class BeaconListAdapter extends BaseAdapter {
  private static CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

  private Context mContext;
  private List<Beacon> mBeaconList;
  private boolean showSelect;
  private boolean isFirst;
  private MapBeaconActivity mMapBeaconActivity;

  public BeaconListAdapter(Context context, List<Beacon> beaconList){
    this.mContext = context;
//
    Collections.sort(beaconList);
    this.mBeaconList= beaconList;

    onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
          mBeaconList.get(buttonView.getId()).setSelect(true);
        } else {
          mBeaconList.get(buttonView.getId()).setSelect(false);
        }
      }
    };
  }

  /*
  * 更新数据
  * */
  public void updateBeaconList(List<Beacon> beaconList){
    Collections.sort(beaconList);
    this.mBeaconList = beaconList;
    notifyDataSetChanged();
  }

  /*
  * minor被扫描到，更新数据
  * */
  public void updateScanedBeacon(Beacon b){
    Beacon beacon;
    mMapBeaconActivity = (MapBeaconActivity)mContext;
    for (int i=0;i<mBeaconList.size();i++) {
      beacon = mBeaconList.get(i);
      if (beacon.getMinor()==b.getMinor() && beacon.getUuid().equals(b.getUuid()) && beacon.getMajor()==b.getMajor()) {
        beacon.setScaned(true);
        beacon.setPowerPercent(b.getPowerPercent());
        beacon.setName(b.getName());
        mBeaconList.set(i, beacon);
//        mMapBeaconActivity.mBeaconList.set(i,beacon);//更新activity 中的list
        mMapBeaconActivity.getmBeaconManager().addBeacon(beacon);//更新数据库
        break;
      }
    }
    notifyDataSetChanged();
  }

  /*
  * 显示选择框
  * */
  public void showSelectBox(boolean isShow){
    this.showSelect = isShow;
    if (showSelect){ // 第一次进入列表，是checkBox都为未选中状态
      this.isFirst = true;
    } else {
      this.isFirst = false;
    }
//    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    if (mBeaconList == null){
      return 0;
    }
    return mBeaconList.size();
  }


  @Override
  public Beacon getItem(int position) {
    return mBeaconList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null){
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_beacon_lis, null);
      viewHolder.scanStateDot = (ImageView) convertView.findViewById(R.id.iv_isscaned);
      viewHolder.name = (TextView) convertView.findViewById(R.id.beacon_list_name);
      viewHolder.power = (TextView) convertView.findViewById(R.id.beacon_list_power);
      viewHolder.major = (TextView) convertView.findViewById(R.id.beacon_list_major);
      viewHolder.minor = (TextView) convertView.findViewById(R.id.beacon_list_minor);
      viewHolder.select = (CheckBox) convertView.findViewById(R.id.beacon_list_select);
      viewHolder.select.setOnCheckedChangeListener(onCheckedChangeListener);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    // 填内容
    viewHolder.name.setText(mBeaconList.get(position).getName());
    viewHolder.power.setText(mBeaconList.get(position).getPowerPercent()+"%");
    viewHolder.select.setId(position);
    viewHolder.major.setText(mBeaconList.get(position).getMajor() + "");
    viewHolder.minor.setText(mBeaconList.get(position).getMinor() + "");
    if (mBeaconList.get(position).isScaned()){
      viewHolder.scanStateDot.setBackgroundResource(R.drawable.ico_point_green);
    }else {
      viewHolder.scanStateDot.setBackgroundResource(R.drawable.ico_point_red);
    }
    if (showSelect){
      viewHolder.select.setVisibility(View.VISIBLE);
      if (isFirst){
        viewHolder.select.setChecked(false);
      }
    } else {
      viewHolder.select.setVisibility(View.GONE);
    }

    return convertView;
  }

  class ViewHolder{
    ImageView scanStateDot;
    TextView name;
    TextView major;
    TextView minor;
    TextView power;
    CheckBox select;
  }

}
