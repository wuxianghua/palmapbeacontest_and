package com.palmap.BluetoothUtils.main.model;

import com.palmap.BluetoothUtils.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/10/14.
 * 一个scene下面可以有很多map
 */
public class Scene implements Serializable {
  private String mSceneName;
  private long mSceneId;
  private String mAppKey;
  private int mBeaconTotal; // 该场景可用beacon总数
  private int mBeaconUsed; // 该场景已用beacon总数

  public Scene(){}

  public Scene(String mSceneName) {
    this.mSceneName = mSceneName;
  }

  public String getSceneName() {
    return mSceneName;
  }

  public void setSceneName(String mSceneName) {
    this.mSceneName = mSceneName;
  }

  public long getSceneId() {
    return mSceneId;
  }

  public void setSceneId(long mSceneId) {
    this.mSceneId = mSceneId;
  }

  public String getAppKey() {
    if("".equals(mAppKey) || mAppKey==null)
      LogUtils.w(" appkey 不存在！");

    return mAppKey;
  }

  public void setAppKey(String mAppKey) {
    this.mAppKey = mAppKey;
  }

  public int getBeaconTotal() {
    return mBeaconTotal;
  }

  public void setBeaconTotal(int mBeaconTotal) {
    this.mBeaconTotal = mBeaconTotal;
  }

  public int getBeaconUsed() {
    return mBeaconUsed;
  }

  public void setBeaconUsed(int mBeaconUsed) {
    this.mBeaconUsed = mBeaconUsed;
  }

  @Override
  public String toString() {
    return "Scene{" +
            "mSceneName='" + mSceneName + '\'' +
            ", mSceneId=" + mSceneId +
            ", mAppKey='" + mAppKey + '\'' +
            ", mBeaconTotal=" + mBeaconTotal +
            ", mBeaconUsed=" + mBeaconUsed +
            '}';
  }

  /*
  *  解析Scene对象
  * */
  public static Scene parse(JSONObject object){
    if (object == null){
      return null;
    }

    Scene scene = new Scene();
    scene.setSceneName(object.optString("sceneName", ""));
    scene.setSceneId(object.optLong("sceneId", -1L));
    scene.setAppKey(object.optString("appKey", ""));

    return scene;
  }

  /*
  *  获取scene链表
  * */
  public static List<Scene> getSceneList(JSONArray array){
    List<Scene> scenes = new ArrayList<Scene>();
    if (array != null){
      try {
        int len = array.length();
        JSONObject object = null;
        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          Scene scene = parse(object);
          if (scene != null){
            scenes.add(scene);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return scenes;
  }

  /*
  *  获取scene数组
  * */
  public static Scene[] getSceneArray(JSONArray array){
    Scene title = new Scene("---");
    if (array == null){
      return new Scene[]{title};
    }

    int len = array.length();
    Scene[] scenes = new Scene[len + 1];
    scenes[0] = title;
    try {
      JSONObject object = null;
      for (int i = 0; i < len; i++){
        object = array.getJSONObject(i);
        Scene scene = parse(object);
        if (scene != null){
          scenes[i + 1] = scene;
        }
      }
    } catch (JSONException e){
      e.printStackTrace();
    }

    return scenes;
  }

}
