package com.palmap.BluetoothUtils.test;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.main.protobuf.BeaconsOfMapProtobuf;
import com.palmap.BluetoothUtils.utils.LogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * Created by zhang on 2015/10/16.
 */
public class HttpTest {

  public static void getContacts(final String url){

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        InputStream in = null;
        try {
          URL target = new URL(url);
          LogUtils.e("HttpTest->url: " + url);
          HttpURLConnection conn = (HttpURLConnection) target.openConnection();
          conn.setDoOutput(true);
          conn.setDoInput(true);
          conn.setRequestMethod("GET");
          conn.setRequestProperty("Content-Type", "application/x-protobuf");
          conn.setRequestProperty("Accept", "application/x-protobuf");
          conn.setRequestProperty("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken);
          conn.connect();
          // check response code
          int code = conn.getResponseCode();
          LogUtils.e("code = " + code);
          boolean success = (code >= 200) && (code < 300);
          in = success ? conn.getInputStream() : conn.getErrorStream();

          int size = conn.getContentLength();

          byte[] response = new byte[size];
          int curr = 0, read = 0;

          while (curr < size) {
            read = in.read(response, curr, size - curr);
            if (read <= 0) break;
            curr += read;
          }

          String result = new String(response);
          LogUtils.e( "result: " + result);
        } catch (IOException e){
          e.printStackTrace();
        } finally {
          if(in != null){
            try {
              in.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });

}

  public static void getContacts_2(final String url){

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        //构造HttpClient的实例
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/x-protobuf");
        httpGet.setHeader("Content-Type", "application/x-protobuf");
        httpGet.setHeader("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken);
        InputStream in = null;
        try {
          HttpResponse response = httpClient.execute(httpGet);
          int code = response.getStatusLine().getStatusCode();
          LogUtils.e( "code: " + code);
          boolean success = (code >= 200) && (code < 300);
          in = response.getEntity().getContent();

          int size = (int) response.getEntity().getContentLength();

          byte[] buf = new byte[size];
          int curr = 0, read = 0;

          while (curr < size) {
            read = in.read(buf, curr, size - curr);
            if (read <= 0) break;
            curr += read;
          }

          BeaconsOfMapProtobuf.BeaconsOfMap beaconsOfMap = BeaconsOfMapProtobuf.BeaconsOfMap.parseFrom(buf);
          LogUtils.e("beaconsOfMap: " + beaconsOfMap.toString());

          LogUtils.e("----------------------");
          LogUtils.e( "beaconsOfMap.getSceneId(): " + beaconsOfMap.getSceneId());
          LogUtils.e( "beaconsOfMap.getGroupsCount(): " + beaconsOfMap.getGroupsCount());
          LogUtils.e( "beaconsOfMap.getGroups(0).toString(): " + beaconsOfMap.getGroups(0).toString());
          LogUtils.e( "beaconsOfMap.getGroups(0).getBeaconsByID(0): " + beaconsOfMap.getGroups(0).getBeacons(0));

        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if(in != null){
            try {
              in.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });

  }

}
