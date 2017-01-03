package com.palmap.BluetoothUtils.main.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/10/16.
 */
public class Beacon implements Serializable, Comparable<Beacon> {
  public static final int ACTION_DELETE = -1; // 删除
  public static final int ACTION_NO_CHANGE = 0; // 不需要提交
  public static final int ACTION_ADD = 1; // 添加
  public static final int ACTION_UPDATE = 2; // 更新
  private String mName;
  private int mPowerPercent;//电量百分比
  private long mSceneId;
  private long mMapId;
  private int mId;
  private String mUuid;
  private int mMajor;
  private int mMinor;
  private double x;
  private double y;
  private long mFloorId;
  private boolean isSelect;
  private int mAction = ACTION_NO_CHANGE; // 默认状态,不需上传
  private boolean isScaned; // 被扫描到

  public static final int DEFAULT_POWER = 0;//未检测到的默认电量百分比

  public Beacon(){}
  /**
  * @Author: eric3
  * @Description: 用于构造扫描到的beacon，传给listview显示
  * @Time 2016/12/15 17:53
  */
  public Beacon(String mName, int mPowerPercent, String mUuid, int mMajor, int mMinor) {
    this.mName = mName;
    this.mPowerPercent = mPowerPercent;
    this.mUuid = mUuid;
    this.mMajor = mMajor;
    this.mMinor = mMinor;
  }

  public String getName() {
    return mName;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public int getPowerPercent() {
    return mPowerPercent;
  }

  public void setPowerPercent(int mPowerPercent) {
    this.mPowerPercent = mPowerPercent;
  }

  public long getSceneId() {
    return mSceneId;
  }

  public void setSceneId(long mSceneId) {
    this.mSceneId = mSceneId;
  }

  public long getMapId() {
    return mMapId;
  }

  public void setMapId(long mMapId) {
    this.mMapId = mMapId;
  }

  public int getId() {
    return mId;
  }

  public void setId(int mId) {
    this.mId = mId;
  }

  public String getUuid() {
    return mUuid;
  }

  public void setUuid(String mUuid) {
    this.mUuid = mUuid;
  }

  public int getMajor() {
    return mMajor;
  }

  public void setMajor(int mMajor) {
    this.mMajor = mMajor;
  }

  public int getMinor() {
    return mMinor;
  }

  public void setMinor(int mMinor) {
    this.mMinor = mMinor;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public long getFloorId() {
    return mFloorId;
  }

  public void setFloorId(long mFloorId) {
    this.mFloorId = mFloorId;
  }

  public boolean isSelect() {
    return isSelect;
  }

  public void setSelect(boolean isSelect) {
    this.isSelect = isSelect;
  }

  public int getAction() {
    return mAction;
  }

  public void setAction(int mAction) {
    this.mAction = mAction;
  }

  public boolean isScaned() {
    return isScaned;
  }

  public void setScaned(boolean scaned) {
    isScaned = scaned;
  }

  @Override
  public String toString() {
    return "Beacon{" +
        "mName='" + mName + '\'' +
        ", mPowerPercent=" + mPowerPercent +
        ", mSceneId=" + mSceneId +
        ", mMapId=" + mMapId +
        ", mId=" + mId +
        ", mUuid='" + mUuid + '\'' +
        ", mMajor=" + mMajor +
        ", mMinor=" + mMinor +
        ", x=" + x +
        ", y=" + y +
        ", mFloorId=" + mFloorId +
        ", isSelect=" + isSelect +
        ", mAction=" + mAction +
        ", isScaned=" + isScaned +
        '}';
  }

  /**
   * 解析beacon对象 -- json格式
   *
   * @param object
   * @return
   */
  public static List<Beacon> getBeaconListAPIV2(JSONObject object){
    List<Beacon> beacons = new ArrayList<Beacon>();
    if (object != null && object.has("list")){
      try {
        JSONArray array = object.getJSONArray("list");
        if (array != null && array.length() > 0){
          Beacon beacon = null;
          JSONObject obj1 = null;
//          JSONObject obj2 = null;
          for (int i = 0, size = array.length(); i < size; i++){
            beacon = new Beacon();
            obj1 = array.getJSONObject(i);

            beacon.setSceneId(obj1.optLong("sceneId"));
            beacon.setName(obj1.optString("name"));
            beacon.setPowerPercent(DEFAULT_POWER);
            beacon.setMapId(obj1.optLong("mapId"));
            beacon.setFloorId(obj1.optLong("floorId"));
            beacon.setId(obj1.optInt("beaconId"));
            beacon.setUuid(obj1.optString("uuid").toUpperCase());
            beacon.setMajor(obj1.optInt("major"));
            beacon.setMinor(obj1.optInt("minor"));
//            obj2 = obj1.optJSONObject("geom");
            beacon.setX(obj1.optDouble("x"));
            beacon.setY(obj1.optDouble("y"));
            beacons.add(beacon);
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return beacons;
  }

  @Override
  public int compareTo(Beacon another) {
    if (another == null) return 0;
    if (this.mMinor > another.mMinor) return 1;
    else if (this.mMinor < another.mMinor) return -1;
    else return 0;
  }
}
