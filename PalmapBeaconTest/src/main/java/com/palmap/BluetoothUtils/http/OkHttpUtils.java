package com.palmap.BluetoothUtils.http;

import android.content.ContentValues;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.utils.PhoneUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eric3 on 2016/11/28.
 */

public class OkHttpUtils {
  private final static long CONNECT_TIMEOUT = 60;
  private static OkHttpClient mOkHttpClient;
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final MediaType X_FORM_URL = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

  private OkHttpUtils(){}
  private static OkHttpClient getInstence() {
    if (mOkHttpClient == null) {

      if (Constant.isDebug == true) {
        mOkHttpClient = new OkHttpClient().newBuilder().addNetworkInterceptor(new StethoInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
      }else {
        mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
      }
    }
    return mOkHttpClient;
  }
  private boolean handleResponse(Response response){
    int resCode = response.code();
    if (resCode == 200 ||resCode == 201||resCode==204)
      return true;
    else
      return false;
  }

  public static void get(String url, Headers headers, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }


    final Request request = new Request.Builder()
        .headers(headers)
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 200||resCode == 201||resCode==204)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }

  public static void postJson(String url, Headers headers, String jsonData, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    RequestBody requestBodyJson = RequestBody.create(JSON, jsonData);
    final Request request = new Request.Builder()
        .headers(headers)
        .post(requestBodyJson)
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 200 ||resCode == 201||resCode==204)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }

  public static void postKeyValue(String url, ContentValues data, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    Headers headers = new Headers.Builder()
        .build();

//    RequestBody requestBodyXForm = RequestBody.create(X_FORM_URL,data.toString());
    RequestBody requestBody = new FormBody.Builder()
        .add(HttpParam.LOGIN_USERNAME_KEY, data.get(HttpParam.LOGIN_USERNAME_KEY).toString())
        .add(HttpParam.LOGIN_PASSWORD_KEY, data.get(HttpParam.LOGIN_PASSWORD_KEY).toString())
        .add(HttpParam.LOGIN_CLIENT_ID_KEY, data.get(HttpParam.LOGIN_CLIENT_ID_KEY).toString())
        .add(HttpParam.LOGIN_GRANT_TYPE_KEY, data.get(HttpParam.LOGIN_GRANT_TYPE_KEY).toString())
        .build();
    final Request request = new Request.Builder()
        .headers(headers)
        .post(requestBody)
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 200)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }

  public static void postFormData(String url, File file, String jsonData, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .build();

    RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", "temp.png", fileBody)
        .addFormDataPart("text", jsonData)
        .build();

    final Request request = new Request.Builder()
        .headers(headers)
        .post(requestBody)
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 200)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }


  public static void put(String url, Headers headers,String data, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }
    RequestBody requestBodyJson = RequestBody.create(JSON, data);
    final Request request = new Request.Builder()
        .headers(headers)
        .put(requestBodyJson)
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 200||resCode == 201||resCode==204)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }

  public static void delete(String url, Headers headers, final HttpDataCallBack callback) {
    if (!isConnNet()) {
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    final Request request = new Request.Builder()
        .headers(headers)
        .delete()
        .url(url)
        .build();

    getInstence().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

        callback.onError(ErrorCode.CODE_EXCEPTION);

        if (e != null)
          LogUtils.e(e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        int resCode = response.code();
        if (resCode == 204)
          callback.onComplete(response.body().string());
        else
          callback.onError(response.code());
      }
    });
  }


  // 检测网络是否连接
  private static boolean isConnNet() {
    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)) {
      return false;
    }
    return true;
  }

}
