package com.palmap.BluetoothUtils.http.jsonclass;

/**
 * Created by eric3 on 2016/9/21.
 */
public class BeaconUpdateJson {
  int beaconId ;
  String name ;
  long sceneId ;
  long floorId ;
  long mapId;
  String uuid ;
  int major ;
  int minor ;
  double x ;
  double y ;

  public BeaconUpdateJson() {
  }

  public int getBeaconId() {
    return beaconId;
  }

  public void setBeaconId(int beaconId) {
    this.beaconId = beaconId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSceneId() {
    return sceneId;
  }

  public void setSceneId(long sceneId) {
    this.sceneId = sceneId;
  }

  public long getFloorId() {
    return floorId;
  }

  public void setFloorId(long floorId) {
    this.floorId = floorId;
  }

  public long getMapId() {
    return mapId;
  }

  public void setMapId(long mapId) {
    this.mapId = mapId;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getMajor() {
    return major;
  }

  public void setMajor(int major) {
    this.major = major;
  }

  public int getMinor() {
    return minor;
  }

  public void setMinor(int minor) {
    this.minor = minor;
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
}
