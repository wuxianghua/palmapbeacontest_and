package com.palmap.BluetoothUtils.http.model;

import android.os.Handler;
import android.os.Looper;

import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;

/**
 * 错误定义类
 */
public class ErrorCode {

  /**
   * 类型转换错误,返回的实际对象与想要得到的不符
   */
  public static final int CODE_CLASS_ERROR = 1001;
  /**
   * JSON类型转换错误，返回的实际对象与想要得到的不符
   */
  public static final int CODE_JSON_ERROR = 1002;

  /**
   * 网络请求错误
   */
  public static final int CODE_REQUEST_ERROR = 1003;

  /**
   * 无网络连接
   */
  public static final int CODE_NO_INTERNET = 1004;

  /*
  * Exception
  * */
  public static final int CODE_EXCEPTION = 1005;

  public static void showError(final int code) {

//        if (Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
//            return;
//        }
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        LogUtils.e("HTTP ErrorCode:" + code);

        if (code == CODE_NO_INTERNET) {
          DialogUtils.showShortToast("无网络连接");
          return;
        }
        if (code == CODE_EXCEPTION) {
          DialogUtils.showShortToast("网络错误");
          return;

        }
        if (code == CODE_REQUEST_ERROR) {
          DialogUtils.showShortToast("请求出错");
          return;

        }
        if (code == 409) {
          DialogUtils.showShortToast("beacon在此地图上冲突（uuid/major/minor）");
          return;

        }


      }
    });


  }

}
