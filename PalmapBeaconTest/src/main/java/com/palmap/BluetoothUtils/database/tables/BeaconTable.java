package com.palmap.BluetoothUtils.database.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by eric3 on 2016/12/1.
 * 蓝牙点位表beacon用于巡检，存储不同用户创建的不同地图下的点位信息。
 * 离线  ITEM_SCENE_ID = 真实id
 * 在线  ITEM_SCENE_ID = CASH_SCENE_ID
 */

public class BeaconTable extends Table{
  public static final String NAME = "beacon";
//  public static final long CASH_SCENE_ID = 0;

  public static final String ITEM_SCENE_ID = "scene_id";//场景id，用于区分不同场景（或用户）下相同地图的beacon，scene_id=0时代表在线模式下的cash
  public static final String ITEM_MAP_ID = "map_id";
  public static final String ITEM_BEACON_ID = "beacon_id";
  public static final String ITEM_UUID = "uuid";
  public static final String ITEM_MAJOR = "major";
  public static final String ITEM_MINOR = "minor";
  public static final String ITEM_FLOOR_ID = "floor_id";
  public static final String ITEM_POINT_X = "point_x";
  public static final String ITEM_POINT_Y = "point_y";
  public static final String ITEM_ACTION = "action";
  public static final String ITEM_ISSCANED = "isscaned";//该beacon是否被巡检到  true、false

  //新增
  public static final String ITEM_NAME = "name";//beacon名字
  public static final String ITEM_POWER = "power";//电量,如50%，存储‘50’


  public static void createTable(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(getCreateTableSQL());
  }


  private static String getCreateTableSQL() {
    return "create table if not exists " + NAME +
        "(" +
//        "_id integer primary key autoincrement, " +
        ITEM_NAME + " text, " +ITEM_POWER + " integer, " +
        ITEM_SCENE_ID + " integer not null, " + ITEM_MAP_ID + " integer not null, " + ITEM_BEACON_ID + " integer not null, " + ITEM_UUID + " text not null, " +
        ITEM_MAJOR + " smallint not null, " + ITEM_MINOR + " smallint not null, " +
        ITEM_FLOOR_ID + " integer not null, " + ITEM_POINT_X + " decimal(11,3) not null, " +
        ITEM_POINT_Y + " decimal(11,3) not null, " + ITEM_ACTION + " smallint not null,"
        + ITEM_ISSCANED + " smallint not null,UNIQUE("+ITEM_BEACON_ID+"), PRIMARY KEY("+ITEM_BEACON_ID+") )";
  }



  public static String getDeleteBeaconSQL(String key,long ID) {
    return "DELETE FROM " + NAME + " WHERE "+key+" = " + ID;
  }

  public static String getDeleteBeaconSQL(long sceneId,long mapId) {
    return "DELETE FROM " + NAME + " WHERE "+ITEM_SCENE_ID+" = " + sceneId + " and "+ITEM_MAP_ID+" = " + mapId;
  }

  public static String getSelectItemSQL(long sceneId,long mapId,long floorId) {
    return "select * from " + NAME + " where "
        + ITEM_SCENE_ID + " = " + sceneId+" and "
        + ITEM_MAP_ID + " = " + mapId+" and "
        + ITEM_FLOOR_ID + " = " + floorId;
  }
  public static String getSelectItemSQL(long sceneId,long mapId) {
    return "select * from " + NAME + " where "
        + ITEM_SCENE_ID + " = " + sceneId+" and "
        + ITEM_MAP_ID + " = " + mapId;
  }


  public String getInsertItemSQL(long sceneId,long mapId,long floorId) {
    return null;
  }
}
