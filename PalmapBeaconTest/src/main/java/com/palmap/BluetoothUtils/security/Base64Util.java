package com.palmap.BluetoothUtils.security;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by eric3 on 2016/10/14.
 */

public class Base64Util {
  /**
   * @param bytes
   * @return
   */
  public static byte[] decode(final byte[] bytes) {
//    return Base64.decode(bytes) ;
    return Base64.decodeBase64(bytes);
  }

  /**
   * 二进制数据编码为BASE64字符串
   *
   * @param bytes
   * @return
   * @throws Exception
   */
  public static String encode(final byte[] bytes) {
    return new String(Base64.encodeBase64(bytes));
  }
}
