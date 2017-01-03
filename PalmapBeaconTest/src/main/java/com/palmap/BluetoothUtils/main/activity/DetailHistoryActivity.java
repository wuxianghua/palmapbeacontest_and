package com.palmap.BluetoothUtils.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
import com.palmap.BluetoothUtils.map.adapter.BeaconListAdapter;
import com.palmap.BluetoothUtils.widget.ActionBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailHistoryActivity extends BaseActivity {
  private ListView mHistoryListView;
  private ActionBar mActionBar;
  private ScanHistorySerializable mScanHistorySerializable;
  private BeaconListAdapter mBeaconListAdapter;
  private List<Beacon> mBeaconList;
  private SQLiteHelper sqLiteHelper;
  private PalMap mPalMap;

  private TextView tv_floor;
  private TextView tv_userName;
  private TextView tv_beaconNum;
  private TextView tv_time;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_history);

    initData();
    initView();
    initActionBar();


  }

  private void initView(){
    mHistoryListView = (ListView) findViewById(R.id.scan_history_list_view);
    tv_floor = (TextView) findViewById(R.id.tv_beacon_floor);
    tv_floor.setText("楼层:"+mScanHistorySerializable.getFloorName());

    tv_userName = (TextView) findViewById(R.id.tv_beacon_user);
    tv_userName.setText("巡检人:"+mScanHistorySerializable.getUserName());

    tv_beaconNum = (TextView) findViewById(R.id.tv_beacon_num);
    int num = mScanHistorySerializable.getBeaconList().size();
    tv_beaconNum.setText("正常/总数:"+(num-mScanHistorySerializable.getAbnormalBeaconNum())+"/"+num);

    tv_time = (TextView) findViewById(R.id.tv_beacon_time);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    Date date = new Date(mScanHistorySerializable.getTime());
    tv_time.setText("时间:"+formatter.format(date));

    //初始化列表
    initBeaconList(mBeaconList);
  }

  private void initData(){
    mPalMap = (PalMap) getIntent().getSerializableExtra(Constant.TAG_MAP_OBJECT);
    mScanHistorySerializable = (ScanHistorySerializable)getIntent().getSerializableExtra(Constant.TAG_SCANHISTORY_OBJECT);
    mBeaconList = mScanHistorySerializable.getBeaconList();
  }

  /*
   * 初始化列表
   * */
  public void initBeaconList(List<Beacon> beaconList){
    if (beaconList == null) return;
    if (mBeaconListAdapter == null){
      mBeaconListAdapter = new BeaconListAdapter(this, beaconList);
      mHistoryListView.setAdapter(mBeaconListAdapter);
    }
  }
  /*
   * 初始化actionBar
   * */
  private void initActionBar() {
    mActionBar = (ActionBar) findViewById(R.id.action_bar);
    mActionBar.setTitle(mPalMap == null ? "巡检记录(空)" : mPalMap.getName());
    mActionBar.setLeftVisible(true);
    mActionBar.setRightBg("继续巡检", 0);



    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
      @Override
      public void onLeft() {
        finishActivityOnAnimation(DetailHistoryActivity.this);
      }

      @Override
      public void onRight() {
        //加载记录继续巡检

        Intent i = new Intent(DetailHistoryActivity.this, MapBeaconActivity.class);
        i.putExtra(Constant.TAG_MAP_OBJECT, mPalMap);
        //判断是否从巡检记录加载beacon,将匹配到的点位变绿
        i.putExtra(Constant.TAG_FROM_HISTORY, true);
        i.putExtra(Constant.TAG_BEACON_HISTORY, mScanHistorySerializable);

        DetailHistoryActivity.this.startActivityOnAnimation(i);

      }
    });
  }
}
