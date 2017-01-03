package com.palmap.BluetoothUtils.http.jsonclass;

/**
 * Created by eric3 on 2016/9/21.
 * {
 * "floorId": 0,
 * "name": "string",
 * "uuid": "string",
 * "major": 0,
 * "minor": 0,
 * "x": 0,
 * "y": 0
 * }
 */
public class BeaconAddJson {

  long floorId;
  String name;
  String uuid;
  int major;
  int minor;
  double x;
  double y;

  public BeaconAddJson() {
  }

  @Override
  public String toString() {
    return "BeaconAddJson{" +
        "floorId=" + floorId +
        ", name='" + name + '\'' +
        ", uuid='" + uuid + '\'' +
        ", major=" + major +
        ", minor=" + minor +
        ", x=" + x +
        ", y=" + y +
        '}';
  }

  public long getFloorId() {
    return floorId;
  }

  public void setFloorId(long floorId) {
    this.floorId = floorId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
