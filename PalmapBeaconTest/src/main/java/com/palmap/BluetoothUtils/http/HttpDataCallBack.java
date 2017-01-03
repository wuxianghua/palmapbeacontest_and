package com.palmap.BluetoothUtils.http;

/**
 * Created by zhang on 2015/10/12.
 * http数据回调
 */
public interface HttpDataCallBack<T> {

  /**
   * 获取数据出错
   * @param errorCode 错误类型
   */
  void onError(int errorCode);

  /**
   * 数据请求成功
   */
  void onComplete(T content);

}
