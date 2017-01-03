package com.palmap.BluetoothUtils.map.service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import com.palmap.BluetoothUtils.main.model.ScanParameters;
import com.palmap.BluetoothUtils.manager.BLECallbackManager;
import com.palmap.BluetoothUtils.map.fragment.MapFragment;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmaplus.nagrand.position.ble.Beacon;
import com.palmaplus.nagrand.position.ble.BeaconUtils;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhang on 2016/7/13.
 */
public class BLEController {
  BLECallbackManager bleCallbackManager;
  /**
   * Represents the local device Bluetooth adapter
   */
  private BluetoothAdapter bluetoothAdapter;

  /**
   * beacon扫描回调
   */
  private BluetoothAdapter.LeScanCallback leScanCallback;

  /**
   * 扫描周期
   */
  private static final long SCAN_PERIOD = 5000;

  /**
   * 被扫描到的beacon
   */
  private Map<Integer, Beacon> scanBeaconMap;

  /**
   * 计时器
   */
  private Timer timer;

  /**
   * 计时任务
   * 用于检测更新beacon的扫中状态
   */
  private TimerTask task;

  /**
   * 是否在扫描中
   */
  private volatile boolean isScanning;

  /**
   * 地图界面
   */
  private MapFragment mapFragment;

  private Handler handler;

  private final String TAG = "BLEController";


  /**
   * @param mapFragment
   */
  public BLEController(final MapFragment mapFragment) {
    handler = new Handler(Looper.getMainLooper());
    scanBeaconMap = new ConcurrentHashMap<Integer, Beacon>();
    this.mapFragment = mapFragment;
  }

  /**
   * 开始扫描周围蓝牙设备
   *
   * @param activity
   * @return
   */
  public boolean start(final Activity activity, final ScanParameters scanParameters) {
    if (bluetoothAdapter != null && leScanCallback != null) {
      bluetoothAdapter.stopLeScan(leScanCallback);
      bluetoothAdapter = null;
    }

    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    if (bluetoothAdapter == null) {
      DialogUtils.showShortToast("bluetooth4.0 is not supported!");
      return false;
    }

    if (scanParameters==null||scanParameters.getItemList()==null||scanParameters.getItemList().size()<=0){
      return false;
    }
    mapFragment.updateBeaconText("Search for<br>"+scanParameters);

    bleCallbackManager = new BLECallbackManager(new BLECallbackManager.OnScanResultListener() {
      @Override
      public void onScanResult(List<com.palmap.BluetoothUtils.main.model.Beacon> beacons) {
        mapFragment.updateBeaconOverlay(beacons);
      }
    });
    bleCallbackManager.start();

    leScanCallback = new BluetoothAdapter.LeScanCallback() {

      @Override
      public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        if (!isScanning) return; // 正在更新界面

        final Beacon beacon = BeaconUtils.beaconFromLeScan(device, rssi, scanRecord);
        if (beacon == null || beacon.getProximityUUID() == null)
          return;

//        LogUtils.i("power:" + beacon.getMeasuredPower() + "  minor:" + beacon.getMinor() + "  major:" + beacon.getMajor() +"   name:" + beacon.getName() +  "\nUUID:" + beacon.getProximityUUID());
        LogUtils.i("power:" + ((int)((beacon.getMeasuredPower()+Integer.MAX_VALUE)/(float)Integer.MAX_VALUE*100)) + "%  minor:" + beacon.getMinor() + "  major:" + beacon.getMajor() +"   name:" + beacon.getName() +  "\nUUID:" + beacon.getProximityUUID());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            mapFragment.scanCount++;
            long time = System.currentTimeMillis();
            long during = time - mapFragment.lastTimeScaned;
            mapFragment.lastTimeScaned = time;
            mapFragment.mScanInfo.setText("扫描:" + mapFragment.scanCount + " 匹配:" +mapFragment.getmBeaconManager().getActiveBeaconNum() + " 扫描间隔:" + during + "ms");

            if (scanParameters.contains(beacon.getProximityUUID(),beacon.getMajor())) {
              mapFragment.updateBeaconText("<font color='#FFFF00'>MATCH: minor=" + beacon.getMinor() + "  major=" + beacon.getMajor() + "<br>UUID=" + beacon.getProximityUUID().toUpperCase() + "</font><br><br>");

//              mapFragment.updateBeaconOverlay(beacon.getMinor());

              bleCallbackManager.add(new com.palmap.BluetoothUtils.main.model.Beacon(beacon.getName(),beacon.getMeasuredPower(),beacon.getProximityUUID().toUpperCase(),beacon.getMajor(),beacon.getMinor()));


            } else {
              mapFragment.updateBeaconText("MISS: minor=" + beacon.getMinor() + "  major=" + beacon.getMajor() + "<br>UUID=" + beacon.getProximityUUID().toUpperCase() + "<br><br>");
            }
          }
        });
      }//onle
    };

    isScanning = true;
    boolean code = bluetoothAdapter.startLeScan(leScanCallback);

    return code;
  }

  /**
   * @return
   */
  public void stop() {
    if (bleCallbackManager != null)
      bleCallbackManager.stop();

    if (bluetoothAdapter != null && leScanCallback != null) {
      bluetoothAdapter.stopLeScan(leScanCallback);
    }
    mapFragment.scanCount = 0;
    isScanning = false;
    leScanCallback = null;
    bluetoothAdapter = null;
  }

  /**
   * 开始或停止扫描
   *
   * @param enable
   */
  private void scanLeDevice(final boolean enable) {
    if (enable) {
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          isScanning = false;
          bluetoothAdapter.stopLeScan(leScanCallback);
        }
      }, SCAN_PERIOD);

      isScanning = true;
      boolean result = bluetoothAdapter.startLeScan(leScanCallback);
      LogUtils.w("startLeScan result: " + result);
    } else {
      isScanning = false;
      bluetoothAdapter.stopLeScan(leScanCallback);
    }
  }

  public boolean isScanning() {
    return isScanning;
  }
}
