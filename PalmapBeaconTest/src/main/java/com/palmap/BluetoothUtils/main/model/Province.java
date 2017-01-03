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
public class Province implements Serializable{
  private String mName;
  private String mValue;

  public Province(){}

  public Province(String mName, String mValue) {
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
    return "Province{" +
            "mName='" + mName + '\'' +
            ", mValue='" + mValue + '\'' +
            '}';
  }

  /*
  * 解析province对象
  * */
  public static Province parse(JSONObject object){
    if (object == null){
      return null;
    }

    Province province = new Province();
    province.setName(object.optString("label", ""));
    province.setValue(object.optString("value", ""));

    return province;
  }

  /*
  * 获取省列表
  * */
  public static List<Province> getProvinces(JSONArray array){
    List<Province> provinces = new ArrayList<Province>();
    Province title = new Province("---", "");
    provinces.add(title);

    if (array != null && array.length() > 0){
      try {
        int len = array.length();
        JSONObject object = null;
        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          Province province = parse(object);
          if (province != null){
            provinces.add(province);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return provinces;
  }

}
