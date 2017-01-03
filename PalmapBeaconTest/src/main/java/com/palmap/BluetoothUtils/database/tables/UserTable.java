package com.palmap.BluetoothUtils.database.tables;


import android.database.sqlite.SQLiteDatabase;

/**
 * Created by eric3 on 2016/12/1.
 * 离线登陆用户名密码存储位置（密码已加密）离线用户信息表user用于离线登陆，存储用户名，密码和场景信息；
 */

public class UserTable extends Table {
  public static final String NAME = "user";

  public static final String ITEM_NAME = "userName";
  public static final String ITEM_PWD = "pwd";
  public static final String ITEM_SCENES = "mSceneArray";
  public static final String ITEM_PROVINCE = "mProvinceData";
  public static final String ITEM_CITY = "mCityData";
  public static final String ITEM_REGION = "mRegionData";
  public static final String ITEM_MAP = "mMapData";

  public static void createTable(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(getCreateTableSQL());
  }

  private static String getCreateTableSQL() {//用户离线登陆数据表
    return "create table if not exists " + NAME
        + "("
//       + "_id integer primary key autoincrement,"
        +ITEM_NAME+" text,"
        +ITEM_PWD+" text,"+ITEM_SCENES+" text,"
        +ITEM_PROVINCE+" text,"+ITEM_CITY+" text,"
        +ITEM_REGION+" text,"+ITEM_MAP+" text,UNIQUE("
        +ITEM_NAME+"), PRIMARY KEY("+ITEM_NAME+") )";
  }

//  private static String getInsertUserNameSQL(String userName) {
//    return "INSERT OR IGNORE INTO " + NAME + " ("+ITEM_NAME+") VALUES ('" + userName + "')";
//  }

  public static String getInsertPwdSQL(String userName, String pwd) {
    return "UPDATE " + NAME + " SET "+ITEM_PWD+" = '" + pwd + "' WHERE "+ITEM_NAME+" = '" + userName + "'";
  }


  public static String getSelectItemSQL(String userName, String type){
    return "select "+type+" from "+NAME+" where "+ITEM_NAME+"='" + userName + "'";
  }


  public static String getUpdateItemSQL(String userName, String type) {
    return "update " + NAME + " set "+type+" = ? WHERE "+ITEM_NAME+" = '" + userName + "'";
  }

  public static String getInsertUserNameSQL(String userName) {
    return "INSERT OR IGNORE INTO " + NAME + " (userName) VALUES ('" + userName + "')";
  }

//  private String getInsertSceneArraySQL( String userName) {
//    return "UPDATE " + NAME + " SET "+ITEM_SCENES+" = ? WHERE "+ITEM_NAME+" = '" + userName + "'";
//  }
//
//  private String getInsertProvinceDataSQL(String userName) {
//    return "UPDATE " + NAME + " SET "+ITEM_PROVINCE+" = ? WHERE "+ITEM_NAME+" = '" + userName + "'";
//  }
//
//  private String getInsertCityDataSQL(String userName) {
//    return "UPDATE " + NAME + " SET "+ITEM_CITY+" = ? WHERE "+ ITEM_NAME+" = '" + userName + "'";
//  }
//
//  private String getInsertRegionDataSQL(String userName) {
//    return "UPDATE " + NAME + " SET "+ITEM_REGION+" = ? WHERE "+ITEM_NAME+" = '" + userName + "'";
//  }
//
//  private String getInsertMapDataSQL(String userName) {
//    return "UPDATE " + NAME + " SET "+ITEM_MAP+" = ? WHERE "+ITEM_NAME+" = '" + userName + "'";
//  }




}
