package com.palmap.BluetoothUtils.main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric3 on 2016/12/15.
 * beacon扫描需要匹配的参数
 */

public class ScanParameters {
  public class Item {
    private String uuid;//存储为大写字母
    private int major;

    public Item(String uuid, int major) {
      this.uuid = uuid;
      this.major = major;
    }

    public String getUuid() {
      return uuid;
    }

    public int getMajor() {
      return major;
    }
  }

  private List<Item> itemList;

  public ScanParameters() {
    this.itemList = new ArrayList<>();
  }

  /**
  * @Author: eric3
  * @Description: 切换楼层需要清空uuid和major
  * @Time 2016/12/19 14:38
  */
  public void clear(){
    itemList = new ArrayList<>();
  }
  public boolean add(String uuid,int major){
    if (itemList==null)
      itemList = new ArrayList<>();

    if (contains(uuid, major))
      return false;

    Item i = new Item(uuid.toUpperCase(),major);
    itemList.add(i);
    return true;
  }

  public List<Item> getItemList() {
    return itemList;
  }

  public boolean contains(String uuid,int major){
    if (itemList==null)
      return false;

    for (Item item:itemList){
      if (item.getMajor()==major&&item.getUuid().equals(uuid.toUpperCase()))
        return true;
    }

    return false;
  }

  @Override
  public String toString() {
    if (itemList == null)
      return "";

    String s = "";
    for (Item item : itemList) {
      s += "[" + item.getUuid() + "," + item.getMajor() + "]<br>";
    }

    return s;
  }
}
