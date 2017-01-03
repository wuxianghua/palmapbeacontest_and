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
public class Region implements Serializable{
  private String mName;
  private String mValue;

  public Region(){}

  public Region(String mName, String mValue) {
    this.mName = mName;
    this.mValue = mValue;
  }


  public String getName() {
    return mName;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public String getValue() {
    return mValue;
  }

  public void setValue(String mValue) {
    this.mValue = mValue;
  }

  @Override
  public String toString() {
    return "Region{" +
            "mName='" + mName + '\'' +
            ", mValue='" + mValue + '\'' +
            '}';
  }

  /*
  * 解析province对象
  * */
  public static Region parse(JSONObject object){
    if (object == null){
      return null;
    }

    Region region = new Region();
    region.setName(object.optString("label", ""));
    region.setValue(object.optString("value", ""));

    return region;
  }

  /*
  * 获取省列表
  * */
  public static List<Region> getRegions(JSONArray array){
    List<Region> regions = new ArrayList<Region>();
    Region title = new Region("---", "");
    regions.add(title);

    if (array != null && array.length() > 0){
      try {
        int len = array.length();
        JSONObject object = null;
        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          Region region = parse(object);
          if (region != null){
            regions.add(region);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return regions;
  }

}
