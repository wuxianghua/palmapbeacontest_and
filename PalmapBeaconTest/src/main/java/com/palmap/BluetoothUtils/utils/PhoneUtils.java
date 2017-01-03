package com.palmap.BluetoothUtils.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

/**
 * Created by zhang on 2015/10/12.
 */
public class PhoneUtils {
  private static final int MB = 1024 * 1024;

  /*
  *  获取Android手机唯一标示
  * */
  public static String getDeviceId(Context context){
    TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return manager.getDeviceId(); // permission#READ_PHONE_STATE
  }

  /*
  *  检测是否有网络连接
  * */
  public static boolean isNetWorkConnected(Context context){
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo(); // permission#ACCESS_NETWORK_STATE
    return networkInfo != null && networkInfo.isAvailable();
  }

  /*
  *  得到存储卡绝对路径
  * */
  public static String getStorePath(){
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)){
      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    return null;
  }

  /*
  * 计算sdCard上的剩余空间
  * */
  public static int freeSpaceOnSdCard(){
    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    double sdFreeMB = (double)stat.getAvailableBlocks() * (double)stat.getBlockSize() / MB;
    return (int) sdFreeMB;
  }

}
