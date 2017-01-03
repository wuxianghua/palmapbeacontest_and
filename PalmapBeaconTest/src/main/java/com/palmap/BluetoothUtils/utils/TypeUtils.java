package com.palmap.BluetoothUtils.utils;

/**
 * Created by zhangyf on 2016/7/14.
 */
public class TypeUtils {

  /**
   * Convert byte[] to hex string
   *
   * @param src
   * @return
   */
  public static String bytesToHexString(byte[] src){
    StringBuilder stringBuilder = new StringBuilder("");
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

}
