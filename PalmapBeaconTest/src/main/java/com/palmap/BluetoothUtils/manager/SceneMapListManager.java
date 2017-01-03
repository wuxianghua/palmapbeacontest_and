package com.palmap.BluetoothUtils.manager;

import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.main.model.Scene;
import com.palmap.BluetoothUtils.map.adapter.MapListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric3 on 2016/11/30.
 * mapselectActivity场景地图列表折叠管理
 */

public class SceneMapListManager {
  private static final boolean AUTO_SHOW_MAP = true;//初始化完成默认展开场景显示地图

  private List<PalMap> mPalMapListOri;//原始map列表数据
  private List<PalMap> mPalMapListCur;//经过关键词过滤、场景折叠过滤后用于展示的map列表数据
  private List<SceneState> mSceneStateList;

  private class SceneState{//记录折叠状态
    Scene scene;
    boolean isShow;

    public SceneState(Scene scene, boolean isShow) {
      this.scene = scene;
      this.isShow = isShow;
    }

    public void setShow(boolean show) {
      isShow = show;
    }

    public void changeShow() {
      isShow = !isShow;
    }
  }

  public SceneMapListManager(List<PalMap> mPalMapListOri, Scene[] mSceneStateList) {
    this.mPalMapListOri = mPalMapListOri;
    initSceneList(mSceneStateList);
  }

  private void initSceneList(Scene[] s){
    mSceneStateList = new ArrayList<>();
    SceneState ss;
    for (int i=0;i<s.length;i++){
      ss = new SceneState(s[i],AUTO_SHOW_MAP);
      mSceneStateList.add(ss);
    }
    refeshCurListBySceneState();
  }

  /**
  * @Author: eric3
  * @Description: 搜索前展开所有地图
  * @Time 2016/12/27 16:58
  */
  private void setAllShow(){
    if (mSceneStateList==null)
      return;

    for (SceneState s:mSceneStateList){
      s.setShow(true);
    }
  }

  /**
  * @Author: eric3
  * @Description: 根据场景的折叠状态刷新map列表
  * @Time 2016/12/26 10:55
  */
  private void refeshCurListBySceneState() {
    if (mPalMapListOri==null)
      return;

    PalMap tp=null;
    mPalMapListCur = new ArrayList<>();
    for (int i=0;i<mPalMapListOri.size();i++){
      tp = mPalMapListOri.get(i);
      if (tp.getMapId()== MapListAdapter.SCENE_IDENTIFIER){
        mPalMapListCur.add(tp);
      }else{//是map
        if (getIsShowByID(tp.getSceneId())){
          mPalMapListCur.add(tp);
        }
      }
    }
  }


  /**
  * @Author: eric3
  * @Description: 点击场景item后更新map列表
  * @Time 2016/12/26 10:56
  */
  public List<PalMap> refeshListById(long sceneId) {

    for (SceneState s: mSceneStateList){
      if (s.scene.getSceneId()==sceneId){
        s.changeShow();
        break;
      }
    }

    refeshCurListBySceneState();

    return mPalMapListCur;
  }

  /**
  * @Author: eric3
  * @Description: 根据关键词返回匹配的结果
  * @Time 216/12/27 12:26
  */
  public List<PalMap> refeshListByKeyWords(String key) {
    if (mPalMapListOri==null)
      return null;

    setAllShow();
    PalMap tp=null;
    long sceneIdToAdd=0;//待添加的sceneId
    PalMap sceneToAdd = null;//待添加的场景
    mPalMapListCur = new ArrayList<>();
    for (int i=0;i<mPalMapListOri.size();i++){
      tp = mPalMapListOri.get(i);
      if (tp.getMapId() == MapListAdapter.SCENE_IDENTIFIER){
        //是scene
        if (tp.getName().contains(key)){
          //scene匹配上关键字
          mPalMapListCur.add(tp);
          sceneIdToAdd = tp.getSceneId();
        }else {//scene不匹配关键字
          sceneToAdd = tp;
          sceneIdToAdd = 0;
        }




      }else {//是map
        if (tp.getSceneId()==sceneIdToAdd){
          //当前sceneId匹配关键字，所有map显示
          mPalMapListCur.add(tp);
        }else {
          if (tp.getName().contains(key)||tp.getProvinceName().contains(key)){
            //匹配上map
            if (sceneToAdd!=null) {
              mPalMapListCur.add(sceneToAdd);
              sceneToAdd = null;
            }
            mPalMapListCur.add(tp);
          }else {

          }
        }
      }
    }

    return mPalMapListCur;
  }


  private boolean getIsShowByID(long id){
    for (SceneState s: mSceneStateList){
      if (s.scene.getSceneId()==id){
       return s.isShow;
      }
    }
    return false;
  }

  public List<PalMap> getmPalMapListOri() {
    return mPalMapListOri;
  }

}
