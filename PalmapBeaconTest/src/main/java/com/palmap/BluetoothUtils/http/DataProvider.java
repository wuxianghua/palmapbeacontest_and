package com.palmap.BluetoothUtils.http;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;

import com.palmap.BluetoothUtils.http.model.ErrorCode;

import okhttp3.Headers;

/**
 * Created by zhang on 2015/10/12.
 * 将接口和具体请求分离，使回调工作在主线程
 */
public class DataProvider {
  private Handler mHandler = new Handler(Looper.getMainLooper());

  /*
  * get请求
  * */
  public void getProvider(final String url, final Headers headers, final HttpDataCallBack<Object> callBack) {
    OkHttpUtils.get(url, headers, new HttpDataCallBack() {
      @Override
      public void onError(final int errorCode) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
             ErrorCode.showError(errorCode);
             callBack.onError(errorCode);
          }
        });
      }

      @Override
      public void onComplete(final Object content) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            callBack.onComplete(content);
          }
        });
      }
    });



  }

  /*
  * post请求
  * */
  public void postProvider(final String url, final ContentValues contentValues, final HttpDataCallBack callBack) {
    OkHttpUtils.postKeyValue(url, contentValues, new HttpDataCallBack() {
      @Override
      public void onError(final int errorCode) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            ErrorCode.showError(errorCode);
            callBack.onError(errorCode);
          }
        });
      }

      @Override
      public void onComplete(final Object content) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            callBack.onComplete(content);
          }
        });
      }


    });
  }

  /*
  * post请求传递数据
  * */
  public void postDataProvider(final String url, final Headers headers, final String jsonData, final HttpDataCallBack callBack) {

        OkHttpUtils.postJson(url, headers, jsonData, new HttpDataCallBack() {
          @Override
          public void onError(final int errorCode) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                ErrorCode.showError(errorCode);
                callBack.onError(errorCode);
              }
            });
          }

          @Override
          public void onComplete(final Object content) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                callBack.onComplete(content);
              }
            });
          }


        });




  }

  /*
  * put请求传递数据
  * */
  public void putDataProvider(final String url, final Headers headers, final String jsonData, final HttpDataCallBack callBack) {

        OkHttpUtils.put(url, headers, jsonData, new HttpDataCallBack() {
          @Override
          public void onError(final int errorCode) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                ErrorCode.showError(errorCode);
                callBack.onError(errorCode);
              }
            });
          }

          @Override
          public void onComplete(final Object content) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                callBack.onComplete(content);
              }
            });
          }


        });


  }

  /*
  * delete请求
  * */
  public void deleteProvider(final String url, final Headers headers, final HttpDataCallBack callBack) {

        OkHttpUtils.delete(url, headers, new HttpDataCallBack() {
          @Override
          public void onError(final int errorCode) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                ErrorCode.showError(errorCode);
                callBack.onError(errorCode);
              }
            });
          }

          @Override
          public void onComplete(final Object content) {
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                callBack.onComplete(content);
              }
            });
          }


        });


  }

}
