package com.palmap.BluetoothUtils.database;

import com.palmap.BluetoothUtils.main.model.Floor;
import com.palmaplus.nagrand.data.PlanarGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric3 on 2016/9/13.
 * 序列化后用于离线地图下载数据的存储
 */
public class MapDataSerializable {
//    class FloorMap{
//        private long floorID;
//        private PlanarGraph planarGraph;
//    }

    private List<Long> mFloorMap;
    private List<Floor> mFloorList;
    private SaveDataImpl saveData;

    public MapDataSerializable() {
        super();
    }

  public List<Long> getMFloorMap() {
    return mFloorMap;
  }

  public List<Floor> getMFloorList() {
    return mFloorList;
  }

  public SaveDataImpl getSaveData() {
    return saveData;
  }

  public void setMFloorMap(List<Long> mFloorMap) {
    this.mFloorMap = mFloorMap;
  }

  public void setMFloorList(List<Floor> mFloorList) {
    this.mFloorList = mFloorList;
  }

  public void setSaveData(SaveDataImpl saveData) {
    this.saveData = saveData;
  }

  @Override
  public String toString() {
    return "MapDataSerializable{" +
        "mFloorMap=" + mFloorMap +
        ", mFloorList=" + mFloorList +
        ", saveData=" + saveData +
        '}';
  }

  public MapDataSerializable(SaveDataImpl saveData){
        mFloorList = null;
        mFloorMap = null;
        this.saveData = saveData;
    }

    public void addFloorList(List<Floor> floorList){
        this.mFloorList = floorList;
    }


    public void addFloorMapData(long floorid, PlanarGraph planarGraph){
        if (mFloorMap == null)
            mFloorMap = new ArrayList<Long>(mFloorList.size());

//        FloorMap floorMap = new FloorMap();
//        floorMap.floorID = floorid;
//        floorMap.planarGraph = planarGraph;

        mFloorMap.add(floorid);

        if(mFloorMap.size() == mFloorList.size()){
            //楼层数据下载完成
            saveData.onDataComplete();

        }
    }

    public interface SaveDataImpl{
        void onDataComplete();
    }

    public List<Floor> getFloorList() {
        return mFloorList;
    }

//    public PlanarGraph getPlanarGraph(long floorID) {
//        for (FloorMap floorMap : mFloorMap) {
//            if (floorMap.floorID == floorID)
//                return floorMap.planarGraph;
//        }
//        return null;
//    }
}
