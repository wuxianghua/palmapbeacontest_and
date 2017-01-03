package com.palmap.BluetoothUtils.database.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by eric3 on 2016/12/1.
 * 巡检记录表history用于查询巡检记录，存储不同用户不同时间的巡检记录。
 */

public class HistoryTable extends Table{
  public static final String NAME = "history";

  public static final String ITEM_SCENE_ID = "scene_id";
  public static final String ITEM_MAP_ID = "map_id";
  public static final String ITEM_FLOOR_NAME = "floor_name";
  public static final String ITEM_FLOOR_ID = "floor_id";
  public static final String ITEM_TIME = "time";
  public static final String ITEM_USER_NAME = "user_name";
  public static final String ITEM_ABNORMAL_BEACON_NUM = "abnormal_beacon_num";
  public static final String ITEM_IS_UPLOAD = "is_upload";
  public static final String ITEM_BEACON_LIST = "beacon_list";

  public static void createTable(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(getCreateTableSQL());
  }



  private static String getCreateTableSQL() {
    return "create table if not exists " + NAME +
        "("
//        +"_id integer primary key autoincrement,"
        +ITEM_SCENE_ID+" long," +ITEM_MAP_ID+" long,"
        +ITEM_FLOOR_NAME+" text,"+ITEM_FLOOR_ID+" long,"
        + ITEM_TIME+" long,"+ITEM_USER_NAME+" text,"
        +ITEM_ABNORMAL_BEACON_NUM+" integer,"+ITEM_IS_UPLOAD+" integer,"
        + ITEM_BEACON_LIST+" text,UNIQUE("+ITEM_TIME+"), PRIMARY KEY("+ITEM_TIME+") )";
  }


  public String getSelectItemSQL(String userName, String type) {
    return null;
  }


  public String getInsertItemSQL(String userName, String type) {
    return null;
  }

  public static String getInsertAllItemSQL() {
    return "replace into " + NAME
        + " ("+ITEM_SCENE_ID+","+ITEM_MAP_ID+","+ITEM_FLOOR_NAME+","
        +ITEM_FLOOR_ID+","+ITEM_TIME+"," +ITEM_USER_NAME+","
        +ITEM_ABNORMAL_BEACON_NUM+","+ITEM_IS_UPLOAD+"," +ITEM_BEACON_LIST+") "+
        "values(?,?,?,?,?,?,?,?,?)";
  }
  public static String getSelectAllItemSQL(long sceneId,long mapId) {
    return "select * from "+NAME+" where "+ITEM_SCENE_ID +" = "+sceneId+" and "+ITEM_MAP_ID +" = "+mapId ;
  }
}
