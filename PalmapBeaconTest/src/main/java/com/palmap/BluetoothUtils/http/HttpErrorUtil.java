package com.palmap.BluetoothUtils.http;

import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.utils.DialogUtils;

/**
 * Created by eric3 on 2016/9/23.
 */
public class HttpErrorUtil {

  public static void showErrorToast(int errorCode){
    switch (errorCode){
      case 500:
        DialogUtils.showLongToast("服务器错误，请稍后重试。");
        break;
      case ErrorCode.CODE_NO_INTERNET:
        DialogUtils.showLongToast("无网络连接，请连接网络。");
        break;
      default:
        DialogUtils.showLongToast("未知网络错误！errorCode:"+errorCode);
        break;

    }
  }
}
