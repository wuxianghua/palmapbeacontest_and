//package com.palmap.BluetoothUtils.main.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
//import com.palmap.BluetoothUtils.R;
//import com.palmap.BluetoothUtils.base.BaseActivity;
//import com.palmap.BluetoothUtils.database.MapDataSerializable;
//import com.palmap.BluetoothUtils.database.SQLiteHelper;
//import com.palmap.BluetoothUtils.http.DataProviderCenter;
//import com.palmap.BluetoothUtils.http.HttpDataCallBack;
//import com.palmap.BluetoothUtils.http.HttpErrorUtil;
//import com.palmap.BluetoothUtils.http.model.ErrorCode;
//import com.palmap.BluetoothUtils.main.adapter.ListWheelAdapter;
//import com.palmap.BluetoothUtils.main.constant.Constant;
//import com.palmap.BluetoothUtils.main.model.Beacon;
//import com.palmap.BluetoothUtils.main.model.City;
//import com.palmap.BluetoothUtils.main.model.Floor;
//import com.palmap.BluetoothUtils.main.model.PalMap;
//import com.palmap.BluetoothUtils.main.model.Province;
//import com.palmap.BluetoothUtils.main.model.Region;
//import com.palmap.BluetoothUtils.main.model.Scene;
//import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
//import com.palmap.BluetoothUtils.tools.DialogUtils;
//import com.palmap.BluetoothUtils.widget.ActionBar;
//import com.palmap.BluetoothUtils.widget.widget.OnWheelScrollListener;
//import com.palmap.BluetoothUtils.widget.widget.WheelView;
//import com.palmaplus.nagrand.core.Engine;
//import com.palmaplus.nagrand.data.DataSource;
//import com.palmaplus.nagrand.data.LocationList;
//import com.palmaplus.nagrand.data.LocationModel;
//import com.palmaplus.nagrand.data.MapModel;
//import com.palmaplus.nagrand.data.Param;
//import com.palmaplus.nagrand.data.PlanarGraph;
//import com.palmaplus.nagrand.io.CacheAsyncHttpClient;
//import com.palmaplus.nagrand.io.FileCacheMethod;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//
///**
// * Created by zhang on 2015/10/14.
// */
//public class MallSelectActivity extends BaseActivity implements OnWheelScrollListener, View.OnClickListener {
//  private static final int PROVINCE = 0;
//  private static final int CITY = 1;
//  private static final int REGION = 2;
//  private static final int MALL = 3;
//  private ActionBar mActionBar;
//  private WheelView mProvince;
//  private WheelView mCity;
//  private WheelView mRegion;
//  private WheelView mPalMap;
//  private TextView mEntry;
//  private TextView mHistory;
//  private TextView mDownload;
//  private boolean offlineMode;
//
//  private Scene mScene = null; // 场景对象
//  private int mShowNum = 2; // wheelView显示个数: mShowNum * 2 + 1
//  private List<Province> mProvinceData;
//  private List<City> mCityData;
//  private List<Region> mRegionData;
//  private List<PalMap> mMallData;
//
//  private List<Beacon> mBeaconListDownload;
//
//  private MapDataSerializable mMapDataSerializable = null;
//  private List<Floor> mFloorListDataDownload;
//
//  private SQLiteHelper mSQLiteHelperDownload;
//
//  private DataSource mDataSourceDownload;//用于离线下载
//  private static final String TAG = "MallSelectActivity";
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_mall_select);
//
//    initData(savedInstanceState);
//
//    initActionBar();
//
//    initView();
//
//    if (offlineMode) {
//      loadDataOffline(PROVINCE);
//    } else {
//      loadData(); // 第一次进入初始化数据
//    }
//
//  }
//
//  /*
// *  加载离线场景数据,data代表要加载的数据类型
// * */
//  private void loadDataOffline(int DATA) {
//    SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
//    switch (DATA) {
//      case PROVINCE:
//        mProvinceData = (List<Province>) sqLiteHelper.getObject(SQLiteHelper.PROVINCEDATA, BluetoothUtilsApplication.userName);
//        LogUtils.i("MallSelectActivity", "provinces: " + mProvinceData.toString());
//        mProvince.setViewAdapter(new ListWheelAdapter<Province>(MallSelectActivity.this, mProvinceData));
//        mProvince.setCurrentItem(1);
//      case CITY:
//        mCityData = (List<City>) sqLiteHelper.getObject(SQLiteHelper.CITYDATA, BluetoothUtilsApplication.userName);
//        LogUtils.i("MallSelectActivity", "cities: " + mCityData.toString());
//        mCity.setViewAdapter(new ListWheelAdapter<City>(MallSelectActivity.this, mCityData));
//        mCity.setCurrentItem(1);
//      case REGION:
//        mRegionData = (List<Region>) sqLiteHelper.getObject(SQLiteHelper.REGIONDATA, BluetoothUtilsApplication.userName);
//        LogUtils.i("MallSelectActivity", "region: " + mRegionData.toString());
//        mRegion.setViewAdapter(new ListWheelAdapter<Region>(MallSelectActivity.this, mRegionData));
//        mRegion.setCurrentItem(1);
//      case MALL:
//        mMallData = (List<PalMap>) sqLiteHelper.getObject(SQLiteHelper.MAPDATA, BluetoothUtilsApplication.userName);
//        LogUtils.i("MallSelectActivity", "mall: " + mMallData.toString());
//        mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData));
//        mPalMap.setCurrentItem(1);
//    }
//    refeshButtonState();
//  }
//
//  /*
//  *  接受传递的参数或初始化本界面数据
//  * */
//  private void initData(Bundle savedInstanceState) {
//    mScene = (Scene) getIntent().getSerializableExtra(Constant.TAG_APPKEY);
//    offlineMode = (Boolean) getIntent().getSerializableExtra(Constant.TAG_HAS_DOWNLOAD);
//    // 初始化游标view
//    mSQLiteHelperDownload = new SQLiteHelper(MallSelectActivity.this);
//
//  }
//
//  /*
//  *  初始化actionBar
//  * */
//  private void initActionBar() {
//    mActionBar = (ActionBar) findViewById(R.id.action_bar);
//    mActionBar.setTitle(mScene == null ? "选择地图" : mScene.getSceneName());
//    mActionBar.setLeftVisible(true);
//    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
//      @Override
//      public void onLeft() {
//        MallSelectActivity.this.finishActivityOnAnimation(MallSelectActivity.this);
//      }
//
//      @Override
//      public void onRight() {
//
//      }
//    });
//  }
//
//  /*
//  *  初始化view控件
//  * */
//  private void initView() {
//    mProvince = (WheelView) findViewById(R.id.mall_select_province);
//    mProvince.addScrollingListener(this);
//    mProvince.setVisibleItems(mShowNum);
//
//    mCity = (WheelView) findViewById(R.id.mall_select_city);
//    mCity.addScrollingListener(this);
//    mCity.setVisibleItems(mShowNum);
//
//    mRegion = (WheelView) findViewById(R.id.mall_select_region);
//    mRegion.addScrollingListener(this);
//    mRegion.setVisibleItems(mShowNum);
//
//    mPalMap = (WheelView) findViewById(R.id.mall_select_mall);
//    mPalMap.addScrollingListener(this);
//    mPalMap.setVisibleItems(mShowNum);
//
//    mEntry = (TextView) findViewById(R.id.mall_select_entry);
//    mEntry.setOnClickListener(this);
//    mEntry.setClickable(false);
//    mEntry.setBackgroundResource(R.drawable.btn_bg_shape);
//
//    mHistory = (TextView) findViewById(R.id.mall_select_history);
//    mHistory.setOnClickListener(this);
//    mHistory.setClickable(false);
//    mHistory.setBackgroundResource(R.drawable.btn_bg_shape);
//
//    mDownload = (TextView) findViewById(R.id.mall_select_download);
//    mDownload.setOnClickListener(this);
//    mDownload.setClickable(false);
//    mDownload.setBackgroundResource(R.drawable.btn_bg_shape);
//  }
//
//  /*
//  *  加载数据
//  * */
//  private void loadData() {
//    if (mScene != null) {
//      showProgress("提示", "加载省列表中...");
//      DataProviderCenter.getInstance().getProvinces(mScene.getSceneId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("MallSelectActivity", "errorCode = " + errorCode);
//          closeProgress();
////          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
////            DialogUtils.showShortToast("无网络连接！");
////          } else {
////            DialogUtils.showShortToast("网络连接错误！errorCode:"+errorCode);
////          }
//          HttpErrorUtil.showErrorToast(errorCode);
//        }
//
//        @Override
//        public void onOk(String content) {
//          closeProgress();
//          try {
//            JSONArray array = new JSONArray(content);
//            mProvinceData = Province.getProvinces(array);
//            LogUtils.w("MallSelectActivity", "provinces: " + mProvinceData.toString());
//            mProvince.setViewAdapter(new ListWheelAdapter<Province>(MallSelectActivity.this, mProvinceData));
//            mProvince.setCurrentItem(1);
//            updateCities(mProvinceData.get(mProvince.getCurrentItem()).getMapId());
//
//            // 初始化其它三个view
//            mCityData = City.getCities(null);
//            mCity.setViewAdapter(new ListWheelAdapter<City>(MallSelectActivity.this, mCityData));
//            mRegionData = Region.getRegions(null);
//            mRegion.setViewAdapter(new ListWheelAdapter<Region>(MallSelectActivity.this, mRegionData));
//            mMallData = PalMap.getMalls(null);
//            mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData));
//
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//      });
//    }
//  }
//
//  @Override
//  public void onScrollingStarted(WheelView wheel) {
//    // 开始滑动时，清除特定wheelView内容
//  }
//
//  /*
//  * TODO wheelView滑动事件处理
//  * */
//  @Override
//  public void onScrollingFinished(WheelView wheel) {
//    if (offlineMode) {//离线模式
//      if (wheel == mProvince) {
//        loadDataOffline(CITY);
//      } else if (wheel == mCity) {
//        loadDataOffline(REGION);
//      } else if (wheel == mRegion) {
//        loadDataOffline(MALL);
//      } else if (wheel == mPalMap) {
//        refeshButtonState();
//      }
//    } else {
//      // 开始滑动时，加载特定wheelView内容
//      if (wheel == mProvince) {
//        updateCities(mProvinceData.get(mProvince.getCurrentItem()).getMapId());
//      } else if (wheel == mCity) {
//        updateRegions(mCityData.get(mCity.getCurrentItem()).getMapId());
//      } else if (wheel == mRegion) {
//        updateMalls(mRegionData.get(mRegion.getCurrentItem()).getMapId());
//      } else if (wheel == mPalMap) {
//        refeshButtonState();
//      }
//    }
//  }
//
//  @Override
//  public void onClick(View v) {
//    switch (v.getId()) {
//      case R.id.mall_select_entry: // 进入商场
//
//        Intent i = new Intent(MallSelectActivity.this, MapBeaconActivity.class);
//        i.putExtra(Constant.TAG_MAP_OBJECT, mMallData.get(mPalMap.getCurrentItem()));
//        i.putExtra(Constant.TAG_APPKEY, mScene);
//        //判断是否已离线
//        i.putExtra(Constant.TAG_HAS_DOWNLOAD, mSQLiteHelperDownload.hasDownload(mMallData.get(mPalMap.getCurrentItem()).getMapId()));
//
//        MallSelectActivity.this.startActivityOnAnimation(i);
//        break;
//
//      case R.id.mall_select_history: // 进入巡检记录
//        Intent ii = new Intent(MallSelectActivity.this, ScanHistoryActivity.class);
//        ii.putExtra(Constant.TAG_MAP_OBJECT, mMallData.get(mPalMap.getCurrentItem()));
//        MallSelectActivity.this.startActivityOnAnimation(ii);
//
//        break;
//      case R.id.mall_select_download: // 下载当前mapid对应的楼层数据、地图数据
//        initEngine();//下载地图数据需要datasource
//        downloadFloorData_SDK();//
//
//
//
//        break;
//    }
//  }
//
//
//
//  /*
// *  离线下载，根据appkey初始化引擎
// * */
//  private void initEngine() {
//    if (mScene == null)
//      return;
//
//    Engine engine = Engine.getInstance();
//    engine.startWithLicense(mScene.getAppKey(), this); // 设置验证license
//
//    // 地图等服务数据源,依赖于Engine，需要通过license验证之后才能正常使用。
//    //添加带缓存的DataSource
//    CacheAsyncHttpClient cacheAsyncHttpClient = new CacheAsyncHttpClient(Constant.SERVER_URL);
//    FileCacheMethod fileCacheMethod = new FileCacheMethod(Constant.OFFLINE_DATA_PATH);
//    cacheAsyncHttpClient.reset(fileCacheMethod);
//    mDataSourceDownload = new DataSource(cacheAsyncHttpClient);
////    mDataSourceDownload = new DataSource(Constant.SERVER_URL);
//
//    //离线下载存储类
//    mMapDataSerializable = new MapDataSerializable(new MapDataSerializable.SaveDataImpl() {
//      @Override
//      public void onDataComplete() {//离线数据下载完成，接下来存入数据库
//
//        showProgress("提示", "正在存入数据库...");
//        mSQLiteHelperDownload.saveMapData(mMallData.get(mPalMap.getCurrentItem()).getMapId(), mMapDataSerializable);
//
//        downloadBeaconData();//下载beacon数据
//      }
//    });
//  }
//
//  /*
//  *  更新城市数据
//  * */
//  private void updateCities(String provinceCode) {
//    // 先清空
//    mCityData = City.getCities(null);
//    mCity.setViewAdapter(new ListWheelAdapter<City>(MallSelectActivity.this, mCityData));
//    mRegionData = Region.getRegions(null);
//    mRegion.setViewAdapter(new ListWheelAdapter<Region>(MallSelectActivity.this, mRegionData));
//    mMallData = PalMap.getMalls(null);
//    mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData));
//
//    if (mProvince.getCurrentItem() == 0) {
//      refeshButtonState();
//      return;
//    }
//
//    if (mScene != null) {
//      showProgress("提示", "加载城市列表中...");
//      DataProviderCenter.getInstance().getCities(provinceCode, mScene.getSceneId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("MallSelectActivity", "errorCode = " + errorCode);
//          closeProgress();
//          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
//            DialogUtils.showShortToast("无网络连接！");
//          } else {
//            DialogUtils.showShortToast("网络连接错误！");
//          }
//        }
//
//        @Override
//        public void onOk(String content) {
//          closeProgress();
//          try {
//            JSONArray array = new JSONArray(content);
//            mCityData = City.getCities(array);
//            LogUtils.w("MallSelectActivity", "cities: " + mCityData.toString());
//            mCity.setViewAdapter(new ListWheelAdapter<City>(MallSelectActivity.this, mCityData));
//            mCity.setCurrentItem(1);
//            updateRegions(mCityData.get(mCity.getCurrentItem()).getMapId());
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//      });
//    }
//  }
//
//  /*
//  *  更新地区数据
//  * */
//  private void updateRegions(String cityCode) {
//    // 先清空
//    mRegionData = Region.getRegions(null);
//    mRegion.setViewAdapter(new ListWheelAdapter<Region>(MallSelectActivity.this, mRegionData));
//    mMallData = PalMap.getMalls(null);
//    mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData));
//
//    if (mCity.getCurrentItem() == 0) {
//      refeshButtonState();
//      return;
//    }
//
//    if (mScene != null) {
//      showProgress("提示", "加载区域列表中...");
//      DataProviderCenter.getInstance().getRegions(cityCode, mScene.getSceneId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("MallSelectActivity", "errorCode = " + errorCode);
//          closeProgress();
//          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
//            DialogUtils.showShortToast("无网络连接！");
//          } else {
//            DialogUtils.showShortToast("网络连接错误！");
//          }
//        }
//
//        @Override
//        public void onOk(String content) {
//          closeProgress();
//          try {
//            JSONArray array = new JSONArray(content);
//            mRegionData = Region.getRegions(array);
//            LogUtils.w("MallSelectActivity", "regions: " + mRegionData.toString());
//            mRegion.setViewAdapter(new ListWheelAdapter<Region>(MallSelectActivity.this, mRegionData));
//            mRegion.setCurrentItem(1);
//            updateMalls(mRegionData.get(mRegion.getCurrentItem()).getMapId());
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//      });
//    }
//  }
//
//  /*
//  *  更新商场数据
//  * */
//  private void updateMalls(String regionCode) {
//    // 先清空数据
//    mMallData = PalMap.getMalls(null);
//    mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData));
//
//    if (mRegion.getCurrentItem() == 0) {
//      refeshButtonState();
//      return;
//    }
//
//    if (mScene != null) {
//      showProgress("提示", "加载商场列表中...");
//      DataProviderCenter.getInstance().getMalls(regionCode, mScene.getSceneId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("MallSelectActivity", "errorCode = " + errorCode);
//          closeProgress();
//          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
//            DialogUtils.showShortToast("无网络连接！");
//          } else {
//            DialogUtils.showShortToast("网络连接错误！");
//          }
//        }
//
//        @Override
//        public void onOk(String content) {
//          closeProgress();
//          try {
//            JSONArray array = new JSONArray(content);
//            mMallData = PalMap.getMalls(array);
//            LogUtils.w("MallSelectActivity", "malls: " + mMallData.toString());
//            mPalMap.setViewAdapter(new ListWheelAdapter<PalMap>(MallSelectActivity.this, mMallData, 12));
//            mPalMap.setCurrentItem(1);
////            mEntry.setClickable(true);
////            mEntry.setBackgroundResource(R.drawable.btn_bg_shape_hover);
////            mDownload.setClickable(true);
////            mDownload.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//            refeshButtonState();
//
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//      });
//    }
//  }
//
//  /*
//  * 离线下载beacon数据
//  * 用工作线程
//  * */
//  private void downloadBeaconData() {
//    if (mScene != null && mPalMap != null) {
//      showProgress("提示", "下载Beacon数据中...");
//      DataProviderCenter.getInstance().getBeacons_2(mMallData.get(mPalMap.getCurrentItem()).getMapId(), mScene.getSceneId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("MapBeaconActivity", "errorCode = " + errorCode);
//          closeProgress();
//          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
//            DialogUtils.showShortToast("无网络连接！");
//          } else {
//            DialogUtils.showShortToast("网络连接错误！");
//          }
//        }
//
//        @Override
//        public void onOk(String content) {
//          closeProgress();
//          try {
//            JSONObject object = new JSONObject(content);
//
//            // TODO 解析返回beacon数据，并存入本地数据库
//            mBeaconListDownload = Beacon.getBeaconList(object);
//
//            showProgress("提示", "beacon数据正在插入数据库，请稍后...");
//            Executors.newSingleThreadExecutor().execute(new Runnable() {
//              @Override
//              public void run() {
//                mSQLiteHelperDownload.createDatabaseTable(SQLiteHelper.TABLE_BEACON, mMallData.get(mPalMap.getCurrentItem()).getMapId() + "");
//                mSQLiteHelperDownload.clearDBTable();
//                mSQLiteHelperDownload.insertBeacons(mBeaconListDownload);
////                if (mBeaconListDownload != null && mBeaconListDownload.size() > 0) {
////                  Beacon beacon = mBeaconListDownload.get(0);
////                  ((MapFragment) mBeaconListDownload.get(0)).setUUIDAndMajor(beacon.getUuid(), beacon.getMajor());
////                }
//
////                mBeaconListDownload = mSQLiteHelperDownload.getBeaconsByID(((MapFragment) mBeaconListDownload.get(0)).mFloorId);
//                runOnUiThread(new Runnable() {
//                  @Override
//                  public void run() {
//                    closeProgress();
//                    DialogUtils.showLongToast("离线下载成功！");
//                    refeshButtonState();
//                  }
//                });
//              }
//            });
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//            DialogUtils.showShortToast("beacon点位数据错误");
//          }
////
////          mBeaconListFragment.updateBeaconNum("待巡检");
////          //初始化便于修改巡检状态
////          mBeaconListFragment.initBeaconList(mBeaconList);
//        }
//      });
//    } else {
//      DialogUtils.showShortToast("数据传输有误，请返回，重新进入！");
//    }
//  }
//
//  /*
//  * TODO 下载地图 - 根据floorId
//  * */
//  private void downloadMap(final int i, final long floorId) {
//
//    LogUtils.w( "开始下载楼层地图，floorId = " + floorId);
//
//    mDataSourceDownload.requestPlanarGraph(floorId, new DataSource.OnRequestDataEventListener<PlanarGraph>() {
//      @Override
//      public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
//
//        if (resourceState == DataSource.ResourceState.CACHE ||
//            resourceState == DataSource.ResourceState.ok) {
//          LogUtils.w( "resourceState = " + resourceState);
//
//          //添加到类当中
//          mMapDataSerializable.addFloorMapData(floorId, planarGraph);
//
//          downloadPlanarGraph(i + 1);
//
//        } else {
//          closeProgress();
//          DialogUtils.showShortToast("地图数据下载失败！请重试。");
//        }
//
//      }
//    });
//  }
//
//  /*
//  * TODO 利用SDK接口下载楼层数据
//  * */
//  private void downloadFloorData_SDK() {
//
//    // mPalMap.getMapId()拿到的时mallID，请求楼层数据需要poiId
//    if (mPalMap != null && mMallData.get(mPalMap.getCurrentItem()).getMapId() != -1) {
//
//      final long mapID = mMallData.get(mPalMap.getCurrentItem()).getMapId();
//      showProgress("提示", "获取地图数据中...");
//      mDataSourceDownload.requestMap(mapID, new DataSource.OnRequestDataEventListener<MapModel>() {
//        @Override
//        public void onRequestDataEvent(DataSource.ResourceState state, MapModel mapModel) {
//
//          LogUtils.w( "mallId->state = " + state);
//          if (state == DataSource.ResourceState.CACHE ||
//              state == DataSource.ResourceState.ok) {
//            final long floorID = MapModel.POI.get(mapModel);
//
//            showProgress("提示", "获取楼层数据中...");
//            mDataSourceDownload.requestPOIChildren(floorID, new DataSource.OnRequestDataEventListener<LocationList>() {
//              @Override
//              public void onRequestDataEvent(DataSource.ResourceState resourceState, final LocationList data) {
//                LogUtils.w( "floorId->resourceState = " + resourceState);
//                if (resourceState == DataSource.ResourceState.CACHE ||
//                    resourceState == DataSource.ResourceState.ok) {
//
//                  if (data != null && data.getSize() > 0) {
//                    mFloorListDataDownload = new ArrayList<Floor>(data.getSize());
//                    Param<Long> id = new Param<Long>("id", Long.class);
//                    Param<String> name = new Param<String>("name", String.class);
//                    Param<String> address = new Param<String>("address", String.class);
//                    for (int i = 0; i < data.getSize(); i++) {
//                      LocationModel poi = data.getPOI(i);
//                      Floor floor = new Floor();
//                      floor.setId(id.get(poi));
//                      floor.setName(name.get(poi));
//                      floor.setAlias(address.get(poi));
//                      mFloorListDataDownload.add(floor);
//                    }
//
//                    //创建序列化类，存储到数据库
//                    mMapDataSerializable.addFloorList(mFloorListDataDownload);
//                    downloadPlanarGraph(0);
//
//
////                        initFloorListView(); // 初始化楼层列表数据
////                        mFloorIndex = getIndexByFloorId(1672); // 初始化第一次加载哪个楼层  ps。。。。。1672是什么gui
////                        String fname = String.valueOf(mFloorListData.get(mFloorIndex).getAlias());
//
////                        mFloorName.setText(fname);
////                        loadMap(mFloorListData.get(mFloorIndex).getId());
//                  }
//
//
//                } else {
//                  closeProgress();
//                  DialogUtils.showLongToast("floorID = " + floorID + "不正确，请检查。");
//                }
//              }
//            });
//          } else {
//            LogUtils.e( "地图数据下载失败：" + state);
//            closeProgress();
//            DialogUtils.showLongToast("下载失败:" + state + ",mapID=" + mapID + ",请检查。");
//          }
//        }
//      });
//
//    } else {
//
//      DialogUtils.showShortToast("mapID不存在，无法下载。请检查。");
//    }
//  }
//
//  /*
//    * 根据floorlist的floorid下载planarGraph
//    * @param i mFloorListDataDownload的下标
//    */
//  public void downloadPlanarGraph(int i) {
//    if (mFloorListDataDownload == null)
//      return;
//
//    if (i < mFloorListDataDownload.size()) {
//
//      showProgress("提示", "下载楼层数据中（" + (i + 1) + "/" + mFloorListDataDownload.size() + "）...");
//
//      downloadMap(i, mFloorListDataDownload.get(i).getId());
//
//
//    }
//
//  }
//
//  /*
//  更新button状态
//   */
//  private void refeshButtonState() {
//
//    if (mPalMap.getCurrentItem() == 0 || mProvince.getCurrentItem() == 0
//        || mCity.getCurrentItem() == 0 || mRegion.getCurrentItem() == 0) {
//      mEntry.setClickable(false);
//      mEntry.setBackgroundResource(R.drawable.btn_bg_shape);
//
//      mDownload.setText("未选择地图");
//      mDownload.setClickable(false);
//      mDownload.setBackgroundResource(R.drawable.btn_bg_shape);
//
//      mHistory.setClickable(false);
//      mHistory.setBackgroundResource(R.drawable.btn_bg_shape);
//    } else {
//      saveObjectOffline();//保存离线
//
//      mEntry.setClickable(true);
//      mEntry.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//
//      mHistory.setClickable(true);
//      mHistory.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//
//      mDownload.setClickable(true);
//      mDownload.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//
//      //判断是否已离线
//      if (mSQLiteHelperDownload.hasDownload(mMallData.get(mPalMap.getCurrentItem()).getMapId())) {
//
//        mDownload.setText("已下载,点击可更新");
//      } else {
//        mDownload.setText("离线下载");
//      }
//
//    }
//
//
//  }
//
//  /*
//  保存mProvince mCity mRegionData mMallData
//   */
//  private void saveObjectOffline() {
//    //离线
//    SQLiteHelper sqLiteHelper = new SQLiteHelper(MallSelectActivity.this);
//    sqLiteHelper.createDatabaseTable(SQLiteHelper.TABLE_USER, null);
//    sqLiteHelper.saveObject(SQLiteHelper.PROVINCEDATA, mProvinceData, BluetoothUtilsApplication.userName);
//    sqLiteHelper.saveObject(SQLiteHelper.CITYDATA, mCityData, BluetoothUtilsApplication.userName);
//    sqLiteHelper.saveObject(SQLiteHelper.REGIONDATA, mRegionData, BluetoothUtilsApplication.userName);
//    sqLiteHelper.saveObject(SQLiteHelper.MAPDATA, mMallData, BluetoothUtilsApplication.userName);
//  }
//}
