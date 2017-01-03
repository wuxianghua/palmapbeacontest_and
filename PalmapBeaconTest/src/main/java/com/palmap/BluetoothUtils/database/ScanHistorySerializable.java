package com.palmap.BluetoothUtils.database;

import com.palmap.BluetoothUtils.main.model.Beacon;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eric3 on 2016/9/20.用于上传巡检记录
 */
public class ScanHistorySerializable implements Serializable{
  private long floorId;
  private String floorName;
  private long time;
  private long mapId;
  private String userName;
  private int abnormalBeaconNum;//异常beacon数量
  private boolean isUpload;
  private List<Beacon> beaconList;

  @Override
  public String toString() {
    return "ScanHistorySerializable{" +
        "floorId=" + floorId +
        ", floorName='" + floorName + '\'' +
        ", time=" + time +
        ", mapId=" + mapId +
        ", userName='" + userName + '\'' +
        ", abnormalBeaconNum=" + abnormalBeaconNum +
        ", isUpload=" + isUpload +
        ", beaconList=" + beaconList +
        '}';
  }

  public long getFloorId() {
    return floorId;
  }

  public void setFloorId(long floorId) {
    this.floorId = floorId;
  }

  public boolean isUpload() {
    return isUpload;
  }

  public void setUpload(boolean upload) {
    isUpload = upload;
  }

  public String getFloorName() {
    return floorName;
  }

  public void setFloorName(String floorName) {
    this.floorName = floorName;
  }
  public int getAbnormalBeaconNum() {
    return abnormalBeaconNum;
  }

  public void setAbnormalBeaconNum(int abnormalBeaconNum) {
    this.abnormalBeaconNum = abnormalBeaconNum;
  }



  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getMapId() {
    return mapId;
  }

  public void setMapId(long mapId) {
    this.mapId = mapId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<Beacon> getBeaconList() {
    return beaconList;
  }

  public void setBeaconList(List<Beacon> beaconList) {
    this.beaconList = beaconList;
  }
}
