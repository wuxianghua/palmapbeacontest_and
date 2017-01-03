package com.palmap.BluetoothUtils.main.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/10/14.
 */
public class PalMap implements Serializable{
  private String mName;
  private long mMapId;
  private long mSceneId;
  private String mProvinceName;
  private String mAppKey;

  public String getAppKey() {
    return mAppKey;
  }

  public void setAppKey(String mAppKey) {
    this.mAppKey = mAppKey;
  }

  public long getSceneId() {
    return mSceneId;
  }

  public void setSceneId(long sceneId) {
    this.mSceneId = sceneId;
  }

  public PalMap(){}

  public PalMap(String mName, long mMapId, long mSceneId, String mProvinceName) {
    this.mName = mName;
    this.mMapId = mMapId;
    this.mSceneId = mSceneId;
    this.mProvinceName = mProvinceName;
  }
//  public PalMap(String mName, long mMapId) {
//    this.mName = mName;
//    this.mMapId = mMapId;
//  }


  public String getName() {
    return mName;
  }

  public String getProvinceName() {
    return mProvinceName;
  }

  public void setProvinceName(String mProvinceName) {
    this.mProvinceName = mProvinceName;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public long getMapId() {
    return mMapId;
  }

  public void setMapId(long mapId) {
    this.mMapId = mapId;
  }

  @Override
  public String toString() {
    return "PalMap{" +
            "mName='" + mName + '\'' +
            ", mMapId='" + mMapId + '\'' +
            '}';
  }

  /*
  * 解析province对象
  * */
  public static PalMap parse(JSONObject object){
    if (object == null){
      return null;
    }

    PalMap palMap = new PalMap();
    palMap.setName(object.optString("mapName", "不知道叫什么名字"));
    palMap.setMapId(object.optLong("mapId", -1));
    palMap.setSceneId(object.optLong("sceneId", -1));
    palMap.setProvinceName(object.optString("provinceName", "不知道在什么地方"));
    return palMap;
  }

  /*
  * 获取地图列表
  * */
  public static List<PalMap> getMalls(JSONArray array){
    List<PalMap> palMaps = new ArrayList<PalMap>();
//    PalMap title = new PalMap("---", -1);
//    palMaps.add(title);

    if (array != null && array.length() > 0){
      try {
        int len = array.length();
        JSONObject object = null;
        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          PalMap palMap = parse(object);
          if (palMap != null){
            palMaps.add(palMap);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return palMaps;
  }

  /*
 * 向列表中添加mall
 * */
  public static List<PalMap> addMalls(JSONArray array, List<PalMap> palMapList,String appKey){

    if (array != null && array.length() > 0){
      try {
        int len = array.length();
        JSONObject object = null;

        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          PalMap palMap = parse(object);
          if (palMap != null){
            palMap.setAppKey(appKey);
            palMapList.add(palMap);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return palMapList;
  }

}
