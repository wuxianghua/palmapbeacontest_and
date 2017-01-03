package com.palmap.BluetoothUtils.database.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by eric3 on 2016/12/1.
 * 离线地图数据表map用于离线地图加载，存储地图id和楼层id；
 */

public class MapTable extends Table{
  public static final String NAME = "map";

  public static final String ITEM_SCENE_ID = "scene_id";
  public static final String ITEM_MAP_ID = "map_id";
  public static final String ITEM_MAP_DATA = "mapDataSerializable";

  public static void createTable(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(getCreateTableSQL());
  }



  private static String getCreateTableSQL() {
    return "create table if not exists " + NAME +
        "("
//        +"_id integer primary key autoincrement,"
        +ITEM_SCENE_ID
        +" integer not null,"+ITEM_MAP_ID+" integer not null,"
        +ITEM_MAP_DATA+" text, PRIMARY KEY("+ITEM_SCENE_ID+", "+ITEM_MAP_ID+") )";
  }


  public static String getSelectItemSQL(long sceneId,long mapId) {
    return "select * from " + NAME + " where "+ITEM_MAP_ID+"=" + mapId+" and "+ITEM_SCENE_ID+" = "+sceneId;
  }


  public String getInsertItemSQL(String userName, String type) {
    return null;
  }

  public static String getReplaceAllItemSQL(){
    return "replace into " + NAME + " ("+ITEM_SCENE_ID+","+ITEM_MAP_ID+","+ITEM_MAP_DATA+") values(?,?,?)";
  }
}
