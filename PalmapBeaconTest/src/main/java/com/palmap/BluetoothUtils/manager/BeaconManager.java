package com.palmap.BluetoothUtils.manager;

import android.app.Activity;
import android.content.Context;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.Floor;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.main.model.ScanParameters;

import java.util.List;

/**
 * Created by eric3 on 2016/12/6.
 * 管理当前地图的beaconList数据,包含数据库操作
 */

public class BeaconManager {
  private Context mContext;
//  private PalMap mPalMap;
  private List<Beacon> mBeaconList;//当前floor beacon列表
  private SQLiteHelper mSQLiteHelper; // 用于操作beacon数据库

//  private int beaconNum = -1;//当前floor beacon数量
  private int activeBeaconNum = 0;//检测到的beacon数量

  //当前待扫描的参数
  private ScanParameters scanParameters;
//  private HashSet<String> uuids;
//  private HashSet<Integer> majors;
  private long floorId;//当前的floorId
  private long[] floorIds;
  private long mapId;
  private long sceneId;
  private boolean isFromHistory;
  private ScanHistorySerializable scanHistorySerializable;

  public BeaconManager(Context context, PalMap map,boolean isFromHistory) {
    this.mContext = context;
    this.sceneId = map.getSceneId();
    this.mapId = map.getMapId();
    this.isFromHistory = isFromHistory;
    if (isFromHistory) {
      this.scanHistorySerializable = (ScanHistorySerializable) ((Activity)mContext).getIntent().getSerializableExtra(Constant.TAG_BEACON_HISTORY);
    }else{
      this.scanHistorySerializable = null;
    }
    // 初始化
    this.mSQLiteHelper = SQLiteHelper.getInstance(context);
    this.floorIds = getFloorIds();
//    this.uuids = new HashSet<>() ;
//    this.majors = new HashSet<>();
    this.scanParameters = new ScanParameters();
  }

  public void initBeaconListFromJson(org.json.JSONObject json){
    mBeaconList = Beacon.getBeaconListAPIV2(json);
  }

  /**
  * @Author: eric3
  * @Description: 更改扫描状态,写入数据库
  * @Time 2016/12/6 10:26
  */
  public void setAllScaned(boolean isScaned){
    if (mBeaconList==null||mBeaconList.size()<0){
      return;
    }

    for (Beacon b:mBeaconList){
      if (b.isScaned()!=isScaned) {
        b.setScaned(isScaned);
        addBeacon(b);//替换
      }
    }
  }

  /**
  * @Author: eric3
  * @Description: 将mBeaconList存入数据库的beacon表中
  * @Time 2016/12/6 10:48
  */
  public void saveBeaconsToDb(){
    mSQLiteHelper.createDatabaseDefault();
    mSQLiteHelper.deleteBeacons(sceneId, mapId);//先删除再插入
    mSQLiteHelper.insertBeacons(mBeaconList);

  }
  private void clearUUIDAndMajor() {
    scanParameters.clear();
  }
  private void addUUIDAndMajor(String uuid, int major) {
//    this.uuids.add(uuid);
//    this.majors.add(major);
    scanParameters.add(uuid, major);
  }

  public ScanParameters getScanParameters() {
    return scanParameters;
  }

  public int  getDefaultMajor() {
    if(scanParameters.getItemList().size()>0){
      return scanParameters.getItemList().get(0).getMajor();
    }else
      return 0;
  }

  public String getDefaultUuid() {
    if(scanParameters.getItemList().size()>0){
      return scanParameters.getItemList().get(0).getUuid();
    }else
      return "UUID";
  }



  public long getMapId() {
    return mapId;
  }

  public long getSceneId() {
    return sceneId;
  }

  public List<Beacon> getBeaconList() {

    return mBeaconList;
  }

  /**
  * @Author: eric3
  * @Description: 根据floorid更新manager的beacon集合
  * @Time 2016/12/6 12:09
  */
  public void refeshBeaconsByFloorId(long floorId){
    mBeaconList = mSQLiteHelper.getBeaconsByID(sceneId, mapId,floorId);
    this.floorId = floorId;
    clearUUIDAndMajor();
    if (mBeaconList != null && mBeaconList.size() > 0) {
      for (Beacon beacon:mBeaconList) {
        addUUIDAndMajor(beacon.getUuid(), beacon.getMajor());
      }

      if (isFromHistory&&scanHistorySerializable!=null){//匹配巡检记录
        if (scanHistorySerializable.getFloorId()==floorId){
          for (Beacon beacon:mBeaconList) {
            for (Beacon b:scanHistorySerializable.getBeaconList()) {
              if (b.getId()==beacon.getId()&&
                  b.getMajor()==beacon.getMajor()&&
                  b.getMinor()==beacon.getMinor()&&
                  b.getUuid().equals(beacon.getUuid())&&b.isScaned()){
                beacon.setScaned(true);//匹配beaconId，满足已巡检
                beacon.setPowerPercent(b.getPowerPercent());
                beacon.setName(b.getName());
                addBeacon(beacon);
                break;
              }
            }
          }
          scanHistorySerializable = null;//仅加载一次
        }
      }
    }
  }

  private long[] getFloorIds(){
    return mSQLiteHelper.getFloorIds(sceneId, mapId);
  }

  /**
  * @Author: eric3
  * @Description: 获取待上传的beacon集合
  * @Time 2016/12/6 11:09
  */
  public List<Beacon> getUploadBeaconList(){
    return mSQLiteHelper.getUploadBeaconList(sceneId, mapId,floorIds);
  }

  /**
  * @Author: eric3
  * @Description: 获取当前地图中beacon总数
  * @Time 2016/12/6 11:11
  */
  public int getBeaconNum(){
    return mBeaconList != null ? mBeaconList.size() : -1;
  }

  /**
   * @Author: eric3
   * @Description: 获取当前地图中异常beacon数量
   * @Time 2016/12/6 11:11
   */
  public String getAbnormalBeaconNum(){
    return getActiveBeaconNum()==0 ? "待巡检" : getBeaconNum()-getActiveBeaconNum()+"";
  }

  public List<Floor> getFloorList(){
    return mSQLiteHelper.getFloorList(sceneId, mapId);
  }

  /**
  * @Author: eric3
  * @Description: 是否有待上传的beacon数据
  * @Time 2016/12/6 12:05
  */
  public boolean hasBeaconDataUpload(){
    return mSQLiteHelper.hasBeaconDataUpload(sceneId, mapId);
  }

  /**
  * @Author: eric3
  * @Description: 巡检结束，保存巡检记录
  * @Time 2016/12/6 12:11
  */
  public void saveScanHistory(String floorName){
    mSQLiteHelper.insertHistoryData(sceneId,mapId,floorName ,
        floorId, System.currentTimeMillis(), BluetoothUtilsApplication.userName,
        activeBeaconNum, mBeaconList);

  }

  /**
  * @Author: eric3
  * @Description: 从数据库删除beacon
  * @Time 2016/12/6 12:20
  */
  public void deleteBeacon(Beacon b){
    mSQLiteHelper.deleteBeaconByID(b.getId());
    mBeaconList.remove(b);
  }

//  /**
//  * @Author: eric3
//  * @Description: 删除数据库中某场景下某地图的所有beacon数据
//  * @Time 2016/12/15 10:29
//  */
//  public void deleteMapBeaconsInDb(){
//    mSQLiteHelper.deleteBeacons(sceneId,mapId);
//  }

 /*
   * 在当前楼层的beacon列表中搜索id值的beacon数据
   */
  public Beacon getBeaconById(int Id) {
    for (Beacon beacon : mBeaconList) {
      if (beacon.getId() == Id)
        return beacon;
    }
    return null;
  }

  /*
  获得巡检到的beacon数量
  */
  public int getActiveBeaconNum() {
    return activeBeaconNum;
  }

  /*
 获得巡检到的beacon数量
 */
  public void setActiveBeaconNum(int num) {
     this.activeBeaconNum = num;
  }

  /*
* 在当前楼层的beacon列表中搜索minor值的beacon数据
*/
  public Beacon getBeaconByMinor(int minor) {
    for (Beacon beacon : mBeaconList) {
      if (beacon.getMinor() == minor)
        return beacon;
    }
    return null;
  }

  public boolean isBeaconDownload(){
    return mSQLiteHelper.isBeaconDownload(sceneId,mapId);
  }

  /**
  * @Author: eric3
  * @Description:  向数据库添加一个beacon,如果beacon id相同，会替换
   * 返回影响的行号
  * @Time 2016/12/6 13:59
  */
  public long addBeacon(Beacon beacon){
    return mSQLiteHelper.addBeacon(beacon);
  }

  /**
  * @Author: eric3
  * @Description: 将beacons对象状态置为ACTION_NO
  * @Time 2016/12/6 14:09
  */
  public long setActionNo(Beacon b,int beaconId){
    return mSQLiteHelper.setActionNo(b,beaconId);
  }

}
