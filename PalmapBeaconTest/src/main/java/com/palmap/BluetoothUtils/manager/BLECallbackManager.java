package com.palmap.BluetoothUtils.manager;

import com.palmap.BluetoothUtils.main.model.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eric3 on 2016/12/6.
 * ble扫描回调管理
 */

public class BLECallbackManager {
  public static final int BLE_CALLBACK_TIME = 2000;//每次BLE扫描回调时间 单位ms
  private static final int MAX_MINOR_NUM = 1000;//每次最大回调设备数

  private List<Beacon> beacons;//存储BLE_CALLBACK_TIME期间扫描到的minor
  private Timer mTimer;
  private TimerTask mTimerTask;
  private OnScanResultListener onScanResultListener;

  public BLECallbackManager(final OnScanResultListener onScanResultListener) {
    this.onScanResultListener = onScanResultListener;
    mTimer = new Timer();
    mTimerTask = new TimerTask() {
      @Override
      public void run() {
//        int[] ms = minors;
        if (beacons!=null&&beacons.size()>0) {
          onScanResultListener.onScanResult(new ArrayList<Beacon>(beacons));//回调更新
          clear();
        }
      }
    };

  }

  public void start(){
    if (mTimer!=null)
    mTimer.schedule(mTimerTask,BLE_CALLBACK_TIME,BLE_CALLBACK_TIME);//启动定时回调
  }
  public void stop(){
    if (mTimer!=null)
      mTimer.cancel();//停止定时回调
  }

  public void add(Beacon b){
    if (beacons==null)
      beacons = new ArrayList<>();

    int power = (int)((b.getPowerPercent()+Integer.MAX_VALUE)/(float)Integer.MAX_VALUE*100);//power  32bit
//    int power = (int)(((b.getPowerPercent()&0x000000ff)*100.0)/0x000000ff);//power 8bit
    b.setPowerPercent(power);
    beacons.add(b);
  }
  /**
  * @Author: eric3
  * @Description: 置空
  * @Time 2016/12/6 17:16
  */
  private void clear(){
    if (beacons!=null)
    beacons.clear();
  }


  public interface OnScanResultListener{
    void onScanResult(List<Beacon> beacons);
  }

}
