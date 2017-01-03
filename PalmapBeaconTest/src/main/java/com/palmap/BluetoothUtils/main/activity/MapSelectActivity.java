package com.palmap.BluetoothUtils.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.database.MapDataSerializable;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.http.DataProviderCenter;
import com.palmap.BluetoothUtils.http.HttpDataCallBack;
import com.palmap.BluetoothUtils.http.HttpErrorUtil;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.Floor;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.main.model.Scene;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
import com.palmap.BluetoothUtils.map.adapter.MapListAdapter;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.widget.ActionBar;
import com.palmaplus.nagrand.core.Engine;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.LocationModel;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.Param;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.io.CacheAsyncHttpClient;
import com.palmaplus.nagrand.io.FileCacheMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MapSelectActivity extends BaseActivity implements View.OnClickListener {
  private ActionBar mActionBar;
  private ListView mListView;
  TextView mEntry;
  TextView mHistory;
  TextView mDownload;
  private MapListAdapter mMapListAdapter;
  private Scene[] mSceneList;

  private MapDataSerializable mMapDataSerializable = null;
  private List<Floor> mFloorListDataDownload;

  private boolean offlineMode;
  private Scene mScene = null; // 场景对象
  private PalMap mPalMap = null;
  private List<Beacon> mBeaconListDownload;
  private SQLiteHelper mSQLiteHelperDownload;

  private List<PalMap> mPalMapListOri;
//  private boolean isSearching = false;//是否正在搜索

  private DataSource mDataSourceDownload;//用于离线下载

  private static final String TAG = "MapSelectActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_select);

    initData(savedInstanceState);

    initActionBar();

    initView();

    if (offlineMode)
      loadDataOffline();
    else
      loadData();

  }

  /*
  *  接受传递的参数或初始化本界面数据
  * */
  private void initData(Bundle savedInstanceState) {
    mScene = new Scene();
    offlineMode = (Boolean) getIntent().getSerializableExtra(Constant.TAG_HAS_DOWNLOAD);
    // 初始化游标view
    mSQLiteHelperDownload = SQLiteHelper.getInstance(MapSelectActivity.this);

  }

  /*
    * 初始化列表
    * */
  public void initMapList(List<PalMap> palMapList) {
    if (palMapList == null) return;

    if (mMapListAdapter == null) {
      mMapListAdapter = new MapListAdapter(this, palMapList, mSceneList);
      mListView.setAdapter(mMapListAdapter);
    }
  }

  /*
  *  初始化actionBar
  * */
  private void initActionBar() {
    mActionBar = (ActionBar) findViewById(R.id.action_bar);
    mActionBar.setRightBg("搜索",R.color.theme_bg_color);
    mActionBar.setTitle(mScene == null ? "点击场景可折叠列表" : mScene.getSceneName());
    mActionBar.setLeftVisible(true);
    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
      @Override
      public void onLeft() {
        MapSelectActivity.this.finishActivityOnAnimation(MapSelectActivity.this);
      }

      @Override
      public void onRight() {
        if (!mActionBar.isSearching()){

         mActionBar.showSearchBox(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (mMapListAdapter!=null)
                 mMapListAdapter.refeshSceneMapByKeyWord(s.toString());
           }

           @Override
           public void afterTextChanged(Editable s) {
           }
         });
        }else{
          mActionBar.hideSearchBox();
        }
      }
    });
  }

  /*
  *  初始化view控件
  * */
  private void initView() {
    mEntry = (TextView) findViewById(R.id.mall_select_entry);
    mEntry.setOnClickListener(this);
    mEntry.setClickable(false);
    mEntry.setBackgroundResource(R.drawable.btn_bg_shape);

    mHistory = (TextView) findViewById(R.id.mall_select_history);
    mHistory.setOnClickListener(this);
    mHistory.setClickable(false);
    mHistory.setBackgroundResource(R.drawable.btn_bg_shape);

    mDownload = (TextView) findViewById(R.id.mall_select_download);
    mDownload.setOnClickListener(this);
    mDownload.setClickable(false);
    mDownload.setBackgroundResource(R.drawable.btn_bg_shape);

    mListView = (ListView) findViewById(R.id.map_list);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mPalMap = mMapListAdapter.getMapByPosition(position);  //不能用mPalMapList.get(position);

        if (mActionBar.isSearching()&&mPalMap.getMapId()==MapListAdapter.SCENE_IDENTIFIER)
          return;//搜索时不能折叠

        mMapListAdapter.setSelect(position);

        refeshButtonState();


        for (int i = 0; i < mSceneList.length; i++) {
          if (mSceneList[i].getSceneId() == mPalMap.getSceneId())
            mScene = mSceneList[i];
        }
        if (!mActionBar.isSearching())
        mActionBar.setTitle(mScene == null ? "点击场景可折叠列表" : mScene.getSceneName());

        if (Constant.isDebug)
          ;
//          DialogUtils.showShortToast("mapID=" + mPalMap.getMapId() + " scene=" + mScene.getSceneName() + " id=" + mScene.getSceneId());


      }
    });

  }

  /*
   *  加载场景数据
   * */
  private void loadData() {
    showProgress("提示", "加载场景列表中...");
    DataProviderCenter.getInstance().getScenes(BluetoothUtilsApplication.userName, null, new HttpDataCallBack<String>() {
      @Override
      public void onError(int errorCode) {
        LogUtils.e("errorCode = " + errorCode);
        closeProgress();
        if (errorCode == ErrorCode.CODE_NO_INTERNET) {
          DialogUtils.showShortToast("无网络连接！");
        } else {
          DialogUtils.showShortToast("网络连接错误！");
        }
      }

      @Override
      public void onComplete(String content) { // 返回JSONArray型数据
        closeProgress();
        try {
          JSONArray jsonArray = new JSONArray(content);
          if (jsonArray != null && jsonArray.length() > 0) {
            mSceneList = Scene.getSceneArray(jsonArray);

            loadMapListData();

          } else {
            DialogUtils.showShortToast("返回数据有误！");
          }
        } catch (JSONException e) {
          e.printStackTrace();
          DialogUtils.showShortToast("返回数据有误！");
        }
      }
    });
  }

  /*
   *  加载离线场景数据
   * */
  private void loadDataOffline() {
    showProgress("提示", "加载场景列表中...");
    SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(this);
    mSceneList = (Scene[]) sqLiteHelper.getObject(SQLiteHelper.SCENEARRAY, BluetoothUtilsApplication.userName);

    mPalMapListOri = (List<PalMap>) sqLiteHelper.getObject(SQLiteHelper.MAPDATA, BluetoothUtilsApplication.userName);

    initMapList(mPalMapListOri);
    closeProgress();
  }

  /*
  *  加载数据
  * */
  private void loadMapListBySceneID(final int position) {

    if (position >= mSceneList.length || mSceneList[position].getSceneId() == 0)
      return;

    showProgress("提示", "加载地图列表中(" + (position) + "/" + (mSceneList.length - 1) + ")...");
    DataProviderCenter.getInstance().getMaps(mSceneList[position].getSceneId(), new HttpDataCallBack<String>() {
      @Override
      public void onError(int errorCode) {
        LogUtils.e("errorCode = " + errorCode);
        HttpErrorUtil.showErrorToast(errorCode);
      }

      @Override
      public void onComplete(String content) {
        try {
          JSONObject obj = new JSONObject(content);
          JSONArray array = obj.getJSONArray("list");
          if (mPalMapListOri == null)
            mPalMapListOri = new ArrayList<PalMap>();

          mPalMapListOri.add(new PalMap(mSceneList[position].getSceneName(), MapListAdapter.SCENE_IDENTIFIER, mSceneList[position].getSceneId(), "▽  场景"));
          mPalMapListOri = PalMap.addMalls(array, mPalMapListOri,mSceneList[position].getAppKey());
          if (position + 1 == mSceneList.length) {

            saveDataOffline();//保存离线

            initMapList(mPalMapListOri);
            closeProgress();
            return;
          } else {
            loadMapListBySceneID(position + 1);
          }

        } catch (JSONException e) {
          e.printStackTrace();
          DialogUtils.showLongToast(e.getMessage());
        }
      }
    });
  }

  /*
  *  加载所有sceneID对应的数据
  * */
  private void loadMapListData() {
    if (mSceneList == null) return;

    loadMapListBySceneID(1);


  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.mall_select_entry: // 进入地图


        Intent i = new Intent(MapSelectActivity.this, MapBeaconActivity.class);
        i.putExtra(Constant.TAG_MAP_OBJECT, mPalMap);
//        i.putExtra(Constant.TAG_APPKEY, mScene.getAppKey());
        //判断是否已离线
        i.putExtra(Constant.TAG_HAS_DOWNLOAD, mSQLiteHelperDownload.hasDownload(mPalMap.getSceneId(),mPalMap.getMapId()));

        MapSelectActivity.this.startActivityOnAnimation(i);
        break;

      case R.id.mall_select_history: // 进入巡检记录
        Intent ii = new Intent(MapSelectActivity.this, ScanHistoryActivity.class);
        ii.putExtra(Constant.TAG_MAP_OBJECT, mPalMap);
        MapSelectActivity.this.startActivityOnAnimation(ii);

        break;
      case R.id.mall_select_download: // 下载当前mapid对应的楼层数据、地图数据
        initEngine();//下载地图数据需要datasource
        downloadFloorData_SDK();//


        break;
    }


  }

  /*
*  离线下载，根据appkey初始化引擎
* */
  private void initEngine() {
    if (mScene == null)
      return;

    Engine engine = Engine.getInstance();
    engine.startWithLicense(mScene.getAppKey(), this); // 设置验证license

    // 地图等服务数据源,依赖于Engine，需要通过license验证之后才能正常使用。
    //添加带缓存的DataSource
    CacheAsyncHttpClient cacheAsyncHttpClient = new CacheAsyncHttpClient(Constant.SERVER_URL);
    FileCacheMethod fileCacheMethod = new FileCacheMethod(Constant.OFFLINE_DATA_PATH);
    cacheAsyncHttpClient.reset(fileCacheMethod);
    mDataSourceDownload = new DataSource(cacheAsyncHttpClient);
//    mDataSourceDownload = new DataSource(Constant.SERVER_URL);

    //离线下载存储类
    mMapDataSerializable = new MapDataSerializable(new MapDataSerializable.SaveDataImpl() {
      @Override
      public void onDataComplete() {//离线数据下载完成，接下来存入数据库

        showProgress("提示", "正在存入数据库...");
        mSQLiteHelperDownload.saveMapData(mPalMap.getSceneId(),mPalMap.getMapId(), mMapDataSerializable);

        //下载beacon数据
        downloadBeaconDataAPIV2();

      }
    });
  }

  /*
  * TODO 利用SDK接口下载楼层数据
  * */
  private void downloadFloorData_SDK() {

    // mPalMap.getMapId()拿到的时mallID，请求楼层数据需要poiId
    if (mPalMap != null && mPalMap.getMapId() != -1) {

      final long mapID = mPalMap.getMapId();
      showProgress("提示", "获取地图数据中...");
      mDataSourceDownload.requestMap(mapID, new DataSource.OnRequestDataEventListener<MapModel>() {
        @Override
        public void onRequestDataEvent(DataSource.ResourceState state, MapModel mapModel) {

          LogUtils.w("mallId->state = " + state);
          if (state == DataSource.ResourceState.CACHE ||
              state == DataSource.ResourceState.ok) {
            final long floorID = MapModel.POI.get(mapModel);

            showProgress("提示", "获取楼层数据中...");
            mDataSourceDownload.requestPOIChildren(floorID, new DataSource.OnRequestDataEventListener<LocationList>() {
              @Override
              public void onRequestDataEvent(DataSource.ResourceState resourceState, final LocationList data) {
                LogUtils.w("floorId->resourceState = " + resourceState);
                if (resourceState == DataSource.ResourceState.CACHE ||
                    resourceState == DataSource.ResourceState.ok) {

                  if (data != null && data.getSize() > 0) {
                    mFloorListDataDownload = new ArrayList<Floor>(data.getSize());
                    Param<Long> id = new Param<Long>("id", Long.class);
                    Param<String> name = new Param<String>("name", String.class);
                    Param<String> address = new Param<String>("address", String.class);
                    for (int i = 0; i < data.getSize(); i++) {
                      LocationModel poi = data.getPOI(i);
                      Floor floor = new Floor();
                      floor.setId(id.get(poi));
                      floor.setName(name.get(poi));
                      floor.setAlias(address.get(poi));
                      mFloorListDataDownload.add(floor);
                    }

                    //创建序列化类，存储到数据库
                    mMapDataSerializable.addFloorList(mFloorListDataDownload);
                    downloadPlanarGraph(0);


//                        initFloorListView(); // 初始化楼层列表数据
//                        mFloorIndex = getIndexByFloorId(1672); // 初始化第一次加载哪个楼层  ps。。。。。1672是什么gui
//                        String fname = String.valueOf(mFloorListData.get(mFloorIndex).getAlias());

//                        mFloorName.setText(fname);
//                        loadMap(mFloorListData.get(mFloorIndex).getId());
                  }


                } else {
                  closeProgress();
                  DialogUtils.showLongToast("floorID = " + floorID + "不正确，请检查。");
                }
              }
            });
          } else {
            LogUtils.e("地图数据下载失败：" + state);
            closeProgress();
            DialogUtils.showLongToast("下载失败:" + state + ",mapID=" + mapID + ",请检查。");
          }
        }
      });

    } else {

      DialogUtils.showShortToast("mapID不存在，无法下载。请检查。");
    }
  }

  /*
   * 离线下载beacon数据
   * 用工作线程
   * */
  private void downloadBeaconDataAPIV2() {
    if (mScene != null && mPalMap != null) {
      showProgress("提示", "下载Beacon数据中...");
      DataProviderCenter.getInstance().getBeaconsAPIV2(mPalMap.getMapId(), mScene.getSceneId(), new HttpDataCallBack<String>() {
        @Override
        public void onError(int errorCode) {
          LogUtils.e("errorCode = " + errorCode);
          closeProgress();
          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
            DialogUtils.showShortToast("无网络连接！");
          } else {
            DialogUtils.showShortToast("网络连接错误！");
          }
        }

        @Override
        public void onComplete(String content) {
          closeProgress();
          try {
            JSONObject object = new JSONObject(content);

            // TODO 解析返回beacon数据，并存入本地数据库
            mBeaconListDownload = Beacon.getBeaconListAPIV2(object);

            showProgress("提示", "beacon数据正在插入数据库，请稍后...");
            Executors.newSingleThreadExecutor().execute(new Runnable() {
              @Override
              public void run() {
                mSQLiteHelperDownload.insertBeacons(mBeaconListDownload);
//                if (mBeaconListDownload != null && mBeaconListDownload.size() > 0) {
//                  Beacon beacon = mBeaconListDownload.get(0);
//                  ((MapFragment) mBeaconListDownload.get(0)).setUUIDAndMajor(beacon.getUuid(), beacon.getMajor());
//                }

//                mBeaconListDownload = mSQLiteHelperDownload.getBeaconsByID(((MapFragment) mBeaconListDownload.get(0)).mFloorId);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    closeProgress();
                    DialogUtils.showLongToast("离线下载成功！");
                    refeshButtonState();
                  }
                });
              }
            });

          } catch (JSONException e) {
            e.printStackTrace();
            DialogUtils.showShortToast("beacon点位数据错误");
          }
//
//          mBeaconListFragment.updateBeaconNum("待巡检");
//          //初始化便于修改巡检状态
//          mBeaconListFragment.initBeaconList(mBeaconList);
        }
      });
    } else {
      DialogUtils.showShortToast("数据传输有误，请返回，重新进入！");
    }
  }


  /*
  * TODO 下载地图 - 根据floorId
  * */
  private void downloadMap(final int i, final long floorId) {

    LogUtils.w("开始下载楼层地图，floorId = " + floorId);

    mDataSourceDownload.requestPlanarGraph(floorId, new DataSource.OnRequestDataEventListener<PlanarGraph>() {
      @Override
      public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {

        if (resourceState == DataSource.ResourceState.CACHE ||
            resourceState == DataSource.ResourceState.ok) {
          LogUtils.w("resourceState = " + resourceState);

          //添加到类当中
          mMapDataSerializable.addFloorMapData(floorId, planarGraph);

          downloadPlanarGraph(i + 1);

        } else {
          closeProgress();
          DialogUtils.showShortToast("地图数据下载失败！请重试。");
        }

      }
    });
  }

  /* 根据floorlist的floorid下载planarGraph
  * @param i mFloorListDataDownload的下标
  */
  public void downloadPlanarGraph(int i) {
    if (mFloorListDataDownload == null)
      return;

    if (i < mFloorListDataDownload.size()) {

      showProgress("提示", "下载楼层数据中（" + (i + 1) + "/" + mFloorListDataDownload.size() + "）...");

      downloadMap(i, mFloorListDataDownload.get(i).getId());


    }

  }

  /*
    更新button状态
     */
  private void refeshButtonState() {

    if (mPalMap == null||mPalMap.getMapId()==MapListAdapter.SCENE_IDENTIFIER) {
      mEntry.setClickable(false);
      mEntry.setBackgroundResource(R.drawable.btn_bg_shape);

      mDownload.setText("未选择地图");
      mDownload.setClickable(false);
      mDownload.setBackgroundResource(R.drawable.btn_bg_shape);

      mHistory.setClickable(false);
      mHistory.setBackgroundResource(R.drawable.btn_bg_shape);
    } else {
      mEntry.setClickable(true);
      mEntry.setBackgroundResource(R.drawable.btn_bg_shape_hover);

      mHistory.setClickable(true);
      mHistory.setBackgroundResource(R.drawable.btn_bg_shape_hover);

      mDownload.setClickable(true);
      mDownload.setBackgroundResource(R.drawable.btn_bg_shape_hover);

      //判断是否已离线
      if (mSQLiteHelperDownload.hasDownload(mPalMap.getSceneId(),mPalMap.getMapId())) {

        mDownload.setText("已下载,点击可更新");
      } else {
        mDownload.setText("离线下载");
      }

    }


  }

  /*
   保存mMallData
    */
  private void saveDataOffline() {
    //离线
    SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(MapSelectActivity.this);
//    sqLiteHelper.saveObject(SQLiteHelper.PROVINCEDATA, mProvinceData, BluetoothUtilsApplication.userName);
//    sqLiteHelper.saveObject(SQLiteHelper.CITYDATA, mCityData, BluetoothUtilsApplication.userName);
//    sqLiteHelper.saveObject(SQLiteHelper.REGIONDATA, mRegionData, BluetoothUtilsApplication.userName);


    sqLiteHelper.saveObject(SQLiteHelper.SCENEARRAY, mSceneList, BluetoothUtilsApplication.userName);
    sqLiteHelper.saveObject(SQLiteHelper.MAPDATA, mPalMapListOri, BluetoothUtilsApplication.userName);
  }

}
