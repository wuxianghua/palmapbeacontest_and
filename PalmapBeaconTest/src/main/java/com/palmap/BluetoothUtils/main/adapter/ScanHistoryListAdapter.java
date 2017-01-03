package com.palmap.BluetoothUtils.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
import com.palmap.BluetoothUtils.main.activity.DetailHistoryActivity;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.PalMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by eric3 on 2016/9/20.
 */
public class ScanHistoryListAdapter extends BaseAdapter {
  private static CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
  private boolean showSelect = false;
  private boolean isFirst;
  private PalMap mPalMap;

  private Context mActivity;
  private List<ScanHistorySerializable> mScanHistoryList;
  private boolean[] mIsCheckedList;//是否被选中

  public ScanHistoryListAdapter(Context context,PalMap palMap,List<ScanHistorySerializable> scanHistoryList){

    mActivity = context;
    this.mPalMap = palMap;
//    Collections.sort(beaconList);
    this.mScanHistoryList= scanHistoryList;
    initIsScanList();
  }

  public void refeshScanHistoryList(PalMap palMap,List<ScanHistorySerializable> scanHistoryList){
    this.mScanHistoryList= scanHistoryList;
    this.mPalMap = palMap;
    initIsScanList();
    notifyDataSetChanged();
  }

  public boolean[] getIsCheckedList() {
    return mIsCheckedList;
  }

  private void initIsScanList(){
    if (mScanHistoryList==null)
      return;

    mIsCheckedList = new boolean[mScanHistoryList.size()];

    setListAllChecked(false);
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
    setListAllChecked(false);

    notifyDataSetChanged();
  }

  private void setListAllChecked(boolean b){
    for (int i=0;i<mIsCheckedList.length;i++){
      mIsCheckedList[i] = b;
    }
  }


  public void setAllChecked(boolean b){
    setListAllChecked(true);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    if (mScanHistoryList == null){
      return 0;
    }
    return mScanHistoryList.size();
  }

  @Override
  public Object getItem(int position) {
    return mScanHistoryList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    final ViewHolder viewHolder;
    if (convertView == null){
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_scan_history_list, null);
      viewHolder.uploadState = (ImageView) convertView.findViewById(R.id.iv_upload);
      viewHolder.floor = (TextView) convertView.findViewById(R.id.history_list_floorid);
      viewHolder.userName = (TextView) convertView.findViewById(R.id.history_list_user);
      viewHolder.beaconNum = (TextView) convertView.findViewById(R.id.history_list_beacon);
      viewHolder.time = (TextView) convertView.findViewById(R.id.history_list_time);
      viewHolder.select = (CheckBox) convertView.findViewById(R.id.beacon_list_select);
      viewHolder.select.setOnCheckedChangeListener(onCheckedChangeListener);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    // 填内容
    viewHolder.floor.setText(mScanHistoryList.get(position).getFloorName());
    viewHolder.userName.setText(mScanHistoryList.get(position).getUserName());
    viewHolder.select.setId(position);
    int num = mScanHistoryList.get(position).getBeaconList().size();
    viewHolder.beaconNum.setText(num-mScanHistoryList.get(position).getAbnormalBeaconNum()+"/"+num);
    SimpleDateFormat  formatter = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
    Date date = new Date(mScanHistoryList.get(position).getTime());
    viewHolder.time.setText(formatter.format(date));

//    convertView.findViewById(R.id.floor).setVisibility(View.GONE);
//    convertView.findViewById(R.id.beacon_num).setVisibility(View.GONE);

    if (mScanHistoryList.get(position).isUpload()){
      viewHolder.uploadState.setBackgroundResource(R.drawable.ico_cloud);
    }else {
      viewHolder.uploadState.setBackgroundResource(R.drawable.ico_local);
    }
    if (showSelect){
      viewHolder.select.setVisibility(View.VISIBLE);
      if (isFirst) {
        viewHolder.select.setChecked(false);
      }
    } else {
      viewHolder.select.setVisibility(View.GONE);
    }

    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (showSelect){
          boolean b = viewHolder.select.isChecked();
          if (b){
            viewHolder.select.setChecked(false);
            mIsCheckedList[position] = false;
          }else {
            viewHolder.select.setChecked(true);
            mIsCheckedList[position] = true;
          }
        }else {
          Intent intent = new Intent(mActivity, DetailHistoryActivity.class);
          intent.putExtra(Constant.TAG_SCANHISTORY_OBJECT, mScanHistoryList.get(position));
          intent.putExtra(Constant.TAG_MAP_OBJECT, mPalMap);
          mActivity.startActivity(intent);
        }
      }
    });

    return convertView;
  }

  class ViewHolder{
    ImageView uploadState;
    TextView floor;
    TextView userName;
    TextView beaconNum;
    TextView time;
    CheckBox select;
  }
}
