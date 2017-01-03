//package com.palmap.BluetoothUtils.main.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.palmap.BluetoothUtils.R;
//import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
//import com.palmap.BluetoothUtils.main.activity.DetailHistoryActivity;
//import com.palmap.BluetoothUtils.main.activity.ScanHistoryActivity;
//import com.palmap.BluetoothUtils.main.constant.Constant;
//import com.palmap.BluetoothUtils.main.model.Beacon;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by eric3 on 2016/9/20.
// */
//public class DetailHistoryListAdapter extends BaseAdapter{
//  private DetailHistoryActivity mActivity;
//  private ScanHistorySerializable mScanHistorySerializable;
//  private String mFloorNameID;
//  private long mTime;
//  private long mMallID;
//  private String mUserName;
//  private int mAbnormalBeaconNum;//异常beacon数量
//  private boolean mIsUpload;
//  private List<Beacon> beaconList;
//
//  public DetailHistoryListAdapter(Context context, ScanHistorySerializable mScanHistorySerializable){
//
//    mActivity = (DetailHistoryActivity) context;
////    Collections.sort(beaconList);
//    this.mScanHistorySerializable= mScanHistorySerializable;
//
//    this.beaconList = mScanHistorySerializable.getBeaconList();
//  }
//
//
//
//  @Override
//  public int getCount() {
//    if (beaconList == null){
//      return 0;
//    }
//    return beaconList.size();
//  }
//
//  @Override
//  public Object getItem(int position) {
//    return beaconList.get(position);
//  }
//
//  @Override
//  public long getItemId(int position) {
//    return position;
//  }
//
//  @Override
//  public View getView(int position, View convertView, ViewGroup parent) {
//    ViewHolder viewHolder = null;
//    if (convertView == null){
//      viewHolder = new ViewHolder();
//      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_beacon_lis, null);
//      viewHolder.scanStateDot = (ImageView) convertView.findViewById(R.id.iv_isscaned);
//      viewHolder.uuid = (TextView) convertView.findViewById(R.id.beacon_list_uuid);
//      viewHolder.major = (TextView) convertView.findViewById(R.id.beacon_list_major);
//      viewHolder.minor = (TextView) convertView.findViewById(R.id.beacon_list_minor);
//      viewHolder.select = (CheckBox) convertView.findViewById(R.id.beacon_list_select);
//      viewHolder.select.setOnCheckedChangeListener(onCheckedChangeListener);
//      convertView.setTag(viewHolder);
//    } else {
//      viewHolder = (ViewHolder) convertView.getTag();
//    }
//
//    // 填内容
//    viewHolder.uuid.setText(mBeaconList.get(position).getUuid());
//    viewHolder.select.setId(position);
//    viewHolder.major.setText(mBeaconList.get(position).getMajor() + "");
//    viewHolder.minor.setText(mBeaconList.get(position).getMinor() + "");
//    if (mBeaconList.get(position).isScaned()){
//      viewHolder.scanStateDot.setBackgroundResource(R.drawable.ico_point_green);
//    }else {
//      viewHolder.scanStateDot.setBackgroundResource(R.drawable.ico_point_red);
//    }
//
//
//    return convertView;
//  }
//
//  class ViewHolder{
//    ImageView scanStateDot;
//    TextView uuid;
//    TextView major;
//    TextView minor;
//
//
//  }
//
//}
