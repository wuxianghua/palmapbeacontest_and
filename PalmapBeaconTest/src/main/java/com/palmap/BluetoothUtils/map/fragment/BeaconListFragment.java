package com.palmap.BluetoothUtils.map.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.manager.BeaconManager;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
import com.palmap.BluetoothUtils.map.adapter.BeaconListAdapter;
import com.palmap.BluetoothUtils.utils.LogUtils;

import java.util.List;

/**
 * Created by zhang on 2015/10/15.
 */
public class BeaconListFragment extends Fragment {
  private ListView mBeaconListView;
  public BeaconListAdapter mBeaconListAdapter = null;
  private TextView tv_beaconAll;//当前楼层Beacon数量
  private TextView tv_beaconAbnormal;//异常Beacon数量
  private TextView tv_beaconLowPower;//低电量Beacon数量
//  private MapBeaconActivity mActivity;
  private BeaconManager mBeaconManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_beacon_list, container, false);
    mBeaconListView = (ListView) fragmentView.findViewById(R.id.fragment_beacon_list_view);
    tv_beaconAbnormal = (TextView) fragmentView.findViewById(R.id.tv_beacon_abnormal);
    tv_beaconLowPower = (TextView) fragmentView.findViewById(R.id.tv_beacon_lowpower);
    tv_beaconAll = (TextView) fragmentView.findViewById(R.id.tv_beacon_all);
    mBeaconManager = ((MapBeaconActivity)getActivity()).getmBeaconManager();

    return fragmentView;
  }

  public void updateBeaconNum(){
//    if (tv_beaconAbnormal ==null)
//      return;

    tv_beaconAbnormal.setText(mBeaconManager.getAbnormalBeaconNum());
    tv_beaconLowPower.setText("待巡检");
    tv_beaconAll.setText(""+mBeaconManager.getBeaconNum());
  }


  /*
    * 初始化beacon列表
    * */
  public void initBeaconList(List<Beacon> beaconList){
    if (beaconList == null) return;
    if ( mBeaconListView==null){
      LogUtils.e("mBeaconListView = null");
      return;
    }
    updateBeaconNum();
    if (mBeaconListAdapter == null ){
      mBeaconListAdapter = new BeaconListAdapter(getActivity(), beaconList);
      mBeaconListView.setAdapter(mBeaconListAdapter);
    }
  }


  /*
  * 显示select框
  * */
  public void showSelectBox(boolean isShow){
    if (mBeaconListAdapter != null){
      mBeaconListAdapter.showSelectBox(isShow);
    }
  }

}
