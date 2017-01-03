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
public class City implements Serializable{
  private String mName;
  private String mValue;

  public City(){}

  public City(String mName, String mValue) {
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
    return "City{" +
            "mName='" + mName + '\'' +
            ", mValue='" + mValue + '\'' +
            '}';
  }

  /*
  * 解析province对象
  * */
  public static City parse(JSONObject object){
    if (object == null){
      return null;
    }

    City city = new City();
    city.setName(object.optString("label", ""));
    city.setValue(object.optString("value", ""));

    return city;
  }

  /*
  * 获取省列表
  * */
  public static List<City> getCities(JSONArray array){
    List<City> cities = new ArrayList<City>();
    City title = new City("---", "");
    cities.add(title);

    if (array != null && array.length() > 0){
      try {
        int len = array.length();
        JSONObject object = null;
        for (int i = 0; i < len; i++){
          object = array.getJSONObject(i);
          City city = parse(object);
          if (city != null){
            cities.add(city);
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }

    return cities;
  }

}
