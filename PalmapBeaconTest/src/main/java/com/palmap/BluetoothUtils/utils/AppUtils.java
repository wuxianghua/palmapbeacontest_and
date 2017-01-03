package com.palmap.BluetoothUtils.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by eric3 on 2016/11/24.
 */

public class AppUtils {
  /**
   * 返回当前程序版本名
   */
  public static String getVersionName(Context context) {
    String versionName = "";
    try {
      // ---get the package info---
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      versionName = pi.versionName;
//      versioncode = pi.versionCode;
      if (versionName == null || versionName.length() <= 0) {
        return "";
      }
    } catch (Exception e) {
      LogUtils.e( e.getMessage());
    }
    return versionName;
  }
}
