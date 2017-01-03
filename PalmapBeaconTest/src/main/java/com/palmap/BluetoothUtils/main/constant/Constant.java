package com.palmap.BluetoothUtils.main.constant;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.database.SQLiteHelper;

/**
 * Created by zhang on 2015/10/12.
 */
public class Constant {

    public static final String APP_CONFIG = "bluetoothApp";
  public static final String TAG_ACCESS_TOKEN = "access_token";
//  public static String TAG_APPKEY = "appKey";
  public static String TAG_MAP_OBJECT = "mapObject";
  public static String TAG_HAS_DOWNLOAD = "hasDownload";
  public static String TAG_FROM_HISTORY = "fromHistory";
  public static String TAG_BEACON_HISTORY = "beaconHistory";
  public static String TAG_SCANHISTORY_OBJECT = "scanHistoryObject";

  public static final String LUR_NAME = "Nagrand/lua";
  public static final String OFFLINE_DATA_PATH = BluetoothUtilsApplication.instance.getExternalFilesDir(null)+"";
  public static final String OFFLINE_DATABASE_PATH = BluetoothUtilsApplication.instance.getDatabasePath(SQLiteHelper.DATABASE_NAME)+"";


  //  public static final String OFFLINE_DATA_PATH = Environment.getExternalStorageDirectory() +  "/palmapDownload/";
//  public static String SERVER_URL = "http://211.161.101.67/nagrand-service/";
  public static String SERVER_URL = "http://api.ipalmap.com/";


  //测试参数，控制自动填充账号密码
  public static Boolean isDebug = true;

  //bellnet专用，免登陆，改启动图标
  public static Boolean isBellnet = false;

  //非凡专用，免登陆
  public static Boolean isFeiFan = false;


}
