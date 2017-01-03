package com.palmap.BluetoothUtils.http;

import android.content.ContentValues;

import com.alibaba.fastjson.JSON;
import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.http.jsonclass.BeaconAddJson;
import com.palmap.BluetoothUtils.http.jsonclass.BeaconUpdateJson;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * Created by zhang on 2015/10/12.
 * 网络数据请求中心
 */
public class DataProviderCenter {
  // 服务器地址
//  private static final String URL_API_SERVER = "https://172.16.10.24/v2"; // 内网
  private static final String URL_API_SERVER = "https://account.ipalmap.com/v2"; // 线上
//  private static final String URL_API_SERVER_2 = "https://www.ipalmap.com/v2"; // 线上
  private static final String URL_API_SERVER_2 = "https://123.59.132.201/v2"; // 线上

  private static final String URL_API_SERVER_3 = "https://www.ipalmap.com/v1"; // 线上

  private static final String URL_APIV2_SERVER = "https://www.ipalmap.com/apiv2"; // 线上最新
//  private static final String URL_API_SERVER_NEW = "http://10.0.23.23/apiv2"; // 最新
// 登陆验证
//  private static final String URL_API_LOGIN = "https://172.16.10.24/auth/realms/master/protocol/openid-connect/token";
  private static final String URL_API_LOGIN = "https://account.ipalmap.com/auth/realms/master/protocol/openid-connect/token";
  // 获取场景列表
  private static final String URL_API_SCENE = URL_API_SERVER_3 + "/scenes";
  // 省
  private static final String URL_API_PROVINCE = URL_API_SERVER_2 + "/provinces";
  // 市
  private static final String URL_API_CITY = URL_API_SERVER_2 + "/cities";
  // 区
  private static final String URL_API_REGION = URL_API_SERVER_2 + "/regions";
  // 获取商场
  private static final String URL_API_MALL = URL_API_SERVER_2 + "/maps/ble";
  // 获取楼层列表
  private static final String URL_API_FLOOR = URL_API_SERVER_2 + "/maps/floor";
  // 获取beacon点位信息
  private static final String URL_API_GET_BEACON = URL_API_SERVER_2 + "/scene/ble/beacons";
  // 提交beacon点位
  private static final String URL_API_PUT_BEACON = URL_API_SERVER_2 + "/scene/ble/beacons";
  // 获取beacon点位信息（接口2）
  private static final String URL_API_GET_BEACON_2 = URL_API_SERVER_2 + "/beacons";
  // 获取楼层列表2
  private static final String URL_APIV2_FLOOR = URL_APIV2_SERVER + "/maps/";
  //打点,删除，更新
  private static final String URL_APIV2_BEACON = URL_APIV2_SERVER + "/scenes/";
  // 获取场景列表
  private static final String URL_APIV2_SCENE = URL_APIV2_SERVER + "/scenes";
  // 省
  private static final String URL_APIV2_PROVINCE = URL_APIV2_SERVER + "/maps/regions?level=1";
  // 市
  private static final String URL_APIV2_CITY = URL_APIV2_SERVER + "/maps/regions?level=2";
  // 区
  private static final String URL_APIV2_REGION = URL_APIV2_SERVER + "/maps/regions?level=3";
  // 获取商场
  private static final String URL_APIV2_MAP = URL_APIV2_SERVER + "/maps";


  private static DataProviderCenter instance = null;
  private DataProvider mDataProvider;

  private DataProviderCenter(){
    mDataProvider = new DataProvider();
  }

  /*
  * 获取单实例对象
  * */
  public static DataProviderCenter getInstance(){
    if (instance == null){
      instance = new DataProviderCenter();
    }
    return instance;
  }


  /*
  * 接口：登陆验证
  * */
  public void login(String userName, String pwd, HttpDataCallBack callBack){
    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
    pairList.add(new BasicNameValuePair("username", userName));
    pairList.add(new BasicNameValuePair("password", pwd));
    pairList.add(new BasicNameValuePair("client_id","palmap_open"));
    pairList.add(new BasicNameValuePair("grant_type","password"));

    ContentValues contentValues = new ContentValues();
    contentValues.put(HttpParam.LOGIN_USERNAME_KEY, userName);
    contentValues.put(HttpParam.LOGIN_PASSWORD_KEY, pwd);
    contentValues.put(HttpParam.LOGIN_CLIENT_ID_KEY,HttpParam.LOGIN_CLIENT_ID_VALUE);
    contentValues.put(HttpParam.LOGIN_GRANT_TYPE_KEY,HttpParam.LOGIN_GRANT_TYPE_VALUE);

    mDataProvider.postProvider(URL_API_LOGIN, contentValues,callBack);
  }

  /*
  * 接口：获取场景信息
  * @param userName 用户名
  * @param keyWord 关键字 没有为null
  * */
  public void getScenes(String userName, String keyWord, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();

    String url;
    url = URL_APIV2_SCENE;

    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取省数据
  * @param sceneId 场景ID
  * */
  public void getProvinces(long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_APIV2_PROVINCE;

    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取城市列表
  * @param provinceCode 省编码
  * @param sceneId 场景ID
  * */
  public void getCities(String provinceCode, long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_API_CITY + "?provinceCode=" + provinceCode + "&sceneId=" + sceneId;
    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取地区列表
  * @param cityCode 城市编码
  * @param sceneId 场景ID
  * */
  public void getRegions(String cityCode, long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_API_REGION + "?cityCode=" + cityCode + "&sceneId=" + sceneId;
    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取商场列表
  * @param regionCode 区域编码
  * @param sceneId 场景ID
  * */
  public void getMalls(String regionCode, long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_API_MALL + "?regionCode=" + regionCode + "&sceneId=" + sceneId;
    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
   * 接口：获取地图列表apiv2
   * @param
   * @param sceneId 场景ID
   * */
  public void getMaps(long sceneID,HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url =  URL_APIV2_SERVER + "/scenes/"+sceneID+"/maps?limit=10000";
    mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取楼层列表
  * @param mallId 商场ID
  * */
  public void getFloors(long mallId, HttpDataCallBack callBack){

    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
      String url = URL_API_FLOOR + "?mapId=" + mallId + "&type=1";
//    String url = URL_API_FLOOR_NEW  + mallId + "/floors";
      mDataProvider.getProvider(url, headers, callBack);
  }

  /*
  * 接口：获取beacon数据库
  * @param mallId 商场ID
  * @param sceneId 场景ID
  * */
  public void getBeacons(long mallId, long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_API_GET_BEACON + "?mapId=" + mallId + "&sceneId=" + sceneId;
    mDataProvider.getProvider(url, headers, callBack);
  }

  public void getBeaconsAPIV2(long mapId, long sceneId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_APIV2_BEACON + sceneId + "/maps/" + mapId + "/beacons?type=0&limit=10000";
    mDataProvider.getProvider(url, headers, callBack);
  }

  public void getFloorsAPIV2(long mapId, HttpDataCallBack callBack){
    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    String url = URL_APIV2_SERVER + "/maps/" + mapId + "/floors";
    mDataProvider.getProvider(url, headers, callBack);
  }

//  /*
//  * 老接口：用post方法上传beacon数据
//  * @param data beacon数据
//  * */
//  public void sendBeacons(byte[] data, HttpDataCallBack callBack){
//    Map<String, String> heads = new HashMap<String, String>();
//    heads.put("Accept", "application/x-protobuf");
//    heads.put("Content-Type", "application/x-protobuf");
//    heads.put("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken);
//    mDataProvider.postDataProvider(URL_API_PUT_BEACON, heads, data, callBack);
//  }

  /*
* 接口：用post方法上传多个beacon数据APIV2
* @param data beacon数据
* */
  public void sendBeaconsAPIV2(final MapBeaconActivity context,long mapID, long sceneID, List<Beacon> beaconList, final HttpDataCallBack callBack, OnUpdateProcess onUpdateProcess){
    if(beaconList == null)
      return;
    Beacon beacon = null;
    int m = beaconList.size();
    for(int i=0;i<beaconList.size();i++) {
      beacon = beaconList.get(i);
      onUpdateProcess.onUpdate(i/m);
      final Beacon finalBeacon = beacon;
      sendBeaconAPIV2(mapID,sceneID,beacon,new HttpDataCallBack() {
        @Override
        public void onError(int errorCode) {
          callBack.onError(errorCode);
        }

        @Override
        public void onComplete(Object content) {
          int oldBeaconID =0;
          try {
            JSONObject object = new JSONObject(content.toString());
            int beaconID = object.getInt("beaconId");
            oldBeaconID = finalBeacon.getId();
            finalBeacon.setId(beaconID);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          context.getmBeaconManager().setActionNo(finalBeacon,oldBeaconID);
        }
      });
    }//for

    callBack.onComplete(null);
  }

  public interface OnUpdateProcess{
    void onUpdate(int percent);
  }
  /*
  * 接口：用post方法上传单个beacon数据APIV2
  * @param data beacon数据
  * */
  public void sendBeaconAPIV2(long mapID,long sceneID, Beacon beacon, HttpDataCallBack callBack){
    String jsonString = null;

    switch (beacon.getAction()){
      case Beacon.ACTION_ADD:
        BeaconAddJson beaconAddJson = new BeaconAddJson();
        beaconAddJson.setFloorId(beacon.getFloorId());
        beaconAddJson.setMajor(beacon.getMajor());
        beaconAddJson.setMinor(beacon.getMinor());
        beaconAddJson.setName(null);
        beaconAddJson.setUuid(beacon.getUuid());
        beaconAddJson.setX(beacon.getX());
        beaconAddJson.setY(beacon.getY());

        jsonString = JSON.toJSONString(beaconAddJson);
        break;
      case Beacon.ACTION_DELETE:

        break;
      case Beacon.ACTION_UPDATE:
        BeaconUpdateJson beaconUpdateJson = new BeaconUpdateJson();
        beaconUpdateJson.setBeaconId(beacon.getId());
        beaconUpdateJson.setSceneId(sceneID);
        beaconUpdateJson.setMapId(mapID);
        beaconUpdateJson.setFloorId(beacon.getFloorId());
        beaconUpdateJson.setMajor(beacon.getMajor());
        beaconUpdateJson.setMinor(beacon.getMinor());
        beaconUpdateJson.setName(null);
        beaconUpdateJson.setUuid(beacon.getUuid());
        beaconUpdateJson.setX(beacon.getX());
        beaconUpdateJson.setY(beacon.getY());

        jsonString = JSON.toJSONString(beaconUpdateJson);
        break;
      default:

        break;
    }
    String url;
    if (beacon.getAction() == Beacon.ACTION_ADD)
       url = URL_APIV2_BEACON + sceneID + "/beacons";//?api_key="+BluetoothUtilsApplication.accessToken;
    else
       url = URL_APIV2_BEACON + sceneID + "/beacons/"+beacon.getId();//+"?api_key="+BluetoothUtilsApplication.accessToken;

    Headers headers = new Headers.Builder()
        .add("Accept", "application/json")
        .add("Content-Type", "application/json")
        .add("Authorization", "Bearer " + BluetoothUtilsApplication.accessToken)
        .build();
    if (beacon.getAction()==Beacon.ACTION_DELETE){
      mDataProvider.deleteProvider(url, headers, callBack);
    }else if(beacon.getAction()==Beacon.ACTION_UPDATE){
      mDataProvider.putDataProvider(url, headers, jsonString == null ? null : jsonString, callBack);
    } else if (beacon.getAction()==Beacon.ACTION_ADD){
      mDataProvider.postDataProvider(url, headers, jsonString == null ? null : jsonString, callBack);
    }
  }


}
