package com.palmap.BluetoothUtils.main.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/4/7.
 * 封装楼层列表信息
 */
public class Floor implements Serializable, Comparable<Floor> {
  private String name;
  private long id;
  private String alias; // 别名，有些场景获取不到

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {//某些场景没有别名
    if (alias == null || alias.equals("null") )//|| alias.equals("F"))
      this.alias = name;

    this.alias = alias;
  }

  @Override
  public String toString() {
    return "Floor{" +
            "name='" + name + '\'' +
            ", id=" + id +
            ", alias='" + alias + '\'' +
            '}';
  }

  //平台接口处理楼层数据
  public static List<Floor> getFloorList(JSONArray array){
    List<Floor> floorList = new ArrayList<Floor>();
    if (array != null){
      int size = array.length();
      if (size > 0){
        try {
          JSONObject object = null;
          Floor floor = null;
          for (int i = 0; i < size; i++){
            object = array.getJSONObject(i);
            floor = new Floor();
            floor.setName(object.optString("label", ""));
            floor.setId(object.optLong("value", -1));
            floor.setAlias(object.optString("alias", ""));
            floorList.add(floor);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }

    return floorList;
  }

  @Override
  public int compareTo(Floor another) {
    long index_2 = another.getId();
    if (this.id <= index_2){
      return 1;
    } else {
      return -1;
    }
  }

}
