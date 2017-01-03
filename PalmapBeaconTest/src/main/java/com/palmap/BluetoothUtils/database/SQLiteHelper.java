package com.palmap.BluetoothUtils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.palmap.BluetoothUtils.database.tables.BeaconTable;
import com.palmap.BluetoothUtils.database.tables.HistoryTable;
import com.palmap.BluetoothUtils.database.tables.MapTable;
import com.palmap.BluetoothUtils.database.tables.UserTable;
import com.palmap.BluetoothUtils.impl.OnDeleteComplete;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.Floor;
import com.palmap.BluetoothUtils.security.EncrypUtil;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.FileUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_ACTION;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_BEACON_ID;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_FLOOR_ID;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_ISSCANED;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_MAJOR;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_MAP_ID;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_MINOR;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_NAME;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_POINT_X;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_POINT_Y;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_POWER;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_SCENE_ID;
import static com.palmap.BluetoothUtils.database.tables.BeaconTable.ITEM_UUID;

/**
 * Created by zhang on 2015/10/19.
 * changed by eric on 2016/9/14
 * 创建和更新数据库
 * 任务：管理beacon数据
 * 管理离线map数据,将对象序列化为字节流字符串，然后将字节流字符串以TEXT类型存储在数据库中
 */
public class SQLiteHelper {
    private static final SQLiteHelper INSTANCE = new SQLiteHelper();
  public static final String DATABASE_NAME = "PalmapDB";
//  private UserTable UserTable;
//  private HistoryTable HistoryTable;
//  private BeaconTable BeaconTable;
//  private MapTable mapTable;
  public static final int TABLE_BEACON = 0;
  public static final int TABLE_MAP = 1;
  public static final int TABLE_USER = 2;
  public static final int TABLE_HISTORY = 3;



  public static final int SCENEARRAY = 100;
  public static final int PROVINCEDATA = 101;
  public static final int CITYDATA = 102;
  public static final int REGIONDATA = 103;
  public static final int MAPDATA = 104;
  public static final int PWD = 105;
  public static final int USERNAME = 106;




  private static Context mContext;
  private volatile static SQLiteDatabase mSqLiteDatabase;

//  private static boolean isDataChanged = false; // 数据库是否更改过 - 用于判断是否有需要重新读取beacon列表

  private static int NEW_BEACON_ID_TEMPORARY = -1;//用于临时为新添加的beacon id（主键）赋值，上传后由服务器分配



  private SQLiteHelper() {

  }

  public static SQLiteHelper getInstance(Context context){
    mContext = context;
    return INSTANCE;
  }
  public void createDatabaseDefault() {
    mSqLiteDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    BeaconTable.createTable(mSqLiteDatabase);
//    mSqLiteDatabase.close();
  }

  public void createDatabaseTable(int TYPE) {//创建表

    switch (TYPE) {
      case TABLE_BEACON:
        BeaconTable.createTable(mSqLiteDatabase);
        break;
      case TABLE_MAP:
        MapTable.createTable(mSqLiteDatabase);
        break;
      case TABLE_USER:
        UserTable.createTable(mSqLiteDatabase);
        break;
      case TABLE_HISTORY:
        HistoryTable.createTable(mSqLiteDatabase);
        break;
      default:
        DialogUtils.showLongToast("数据库创建失败，TYPE=" + TYPE);
        return;

    }

//    mSqLiteDatabase.close();
  }

//  public String getBeaconTableName(long mapID){
//    return "beacon_"+mapID;
//  }
  /*
  * 清空某表
  * */
  public void clearDBTable(String tableName) {
    if (mSqLiteDatabase == null) return;
    openDatabase();
//    mSqLiteDatabase.delete(tableName, null, null);
//    int returnCode = mSqLiteDatabase.delete(tableName, null,null);
    //清除表中所有记录：
    mSqLiteDatabase.execSQL("DELETE FROM " + tableName);
//    mSqLiteDatabase.close();
  }
  /**
  * @Author: eric
  * @Description: 删除beacon表中scene=0的beacon（在线模式的缓存）
  * @Time 2016/12/2 11:48
  */
  public void deleteBeaconCash(){
//    BeaconTable.getDeleteBeaconSQL(BeaconTable.ITEM_SCENE_ID,BeaconTable.CASH_SCENE_ID);
  }

  /*
* 清空当前表
* */
//  public void clearDBTable() {
//    if (mSqLiteDatabase == null) return;
//    if (!mSqLiteDatabase.isOpen()) {
//      mSqLiteDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
//    }
//    //清除表中所有记录：
//    mSqLiteDatabase.execSQL("DELETE FROM " + mTableName);
//
//    mSqLiteDatabase.close();
//  }

  /*
  * 更新一条beacon数据
  * 需要确保 mSqLiteDatabase.isOpen() = true
  * */
  private long updateBeacon(Beacon beacon, int beaconID){//},boolean isOfflineMode) {
    if (beacon == null) { // 没有数据，失败
      return -1L;
    }
    openDatabase();

    ContentValues values = new ContentValues();
    values.put(ITEM_SCENE_ID, beacon.getSceneId());
    values.put(ITEM_MAP_ID, beacon.getMapId());
    values.put(ITEM_FLOOR_ID, beacon.getFloorId());
    values.put(ITEM_BEACON_ID, beacon.getId());
    values.put(ITEM_UUID, beacon.getUuid());
    values.put(ITEM_MAJOR, beacon.getMajor());
    values.put(ITEM_MINOR, beacon.getMinor());
    values.put(ITEM_POINT_X, beacon.getX());
    values.put(ITEM_POINT_Y, beacon.getY());
    values.put(ITEM_ACTION, beacon.getAction());
    values.put(ITEM_ISSCANED, beacon.isScaned() == true ? 1 : 0);

    int ret = 0;

    ret = mSqLiteDatabase.update(BeaconTable.NAME, values, BeaconTable.ITEM_BEACON_ID+"=?",new String[]{ beaconID+""});

//    mSqLiteDatabase.close();

    return ret;
  }

  /*
  * 插入一条beacon数据
  * 需要确保 mSqLiteDatabase.isOpen() = true
  *
  * */
  private long insertBeacon(Beacon beacon) {
     if (beacon == null) { // 没有数据，插入失败
      return -1L;
    }
//    openDatabase();

    ContentValues values = new ContentValues();
    values.put(ITEM_SCENE_ID, beacon.getSceneId());
    values.put(ITEM_MAP_ID, beacon.getMapId());
    values.put(ITEM_BEACON_ID, beacon.getId());
    values.put(ITEM_UUID, beacon.getUuid());
    values.put(ITEM_MAJOR, beacon.getMajor());
    values.put(ITEM_MINOR, beacon.getMinor());
    values.put(ITEM_FLOOR_ID, beacon.getFloorId());
    values.put(ITEM_POINT_X, beacon.getX());
    values.put(ITEM_POINT_Y, beacon.getY());
    values.put(ITEM_ACTION, beacon.getAction());
    values.put(ITEM_ISSCANED, beacon.isScaned() == true ? 1 : 0);
    values.put(ITEM_NAME, beacon.getName());
    values.put(ITEM_POWER, beacon.getPowerPercent());


    //replace = insert or update
    return mSqLiteDatabase.replace(BeaconTable.NAME, null, values);//可能是下载，可能是在线加载
  }

  /*
  *   批量插入
  * */
  public boolean insertBeacons(List<Beacon> beacons) {
    if (beacons == null || beacons.size() <= 0) {
      return false;
    }
    openDatabase();
    LogUtils.w( "待输入数据库items = " + beacons.size());


    for (Beacon beacon : beacons) {
      insertBeacon(beacon);
    }

//    mSqLiteDatabase.close();

    return true;
  }

  /*
  * 获取指定楼层beacon数据，在线\离线 模式
  * */
  public List<Beacon> getBeaconsByID(long sceneId, long mapId, long floorId) {
    List<Beacon> beacons = new ArrayList<Beacon>();
    openDatabase();

    String sql = BeaconTable.getSelectItemSQL(sceneId, mapId, floorId);


    try {
      Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);


    if (cursor.moveToFirst()) {
      do {
        Beacon beacon = new Beacon();
        beacon.setSceneId(cursor.getInt(cursor.getColumnIndex(ITEM_SCENE_ID)));
        beacon.setMapId(cursor.getInt(cursor.getColumnIndex(ITEM_MAP_ID)));
        beacon.setId(cursor.getInt(cursor.getColumnIndex(ITEM_BEACON_ID)));
        beacon.setUuid(cursor.getString(cursor.getColumnIndex(ITEM_UUID)));
        beacon.setMajor(cursor.getInt(cursor.getColumnIndex(ITEM_MAJOR)));
        beacon.setMinor(cursor.getInt(cursor.getColumnIndex(ITEM_MINOR)));
        beacon.setX(cursor.getDouble(cursor.getColumnIndex(ITEM_POINT_X)));
        beacon.setY(cursor.getDouble(cursor.getColumnIndex(ITEM_POINT_Y)));
        beacon.setFloorId(cursor.getLong(cursor.getColumnIndex(ITEM_FLOOR_ID)));
        beacon.setAction(cursor.getInt(cursor.getColumnIndex(ITEM_ACTION)));
        beacon.setScaned(1 == cursor.getInt(cursor.getColumnIndex(ITEM_ISSCANED)));
        beacon.setName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));
        beacon.setPowerPercent(cursor.getInt(cursor.getColumnIndex(ITEM_POWER)));
        beacons.add(beacon);
      } while (cursor.moveToNext());
    } else {
      LogUtils.w("当前无beacon数据");
    }
    cursor.close();
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }
//    mSqLiteDatabase.close();

    LogUtils.i( "floorId = " + floorId + ", beaconNum = " + beacons.size());
    return beacons;
  }


  /*
  * 获取所有楼层beacon数据，离线模式
  * */
  public List<Beacon> getBeacons(long sceneId,long mapId) {
    openDatabase();

    List<Beacon> beacons = new ArrayList<Beacon>();

    String sql =BeaconTable.getSelectItemSQL(sceneId, mapId);

    try {
      Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);

      if (cursor.moveToFirst()) {
        do {
          Beacon beacon = new Beacon();
          beacon.setSceneId(cursor.getInt(cursor.getColumnIndex(ITEM_SCENE_ID)));
          beacon.setMapId(cursor.getInt(cursor.getColumnIndex(ITEM_MAP_ID)));
          beacon.setId(cursor.getInt(cursor.getColumnIndex(ITEM_BEACON_ID)));
          beacon.setUuid(cursor.getString(cursor.getColumnIndex(ITEM_UUID)));
          beacon.setMajor(cursor.getInt(cursor.getColumnIndex(ITEM_MAJOR)));
          beacon.setMinor(cursor.getInt(cursor.getColumnIndex(ITEM_MINOR)));
          beacon.setX(cursor.getDouble(cursor.getColumnIndex(ITEM_POINT_X)));
          beacon.setY(cursor.getDouble(cursor.getColumnIndex(ITEM_POINT_Y)));
          beacon.setFloorId(cursor.getLong(cursor.getColumnIndex(ITEM_FLOOR_ID)));
          beacon.setAction(cursor.getInt(cursor.getColumnIndex(ITEM_ACTION)));
          beacon.setScaned(1 == cursor.getInt(cursor.getColumnIndex(ITEM_ISSCANED)));
          beacons.add(beacon);
        } while (cursor.moveToNext());
      } else {
        LogUtils.w("当前无beacon数据");
      }
      cursor.close();
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }
//    mSqLiteDatabase.close();

    LogUtils.w(" beaconNum = " + beacons.size());
    return beacons;
  }

  /*
  * 添加一个beacon,如果beacon id相同，会替换
  * */
  public long addBeacon(Beacon beacon) {
    if (beacon == null) {
      return -1;
    }

    openDatabase();

    if (beacon.getAction() == Beacon.ACTION_ADD)//如果是新添加的beacon
      beacon.setId(NEW_BEACON_ID_TEMPORARY--);//用负值尽量避免与正常id混淆


    long num = insertBeacon(beacon);//若已存在会替换
//    if (beacon.getAction() != Beacon.ACTION_NO_CHANGE)//改变扫描状态也会插入数据库，但不需要上传
//      isDataChanged = true;
//    mSqLiteDatabase.close()
    return num;

  }

  /*
  * delete beacons
  * */
//  public void deleteBeacons(List<Beacon> beacons) {
//    if (beacons == null || beacons.size() <= 0) {
//      return;
//    }
//
//    if (mSqLiteDatabase == null) return;
//
//    List<Beacon> deleteBeacons = new ArrayList<Beacon>();
//    for (Beacon beacon : beacons) {
//      if (beacon.isSelect()) {
//        deleteBeacons.add(beacon);
//      }
//    }
//
//    if (deleteBeacons.size() > 0) { // 由待删除数据
//      if (!mSqLiteDatabase.isOpen()) {
//        mSqLiteDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
//      }
//
//      for (Beacon beacon : deleteBeacons) {
//        int minor = beacon.getMinor();
//        LogUtils.w("minor = " + minor);
//        try {
//          if (MapBeaconActivity.hasDownload)
//            mSqLiteDatabase.delete(getBeaconTableName(MapBeaconActivity.mPalMap.getMapId()), ITEM_MINOR + "=?", new String[]{String.valueOf(minor)});
//          else
//            mSqLiteDatabase.delete(TABLE_NAME_BEACON_CASH, ITEM_MINOR + "=?", new String[]{String.valueOf(minor)});
//        }catch (SQLException e){
//          DialogUtils.showLongToast(e.getMessage());
//        }
//
//        beacons.remove(beacon);
//      }
//
//      mSqLiteDatabase.close();
//      isDataChanged = true;
//    }
//
//  }
  /*
  * 将beacons对象状态置为ACTION_NO
  * 返回
  * */
  public long setActionNo(Beacon beacon,int oldBeaconID){

    beacon.setAction(Beacon.ACTION_NO_CHANGE);

    return updateBeacon(beacon,oldBeaconID);
  }

  /**
  * @Author: eric3
  * @Description: 根据sceneid,mapid删除数据
  * @Time 2016/12/15 10:33
  */
  public void deleteBeacons(long sceneId, long mapId) {
    openDatabase();

    try {
      mSqLiteDatabase.execSQL(BeaconTable.getDeleteBeaconSQL(sceneId, mapId));
//        ret += mSqLiteDatabase.delete(TABLE_NAME_BEACON_CASH, ITEM_BEACON_ID + "=?", new String[]{String.valueOf(beaconID)});
    }catch (SQLException e){
      e.printStackTrace();
      LogUtils.e("catch Exception:"+e.getMessage());
    }
  }
  /*
 * 删除beaconID对象
 * 返回删除记录数
 * */
  public void deleteBeaconByID(int beaconID) {

    openDatabase();

    try {
      mSqLiteDatabase.execSQL(BeaconTable.getDeleteBeaconSQL(BeaconTable.ITEM_BEACON_ID,beaconID));
//        ret += mSqLiteDatabase.delete(TABLE_NAME_BEACON_CASH, ITEM_BEACON_ID + "=?", new String[]{String.valueOf(beaconID)});
    }catch (SQLException e){
      e.printStackTrace();
      LogUtils.e("catch Exception:"+e.getMessage());
    }

  }

  /*
   * 生成待上传的beaconList
   * */
  public List<Beacon> getUploadBeaconList(long sceneId,long mapId,long[] floorIds) {
    if (floorIds == null || floorIds.length <= 0) {
      return null;
    }

    List<Beacon> beaconList = new ArrayList<>();
    for (int i = 0; i < floorIds.length; i++) { // 遍历，添加楼层
      List<Beacon> beacons = getBeaconsByID(sceneId, mapId, floorIds[i]);
      if (beacons != null) {
        for (Beacon beacon : beacons) { // 遍历，添加每层楼的beacon
          if (beacon.getAction() != Beacon.ACTION_NO_CHANGE) {//如果需要上传
            beaconList.add(beacon);
          }
        }
      }//if
    }

    return beaconList;
  }
  /*
  检查是否有待上传的beacon数据
   */
  public boolean hasBeaconDataUpload(long sceneId,long mapId){

    final long[] floorIds = getFloorIds(sceneId, mapId);
    List<Beacon> beaconList = getUploadBeaconList(sceneId,mapId,floorIds);
    if (beaconList == null)
      return false;
    if (beaconList.size()>0)
      return true;
    else
      return false;
  }
  /*
  * 根据sceneid mapid查询所有floorId
  * */
  public long[] getFloorIds(long sceneId,long mapId) {
//    if (mSqLiteDatabase == null) return null;
    openDatabase();
    long[] floorIds = null;
    try {
      Cursor cursor = mSqLiteDatabase.query(true, BeaconTable.NAME, new String[]{ITEM_FLOOR_ID},
          ITEM_SCENE_ID +" =? and "+ITEM_MAP_ID+" =?" , new String[]{ Long.toString(sceneId), Long.toString(mapId)}, null, null, ITEM_FLOOR_ID + " ASC", null);



    int index = 0;
    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
      floorIds = new long[cursor.getCount()];
      do {
        long id = cursor.getLong(cursor.getColumnIndex(ITEM_FLOOR_ID));
        floorIds[index++] = id;
      } while (cursor.moveToNext());
    }
    cursor.close();
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }

    return floorIds;
  }

  /**
  * @Author: eric3
  * @Description: beacon数据是否已下载,替代isTableNameExist
  * @Time 2016/12/2 17:11
  */
  public boolean isBeaconDownload(long sceneId,long mapId){
//     List<Beacon> beacons = getBeaconsOffline(sceneId, mapId);
    List<Beacon> beacons = getBeacons(sceneId, mapId);
    if (beacons==null||beacons.size()==0)
      return false;
    else
      return true;
  }

  /*
  判断表名是否存在
   */
//  public boolean isTableNameExist(String tableName) {
//    if (mSqLiteDatabase == null || !mSqLiteDatabase.isOpen()) {
//      mSqLiteDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
//    }
//    Cursor cursor =null;
//    try {
//      cursor = mSqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
//    }catch (SQLException e){
//      DialogUtils.showLongToast(e.getMessage());
//    }
//      while (cursor.moveToNext()) {
//      //遍历出表名
//      String name = cursor.getString(0);
//      if (tableName.equals(name)) {
//        return true;
//      }
//    }
//    return false;
//  }

  /*
 判断用户数据是否离线
  */
  public String getUserPwd(String userName) {
    openDatabase();

    Cursor cursor =null;
    try {
      cursor =mSqLiteDatabase.rawQuery(UserTable.getSelectItemSQL(userName,UserTable.ITEM_PWD), null);
    }catch (SQLException e){

      LogUtils.e(e.getMessage());
    }

    if (cursor == null){
      return null;
    }
    if (cursor.getCount() == 0)
      return null;

    cursor.moveToFirst();
    String pwdByte = cursor.getString(cursor.getColumnIndex(UserTable.ITEM_PWD));

    EncrypUtil des = null;
    try {
//      des = new EncrypUtil();
//      byte[] b = Base64Util.decode(pwdByte.getBytes("UTF-8"));
      String decontent = EncrypUtil.decrypt("palmap",pwdByte);

      return new String(decontent);

    } catch (Exception e) {

      e.printStackTrace();
      DialogUtils.showLongToast(e.getMessage());
    }

    return null;

  }

  /*
  清除所有缓存数据,可静态调用
   */
  public static void askAndDeleteAllTable(final Context context, final OnDeleteComplete onDeleteComplete) {

    DialogUtils.showDialog(context, "是否清空所有缓存数据", new DialogUtils.DialogCallBack() {
      @Override
      public void onOk() {
        SQLiteDatabase sqliteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = null;
        try {
          cursor =sqliteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
        }catch (SQLException e){
          DialogUtils.showLongToast(e.getMessage());
        }

        while (cursor.moveToNext()) {
          //遍历出表名
          String tableName = cursor.getString(0);
          if (tableName.equals("android_metadata") || tableName.equals("sqlite_sequence"))
            continue;
          //清除表中所有记录：
          sqliteDatabase.execSQL("DROP TABLE " + tableName);
        }
        sqliteDatabase.close();
        if (!FileUtils.DeleteFolder(Constant.OFFLINE_DATA_PATH)) {//删除离线缓存的地图数据
          DialogUtils.showLongToast("请手动删除目录" + Constant.OFFLINE_DATA_PATH);
        } else {
          DialogUtils.showLongToast("缓存已清空！");
        }
        onDeleteComplete.onComplete();
      }

      @Override
      public void onCancel() {
        return;
      }
    });

  }

  /*
     mapid对应的map是否已下载
   */
  public boolean hasDownload(long sceneId,long mapId) {
    openDatabase();

    Cursor cursor = null;
    try {
      cursor = mSqLiteDatabase.rawQuery(MapTable.getSelectItemSQL(sceneId,mapId), null);
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }

//    closeDatabase();
    if (cursor!=null&&cursor.getCount()>0)
      return true;
    else
      return false;
  }

  private void openDatabase(){
    if (mSqLiteDatabase == null || !mSqLiteDatabase.isOpen()) {
      mSqLiteDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    //之前没有创建巡检记录表，则创建
    if (!isTableExist(BeaconTable.NAME)){
      createDatabaseTable(TABLE_BEACON);
    }
    if (!isTableExist(MapTable.NAME)){
      createDatabaseTable(TABLE_MAP);
    }
    if (!isTableExist(HistoryTable.NAME)){
      createDatabaseTable(TABLE_HISTORY);
    }
    if (!isTableExist(UserTable.NAME)){
      createDatabaseTable(TABLE_USER);
    }
  }
  
  /** 
  * @Author: eric3
  * @Description: 关闭数据库，一般在app退出时调用
  * @Time 2016/12/5 10:10
  */
  public static void closeDatabase(){
    if(mSqLiteDatabase!=null)
      mSqLiteDatabase.close();
  }

  /**
   * 保存离线map
   *
   * @param
   */
  private void saveObject(long sceneId,long mapId,MapDataSerializable mapDataSerializable) {
    openDatabase();
//        Gson gson = new Gson();
//        String json = gson.toJson(mapDataSerializable);
    String json = JSON.toJSONString(mapDataSerializable);

    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
//            objectOutputStream.writeObject(mapDataSerializable);

      objectOutputStream.writeObject(json);
      objectOutputStream.flush();
      byte data[] = arrayOutputStream.toByteArray();

      data = json.getBytes();
      objectOutputStream.close();
      arrayOutputStream.close();


      mSqLiteDatabase.execSQL(MapTable.getReplaceAllItemSQL(), new Object[]{sceneId, mapId,data});
//      mSqLiteDatabase.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      DialogUtils.showLongToast("saveObject()错误,"+e.getMessage());
    }
  }

  /**
   * 获取离线map信息集合
   *
   * @param
   */
  private MapDataSerializable getObject(long sceneId,long mapId) {
    openDatabase();

    MapDataSerializable mapDataSerializable = null;

    Cursor cursor = null;
    try {
      cursor =mSqLiteDatabase.rawQuery(MapTable.getSelectItemSQL(sceneId,mapId), null);
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }

    if (cursor != null) {
      while (cursor.moveToNext()) {
        byte data[] = cursor.getBlob(cursor.getColumnIndex(MapTable.ITEM_MAP_DATA));
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
        try {

          mapDataSerializable = JSON.parseObject(data, MapDataSerializable.class);
          arrayInputStream.close();
          break;//这里为了测试就取一个数据
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          DialogUtils.showShortToast("getObject()错误,mapid = " + mapId);
        }

      }
    }
    return mapDataSerializable;

  }

  /**
   * 获取离线场景信息
   *
   * @param
   */
  public Object getObject(int TYPE, String userName) {
    openDatabase();
    byte data[] = null;
    Cursor cursor = null;
    try {
      cursor =mSqLiteDatabase.rawQuery("select * from "+UserTable.NAME+" where userName='" + userName + "'", null);
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }

    if (cursor != null) {
      cursor.moveToFirst();
      try {
        switch (TYPE) {
          case USERNAME:
            return cursor.getString(cursor.getColumnIndex(UserTable.ITEM_NAME));
          case PWD:
            return cursor.getString(cursor.getColumnIndex(UserTable.ITEM_PWD));
          case SCENEARRAY:
            data = cursor.getBlob(cursor.getColumnIndex(UserTable.ITEM_SCENES));
            break;
          case PROVINCEDATA:
            data = cursor.getBlob(cursor.getColumnIndex(UserTable.ITEM_PROVINCE));
            break;
          case CITYDATA:
            data = cursor.getBlob(cursor.getColumnIndex(UserTable.ITEM_CITY));
            break;
          case REGIONDATA:
            data = cursor.getBlob(cursor.getColumnIndex(UserTable.ITEM_REGION));
            break;
          case MAPDATA:
            data = cursor.getBlob(cursor.getColumnIndex(UserTable.ITEM_MAP));
            break;
          default:
            DialogUtils.showLongToast("字段错误：TYPE=" + TYPE);
            break;
        }
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
        Object object = inputStream.readObject();
        inputStream.close();
        arrayInputStream.close();

        return object;

      } catch (Exception e) {
        e.printStackTrace();
        DialogUtils.showShortToast("getObject()错误:" + e.getMessage());
      }

    }
    return null;
  }

  public List<Floor> getFloorList(long sceneId,long mapID) {
    MapDataSerializable mapDataSerializable = getObject(sceneId,mapID);
    if (mapDataSerializable==null)
      return null;

    return mapDataSerializable.getFloorList();
  }

  public void saveMapData(long sceneId,long mapId, MapDataSerializable m) {
    saveObject(sceneId,mapId,m);
//    mSqLiteDatabase.close();
  }

//  public static boolean isDataChanged() {
//    return isDataChanged;
//  }

//  public void setDataChanged(boolean isDataChanged) {
//    this.isDataChanged = isDataChanged;
//  }

  public void saveObject(int TYPE, Object object, String userName) {

    openDatabase();

    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
      objectOutputStream.writeObject(object);

//            objectOutputStream.writeObject(json);
      objectOutputStream.flush();
      byte data[] = arrayOutputStream.toByteArray();

//            data = json.getBytes();
      objectOutputStream.close();
      arrayOutputStream.close();

      switch (TYPE) {
        case USERNAME:
          mSqLiteDatabase.execSQL(UserTable.getInsertUserNameSQL(userName));
          break;
        case PWD:

          EncrypUtil des = new EncrypUtil();
          String msg =(String) object;

          String encontent = EncrypUtil.encrypt("palmap",msg);
//          msg = Base64Util.encode(encontent.getBytes());
          mSqLiteDatabase.execSQL(UserTable.getInsertPwdSQL(userName, encontent));
          break;
        case SCENEARRAY:
          mSqLiteDatabase.execSQL(UserTable.getUpdateItemSQL(userName,UserTable.ITEM_SCENES), new Object[]{data});
          break;
        case PROVINCEDATA:
          mSqLiteDatabase.execSQL(UserTable.getUpdateItemSQL(userName,UserTable.ITEM_PROVINCE), new Object[]{data});
          break;
        case CITYDATA:
          mSqLiteDatabase.execSQL(UserTable.getUpdateItemSQL(userName,UserTable.ITEM_CITY), new Object[]{data});
          break;
        case REGIONDATA:
          mSqLiteDatabase.execSQL(UserTable.getUpdateItemSQL(userName,UserTable.ITEM_REGION), new Object[]{data});
          break;
        case MAPDATA:
          mSqLiteDatabase.execSQL(UserTable.getUpdateItemSQL(userName,UserTable.ITEM_MAP), new Object[]{data});
          break;
        default:
          DialogUtils.showLongToast("字段错误：TYPE=" + TYPE);
          break;
      }

//      mSqLiteDatabase.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      DialogUtils.showLongToast("saveObject()错误," + e.getMessage());
    }
  }
  /*
   判断表是否存在
 */
  public boolean isTableExist(String tableName) {
    Cursor cursor =null;
    try {
      cursor = mSqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
    }catch (SQLException e){
      DialogUtils.showLongToast(e.getMessage());
    }
    if (cursor==null){
      DialogUtils.showShortToast("打开数据库失败");
      return false;
    }
    while (cursor.moveToNext()) {
      //遍历出表名
      String name = cursor.getString(0);
      if (tableName.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /*
  向数据库插入一条巡检记录
   */
  public void insertHistoryData(long sceneId,long mapId,String floorName,long floorId, long time, String userName,int activityBeaconNum, List<Beacon> beaconList) {
    openDatabase();

//    mTableName = "history_"+mallID;
    int abnormalBeaconNum = beaconList.size()-activityBeaconNum;
    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
      objectOutputStream.writeObject(beaconList);

      objectOutputStream.flush();
      byte data[] = arrayOutputStream.toByteArray();

      objectOutputStream.close();
      arrayOutputStream.close();

      //floorNAMEID,mallID,time,userName,abnormalBeaconNum,isUpload,mBeaconList
      mSqLiteDatabase.execSQL(HistoryTable.getInsertAllItemSQL(), new Object[]{sceneId, mapId, floorName,floorId, time, userName,abnormalBeaconNum,0, data});
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      DialogUtils.showLongToast("insertHistoryData()错误," + e.toString());
    }
//    mSqLiteDatabase.close();
    DialogUtils.showShortToast("巡检记录保存成功。");
  }

  public List<ScanHistorySerializable> getLocalHistoryByMapID(long sceneId,long mapID) {
    openDatabase();
    ScanHistorySerializable s ;
    List<ScanHistorySerializable> scanHistoryList = new ArrayList<ScanHistorySerializable>();

    try {

      Cursor cursor = mSqLiteDatabase.rawQuery(HistoryTable.getSelectAllItemSQL(sceneId, mapID), null);

      if (cursor != null) {
        while (cursor.moveToNext()) {
          //floorNAMEID,mallID,time,userName,abnormalBeaconNum,isUpload,mBeaconList
          s = new ScanHistorySerializable();
          s.setFloorId(cursor.getLong(cursor.getColumnIndex(HistoryTable.ITEM_FLOOR_ID)));
          s.setFloorName(cursor.getString(cursor.getColumnIndex(HistoryTable.ITEM_FLOOR_NAME)));
          s.setTime(cursor.getLong(cursor.getColumnIndex(HistoryTable.ITEM_TIME)));
          s.setMapId(cursor.getLong(cursor.getColumnIndex(HistoryTable.ITEM_MAP_ID)));
          s.setUserName(cursor.getString(cursor.getColumnIndex(HistoryTable.ITEM_USER_NAME)));
          s.setAbnormalBeaconNum(cursor.getInt(cursor.getColumnIndex(HistoryTable.ITEM_ABNORMAL_BEACON_NUM)));
          s.setUpload((cursor.getInt(cursor.getColumnIndex(HistoryTable.ITEM_IS_UPLOAD)) == 1) ? true : false);
          try {
            byte data[] = cursor.getBlob(cursor.getColumnIndex(HistoryTable.ITEM_BEACON_LIST));
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
            Object object = inputStream.readObject();
            inputStream.close();
            arrayInputStream.close();
            s.setBeaconList((List<Beacon>) object);

            scanHistoryList.add(s);

          } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showLongToast("getLocalHistoryByMapID错误：" + e.toString());
            return null;
          }


        }//while
//        mSqLiteDatabase.close();
        return scanHistoryList;

      } else {
        DialogUtils.showShortToast("没有mallID=" + mapID + "的巡检记录");
//        mSqLiteDatabase.close();

        return null;
      }
    } catch (SQLException e) {
      DialogUtils.showLongToast(e.getMessage());
    }
    return null;
  }


}
