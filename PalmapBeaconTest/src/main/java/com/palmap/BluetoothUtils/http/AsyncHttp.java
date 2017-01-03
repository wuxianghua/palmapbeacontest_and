package com.palmap.BluetoothUtils.http;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.utils.PhoneUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by zhang on 2015/10/12.
 */
public class AsyncHttp {
  public static int HTTP_CONNECTION_TIMEOUT = 30; // 单位秒
  private static AsyncHttp instance = null;
  /*
  * 获取单实例对象
  * */
  public static AsyncHttp getInstance(){
    if (instance == null){
      instance = new AsyncHttp();
    }
    return instance;
  }

  /*
  * get数据请求
  * */
  public ClientConnectionManager getRequest(final String url, final Map<String, String> heads, final HttpDataCallBack callback){
    // 如果callback为null，则请求结果无法返回
    if (callback == null){
      return null;
    }

    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)){
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return null;
    }

    // 判断url是否为空
    if (url == null){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return null;
    }
    LogUtils.w( "url: " + url);

    // 创建http连接相关对象
    final HttpClient httpClient = new DefaultHttpClient();

    // 启用工作线进行网络请求
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        // 设置参数
        HttpParams params = httpClient.getParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT * 1000); // 超时连接设置

        HttpGet httpGet = new HttpGet(url);

        // 设置head
        if (heads != null){
          LogUtils.w("添加head中...");
          for (String key : heads.keySet()){
            LogUtils.w("key: " + key);
//            LogUtils.w("value: " + heads.get(key));
            httpGet.addHeader(key, heads.get(key));
          }
        }
        // 执行请求
        InputStream inputStream = null;
        try {
          HttpResponse response = httpClient.execute(httpGet);
          int returnCode = response.getStatusLine().getStatusCode();
          LogUtils.w( "returnCode = " + returnCode);
          if (returnCode == HttpURLConnection.HTTP_OK){
            HttpEntity entity = response.getEntity();
            LogUtils.w( "entity.getContentType().getMapId(): " + entity.getContentType().getValue());
            if (entity != null){
              String contentType = entity.getContentType().getValue();
              if (contentType.contains("application/json")){
                String content = EntityUtils.toString(entity, HTTP.UTF_8);
                callback.onComplete(content);
              } else if (contentType.contains("application/x-protobuf")){
                inputStream = entity.getContent();
                int size = (int) entity.getContentLength();
                LogUtils.w( "size = " + size);
                if (size < 0) {
                  callback.onError(ErrorCode.CODE_EXCEPTION);
                  return;
                }
                byte[] buffer = new byte[size];
                int curr = 0, read;
                while (curr < size){
                  read = inputStream.read(buffer, curr, size - curr);
                  if (read <= 0){
                    break;
                  }
                  curr += read;
                }
                callback.onComplete(buffer);
              } else { // 返回类型不明
                LogUtils.w( "返回数据类型未知！");
                callback.onError(ErrorCode.CODE_REQUEST_ERROR);
              }
            } else {
              callback.onError(ErrorCode.CODE_REQUEST_ERROR);
            }
          } else {
            callback.onError(returnCode);
          }
        } catch (IOException e) {
          e.printStackTrace();
          LogUtils.w( "执行httpClient.execute(httpGet)的异常！");
          callback.onError(ErrorCode.CODE_REQUEST_ERROR);
        } finally {
          httpClient.getConnectionManager().shutdown(); // 关闭请求连接
          if (inputStream != null){
            try {
              inputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

      }
    });

    return httpClient.getConnectionManager();
  }

  /*
  * post数据请求
  * */
  public ClientConnectionManager postRequest(final String url, final Map<String, String> heads, final List<NameValuePair> pairList, final HttpDataCallBack callback){
    // 如果callback为null，则请求结果无法返回
    if (callback == null){
      return null;
    }

    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)){
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return null;
    }

    // 判断url是否为空
    if (url == null){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return null;
    }
    LogUtils.i( "url: " + url);

    // 创建http连接相关对象
    final DefaultHttpClient httpClient = new DefaultHttpClient();

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        // 设置参数
        HttpParams params = httpClient.getParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT * 1000); // 超时连接设置

        HttpPost httpPost = new HttpPost(url);

        // 设置head
        if (heads != null){
          LogUtils.w("添加head中...");
          for (String key : heads.keySet()){
            LogUtils.w( "key: " + key);
//              LogUtils.w( "value: " + heads.get(key));
            httpPost.addHeader(key, heads.get(key));
          }
        }

        InputStream inputStream = null;
        try {

          if (pairList!=null)
          httpPost.setEntity(new UrlEncodedFormEntity(pairList, HTTP.UTF_8));

          HttpResponse response = httpClient.execute(httpPost);
          int returnCode = response.getStatusLine().getStatusCode();
          if (returnCode == HttpURLConnection.HTTP_OK){
            HttpEntity entity = response.getEntity();
            LogUtils.w( "entity.getContentType().getMapId(): " + entity.getContentType().getValue());

            if (entity != null){
              String contentType = entity.getContentType().getValue();
              if (contentType.contains("application/json")){
                String content = EntityUtils.toString(entity, HTTP.UTF_8);
                callback.onComplete(content);
              } else if (contentType.contains("application/x-protobuf")){
                inputStream = entity.getContent();
                int size = (int) entity.getContentLength();
                LogUtils.w( "size = " + size);
                if (size < 0) {
                  callback.onError(ErrorCode.CODE_EXCEPTION);
                  return;
                }
                byte[] buffer = new byte[size];
                int curr = 0, read = 0;
                while (curr < size){
                  read = inputStream.read(buffer, curr, size - curr);
                  if (read <= 0){
                    break;
                  }
                  curr += read;
                }
                callback.onComplete(buffer);
              } else { // 返回类型不明
                LogUtils.w( "返回数据类型未知！");
                callback.onError(ErrorCode.CODE_REQUEST_ERROR);
              }
            } else {
              callback.onError(ErrorCode.CODE_REQUEST_ERROR);
            }
          } else {
            callback.onError(returnCode);
          }

        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
          LogUtils.w( "new UrlEncodedFormEntity(pairList, HTTP.UTF_8)异常！");
          callback.onError(ErrorCode.CODE_REQUEST_ERROR);
        } catch (ClientProtocolException e) {
          e.printStackTrace();
          LogUtils.w( "httpClient.execute(httpPost)异常->ClientProtocolException！");
          callback.onError(ErrorCode.CODE_REQUEST_ERROR);
        } catch (IOException e) {
          e.printStackTrace();
          LogUtils.w( "httpClient.execute(httpPost)异常->IOException！");
          callback.onError(ErrorCode.CODE_REQUEST_ERROR);
        } finally {
          httpClient.getConnectionManager().shutdown();
          if (inputStream != null){
            try {
              inputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

      }
    });

    return httpClient.getConnectionManager();
  }

  /**
   * Delete
   * @param url 发送请求的URL
   *
   */
  public void deleteRequest(final String url, final Map<String, String> heads, final HttpDataCallBack callback){
    // 如果callback为null，则请求结果无法返回
    if (callback == null){
      return;
    }

    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)){
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    // 判断url是否为空
    if (url == null){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return;
    }

    LogUtils.i( "url: " + url);

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        try {
          URL httpUrl = new URL(url);
          HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
          urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT * 1000);
          urlConnection.setRequestMethod("DELETE");
          urlConnection.setDoInput(true); // 读取数据
          urlConnection.setDoOutput(false); // 向服务器写数据

          // 添加请求属性
          if (heads != null && !heads.isEmpty()){
            for (Object key : heads.keySet()){
              urlConnection.setRequestProperty(key.toString(), heads.get(key));
            }
          }
//          urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
//          LogUtils.w( "data.length = " + data.length);

//          if (data!=null) {
//            // 获得输出流，向服务器输出内容
//            OutputStream outputStream = urlConnection.getOutputStream();
//            outputStream.write(data, 0, data.length);
//            outputStream.flush();
//            outputStream.close();
//          }

          // 获得服务器响应结果和状态码
          int responseCode = urlConnection.getResponseCode();
          LogUtils.w( "sendData->responseCode = " + responseCode);
          if (responseCode == 200 || responseCode == 204 || responseCode == 201){//现在反204
            InputStream inputStream = urlConnection.getInputStream();
            String result = changeInputStream(inputStream, HTTP.UTF_8);
            inputStream.close();
            callback.onComplete(result);
          } else {
            callback.onError(responseCode);
          }

        } catch (MalformedURLException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        } catch (IOException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        }
      }
    });
  }
  /*
  * post请求，发送数据
  * */
  public void sendDataByPost(final String url, final byte[] data, final Map<String, String> heads, final HttpDataCallBack callback){
    // 如果callback为null，则请求结果无法返回
    if (callback == null){
      return;
    }

    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)){
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    // 判断url是否为空
    if (url == null){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return;
    }

    // 判断数据是否为空
    if (data == null || data.length <= 0){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return;
    }
    LogUtils.i( "url: " + url);

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        try {
          URL httpUrl = new URL(url);
          HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
          urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT * 1000);
          urlConnection.setRequestMethod("POST");
          urlConnection.setDoInput(true); // 读取数据
          urlConnection.setDoOutput(true); // 向服务器写数据

          // 添加请求属性
          if (heads != null && !heads.isEmpty()){
            for (Object key : heads.keySet()){
              urlConnection.setRequestProperty(key.toString(), heads.get(key));
            }
          }
//          urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
//          LogUtils.w( "data.length = " + data.length);


            // 获得输出流，向服务器输出内容
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(data, 0, data.length);
            outputStream.flush();
            outputStream.close();


          // 获得服务器响应结果和状态码
          int responseCode = urlConnection.getResponseCode();
          LogUtils.w( "sendData->responseCode = " + responseCode);
          if (responseCode == 200 || responseCode == 204 || responseCode == 201){//现在反204
            InputStream inputStream = urlConnection.getInputStream();
            String result = changeInputStream(inputStream, HTTP.UTF_8);
            inputStream.close();
            callback.onComplete(result);
          } else {
            callback.onError(responseCode);
          }

        } catch (MalformedURLException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        } catch (IOException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        }
      }
    });

  }

  /*
  * post请求，发送数据
  * */
  public void sendDataByPut(final String url, final byte[] data, final Map<String, String> heads, final HttpDataCallBack callback){
    // 如果callback为null，则请求结果无法返回
    if (callback == null){
      return;
    }

    // 检测网络是否连接
    if (!PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)){
      callback.onError(ErrorCode.CODE_NO_INTERNET); // 无网络连接
      return;
    }

    // 判断url是否为空
    if (url == null){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return;
    }

    // 判断数据是否为空
    if (data == null || data.length <= 0){
      callback.onError(ErrorCode.CODE_REQUEST_ERROR); // 网络请求错误
      return;
    }
    LogUtils.i( "url: " + url);

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        try {
          URL httpUrl = new URL(url);
          HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
          urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT * 1000);
          urlConnection.setRequestMethod("PUT");
          urlConnection.setDoInput(true); // 读取数据
          urlConnection.setDoOutput(true); // 向服务器写数据

          // 添加请求属性
          if (heads != null && !heads.isEmpty()){
            for (Object key : heads.keySet()){
              urlConnection.setRequestProperty(key.toString(), heads.get(key));
            }
          }
//          urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
//          LogUtils.w( "data.length = " + data.length);


          // 获得输出流，向服务器输出内容
          OutputStream outputStream = urlConnection.getOutputStream();
          outputStream.write(data, 0, data.length);
          outputStream.flush();
          outputStream.close();


          // 获得服务器响应结果和状态码
          int responseCode = urlConnection.getResponseCode();
          LogUtils.w( "sendData->responseCode = " + responseCode);
          if (responseCode == 200 || responseCode == 204 || responseCode == 201){//现在反204
            InputStream inputStream = urlConnection.getInputStream();
            String result = changeInputStream(inputStream, HTTP.UTF_8);
            inputStream.close();
            callback.onComplete(result);
          } else {
            callback.onError(responseCode);
          }

        } catch (MalformedURLException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        } catch (IOException e) {
          e.printStackTrace();
          callback.onComplete(ErrorCode.CODE_EXCEPTION);
        }
      }
    });

  }

  /*
  * 将一个输入流转换成指定编码的字符串
  * */
  private String changeInputStream(InputStream inputStream, String encode){
    String result = null;
    if (inputStream != null){
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int len = 0;
      try {
        while ((len = inputStream.read(buffer)) != -1){
          byteArrayOutputStream.write(buffer, 0, len);
        }
        result = new String(byteArrayOutputStream.toByteArray(), encode);
        byteArrayOutputStream.close(); // 关流
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
