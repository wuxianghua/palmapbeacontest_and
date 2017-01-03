package com.palmap.BluetoothUtils.map.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseFragment;
import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
import com.palmap.BluetoothUtils.http.DataProviderCenter;
import com.palmap.BluetoothUtils.http.HttpDataCallBack;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.Floor;
import com.palmap.BluetoothUtils.manager.BeaconManager;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
import com.palmap.BluetoothUtils.map.adapter.FloorListAdapter;
import com.palmap.BluetoothUtils.map.service.BLEController;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.utils.PhoneUtils;
import com.palmap.BluetoothUtils.utils.VibratorUtils;
import com.palmap.BluetoothUtils.utils.ViewUtils;
import com.palmap.BluetoothUtils.widget.Mark;
import com.palmap.BluetoothUtils.widget.ModifyBeaconDialog;
import com.palmaplus.nagrand.core.Types;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.io.CacheAsyncHttpClient;
import com.palmaplus.nagrand.io.FileCacheMethod;
import com.palmaplus.nagrand.view.MapOptions;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.gestures.OnSingleTapListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/10/15.
 */
public class MapFragment extends BaseFragment implements View.OnClickListener {
  private TextView mFloorName;
  private TextView mLocation;
  private TextView mZoomIn;
  private TextView mZoomOut;
  private TextView mShootScreen;
  private TextView mBeaconShow;
  private ListView mFloorListView;
  private ToggleButton mToggleButton;
  private ToggleButton mPauseButton;

  //  private PalMap mPalMap;
  private List<Floor> mFloorListData;
  private int mFloorIndex; // 当前楼层索引
  protected FloorListAdapter mFloorListAdapter = null;
  private ScaleAnimation mEnterAnimation;

  public BeaconManager getmBeaconManager() {
    return mBeaconManager;
  }

  private ScaleAnimation mExitAnimation;
  public long mFloorId;

  private MapView mMapView;
  private FrameLayout mWhiteBg;
  private RelativeLayout mMapContainer; // 地图上覆盖物容器
  private DataSource mDataSource;
  private MapOptions mMapOptions;

  private Handler mHandler;
  private List<Mark> mBeaconMarkList;

  private MapBeaconActivity mActivity;
  private BeaconManager mBeaconManager;
  private BeaconListFragment mBeaconListFragment;
  boolean isScan = false; // 按下mToggleButton(扫描赞停时也为true)  为true时，开始巡检，不可以打点
  private ModifyBeaconDialog mModifyBeaconDialog;
  private double[] mCurPoint = new double[2]; // 地图当前点击点的世界坐标

  private BLEController mBleController;


  private boolean isLoadingMap; // 正在加载地图
  private boolean isFirstInspection = true; // 第一次开启巡检


  private final String TAG = "MapFragment";
  public long lastTimeScaned;
  public long thisTimeScaned;
  public int scanCount;
  public TextView mScanInfo;

  private int headCharNum = 0;

  public boolean isScan() {
    return isScan;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    LogUtils.w("onCreateView");

    View fragmentView = inflater.inflate(R.layout.fragment_map, container, false);

    // 初始化view
    mFloorName = (TextView) fragmentView.findViewById(R.id.map_floor_name);
    mFloorName.setOnClickListener(this);
    mLocation = (TextView) fragmentView.findViewById(R.id.map_location);
    mLocation.setOnClickListener(this);
    mZoomIn = (TextView) fragmentView.findViewById(R.id.map_zoom_in);
    mZoomIn.setOnClickListener(this);
    mZoomOut = (TextView) fragmentView.findViewById(R.id.map_zoom_out);
    mZoomOut.setOnClickListener(this);
    mScanInfo = (TextView) fragmentView.findViewById(R.id.scan_info);
    mScanInfo.setVisibility(View.GONE);
    mShootScreen = (TextView) fragmentView.findViewById(R.id.map_shootscreen);
    mShootScreen.setOnClickListener(this);
    mBeaconShow = (TextView) fragmentView.findViewById(R.id.tv_beacon);
    mBeaconShow.setVisibility(View.GONE);
    mFloorListView = (ListView) fragmentView.findViewById(R.id.map_list_view);

    mMapView = (MapView) fragmentView.findViewById(R.id.map_view);
    mWhiteBg = (FrameLayout) fragmentView.findViewById(R.id.white_bg); //防止黑屏
    mMapOptions = new MapOptions(); // 该对象可设置一些地图手势操作
    mMapOptions.setSkewEnabled(false);//关闭俯仰
    mMapView.setMapOptions(mMapOptions);
    mMapView.initRatio(1.0F);
    mMapContainer = (RelativeLayout) fragmentView.findViewById(R.id.map_container);
//    mMapContainer.setOnTouchListener(new View.OnTouchListener() {
//      @Override
//      public boolean onTouch(View v, MotionEvent event) {
//        return false;//优化地图手势操作
//      }
//    });
    mMapView.setOverlayContainer(mMapContainer);
    mMapView.setBackgroundResource(android.R.color.transparent);
    mMapView.setMinAngle(45);

    mHandler = new Handler(Looper.getMainLooper());

    mToggleButton = (ToggleButton) fragmentView.findViewById(R.id.map_toggle_btn);
    mToggleButton.setChecked(false);
    mPauseButton = (ToggleButton) fragmentView.findViewById(R.id.pause);
    mPauseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isScan) {
          return;
        }
        if (isChecked) {
          mBleController.stop();
        } else {
          mBleController.start(getActivity(), mBeaconManager.getScanParameters());
        }
      }
    });
    mPauseButton.setVisibility(View.GONE);
    mBleController = new BLEController(this);


    return fragmentView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    LogUtils.w("onActivityCreated");
    mActivity = (MapBeaconActivity) getActivity();
    mBeaconListFragment = (BeaconListFragment) mActivity.getmFragmentList().get(1);

    // 获取manager
    mBeaconManager = mActivity.getmBeaconManager();
    setupListener(); // 设置地图上一些监听器

    if (PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)) {// 检测网络是否连接
      //有网先删除缓存数据，再加载网络数据
//      mBeaconManager.deleteMapBeaconsInDb();

      mDataSource = new DataSource(Constant.SERVER_URL); // 地图等服务数据源,离线模式不需要
      loadFloorDataAPIV2();
//      loadFloorData_SDK(); // 加载楼层数据  不能用，不适合多栋建筑
    } else if (mActivity.hasDownload) {
      // 无网加载离线楼层数据
      loadFloorDataOffline();
    } else {
      //无网无缓存
      DialogUtils.showLongToast("没有缓存数据，请连接网络");
    }


  }

  public void refeshScanedBeaconNum() {
    int num = 0;
    if (mBeaconMarkList != null && mBeaconMarkList.size() != 0) {
      for (Mark mark : mBeaconMarkList) {
        if (mark.isScaned()) {
          num++;
        }
      }
    }
    mBeaconManager.setActiveBeaconNum(num);

    mBeaconListFragment.updateBeaconNum();
  }


  //更新log
  public void updateBeaconText(String msg) {
    int sizeOfLine = 45;
    CharSequence charSequence = mBeaconShow.getText();
//    SpannedString spannedString = SpannedString.valueOf(mBeaconShow.getText());
//    if (charSequence=="")
//      charSequence = getString(R.string.log);

    if (charSequence.length() > 12 * sizeOfLine && msg.length() + headCharNum < charSequence.length())
      charSequence = charSequence.subSequence(0, headCharNum).toString() + charSequence.subSequence(msg.length() + headCharNum, charSequence.length());

    mBeaconShow.setText(charSequence + Html.fromHtml(msg).toString());
  }

  /*
  加载离线楼层数据
   */
  private void loadFloorDataOffline() {
    showProgress("提示", "加载Beacon数据中...");
    mFloorListData = mBeaconManager.getFloorList();  //更新FloorList
    if (mFloorListData == null) {
      DialogUtils.showShortToast("本地楼层数据加载失败，请重新下载。");
    }
    closeProgress();

    initFloorListView(); // 初始化楼层列表数据
    mFloorIndex = getDefaultFloorIndex(1672); // 初始化加载楼层
    String fname = String.valueOf(mFloorListData.get(mFloorIndex).getAlias());

    mFloorName.setText(fname);
//    loadMapOffline(mFloorListData.get(mFloorIndex).getId());

    //添加带缓存的DataSource
    CacheAsyncHttpClient cacheAsyncHttpClient = new CacheAsyncHttpClient(Constant.SERVER_URL);
    FileCacheMethod fileCacheMethod = new FileCacheMethod(Environment.getExternalStorageDirectory() + File.separator + "Nagrand/download/");
    cacheAsyncHttpClient.reset(fileCacheMethod);
    mDataSource = new DataSource(cacheAsyncHttpClient);

    mFloorId = mFloorListData.get(mFloorIndex).getId();
    loadMap(mFloorId);
  }


  /*
   * TODO 利用接口获取楼层数据
   * */
  private void loadFloorDataAPIV2() {
    // mPalMap.getMapId()拿到的时mallID，请求楼层数据需要poiId
    if (mBeaconManager.getMapId() != -1) {

      showProgress(mHandler, "提示", "加载商场楼层数据中...");
      DataProviderCenter.getInstance().getFloorsAPIV2(mBeaconManager.getMapId(), new HttpDataCallBack() {
        @Override
        public void onError(int errorCode) {
          DialogUtils.showLongToast("error:" + errorCode);
        }

        @Override
        public void onComplete(Object content) {
          try {
//            JSONObject object = new JSONObject(content.toString());
            JSONArray array = new JSONArray(content.toString());
            if (!(array != null && array.length() > 0)) {
              DialogUtils.showLongToast("no floor data!");
            }
            mFloorListData = new ArrayList<Floor>(array.length());
            JSONObject obj1 = null;
            Floor floor = null;
            for (int i = 0; i < array.length(); i++) {
              obj1 = array.getJSONObject(i);
              floor = new Floor();
              floor.setId(obj1.getLong("floorId"));
              floor.setName(obj1.getString("floorName"));
              floor.setAlias(obj1.getString("alias"));
              mFloorListData.add(floor);
            }
          } catch (Exception e) {
            DialogUtils.showLongToast(e.getMessage());
            return;
          }

          initFloorListView(); // 初始化楼层列表数据
          mFloorIndex = getDefaultFloorIndex(1672); // 初始化加载楼层
          String fname = String.valueOf(mFloorListData.get(mFloorIndex).getAlias());

          mFloorName.setText(fname);
          loadMap(mFloorListData.get(mFloorIndex).getId());

        }
      });
    } else {
      DialogUtils.showShortToast("商场ID传递错误！");
    }

  }


  /*
  * 设置地图上mapniew的监听器
  * */
  private void setupListener() {

    //点击事件，添加一个beacon
    mMapView.setOnSingleTapListener(new OnSingleTapListener() { // 监听单击事件
      @Override
      public void onSingleTap(final MapView mapView, final float x, final float y) {
        if (isScan) {
          return;
        }
        Types.Point point = mMapView.converToWorldCoordinate(x, y);
        mCurPoint[0] = point.x;
        mCurPoint[1] = point.y;
        LogUtils.i("x = " + point.x + ", y = " + point.y);

//          if (mModifyBeaconDialog == null) {
        mModifyBeaconDialog = new ModifyBeaconDialog(getActivity(), new ModifyBeaconDialog.OnModifyBeaconDialogListener() {
          @Override
          public void onOk(final Beacon beacon) {//新建beacon，插入数据库,等待点击上传按钮上传beacon数据
            LogUtils.w("UUID: " + beacon.getUuid() + ", major: " + beacon.getMajor() + ", minor: " + beacon.getMinor());

            //dialog中已设置uuid,major,minor
            beacon.setSceneId(mBeaconManager.getSceneId());
            beacon.setMapId(mBeaconManager.getMapId());
            beacon.setX(mCurPoint[0]);
            beacon.setY(mCurPoint[1]);
            beacon.setFloorId(mFloorId);

            long ret = mBeaconManager.addBeacon(beacon);

//              if (ret == -1) {
//                LogUtils.i("addBeaconOverlay 失败" );
//                DialogUtils.showLongToast("添加beacon数据失败，请重试！");
//
//                // 添加覆盖物
//                removeBeaconOverlay(beacon);
//                addBeaconOverlay(beacon);
//
//                mBeaconManager.refeshBeaconsByFloorId(mFloorId);
//                mMapView.getOverlayController().refresh();//更新mapview
//              }


            //直接上传

            DataProviderCenter.getInstance().sendBeaconAPIV2(mActivity.mPalMap.getMapId(), mActivity.mPalMap.getSceneId(), beacon, new HttpDataCallBack() {
              @Override
              public void onError(int errorCode) {
                LogUtils.e("sendBeaconAPIV2 errorCode = " + errorCode);
                if (errorCode == ErrorCode.CODE_NO_INTERNET) {
                  DialogUtils.showShortToast("无网络连接，无法上传！");
                } else {
                  DialogUtils.showShortToast("网络信号弱，无法上传，请更换wifi或网络运营商。");
                }
                mBeaconManager.deleteBeacon(beacon);
                refeshBeaconMarkAndList();
              }

              @Override
              public void onComplete(Object content) {
                int oldBeaconID = 0;
                try {
                  JSONObject object = new JSONObject((String) content);
                  int beaconID = object.getInt("beaconId");
                  oldBeaconID = beacon.getId();
                  beacon.setId(beaconID);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                mBeaconManager.setActionNo(beacon, oldBeaconID);
//                    mBeaconListFragment.mBeaconListAdapter.updateScanedBeacon(mActivity.mBeaconList);
                refeshBeaconMarkAndList();
                DialogUtils.showShortToast("上传&新增beacon成功。");
              }
            });

          }//ok
        });
//          }
        mModifyBeaconDialog.show();


      }
    });

    /*
    切换楼层，刷新beacon列表和mark
     */
    mMapView.setOnChangePlanarGraph(new MapView.OnChangePlanarGraph() {
      @Override
      public void onChangePlanarGraph(PlanarGraph planarGraph, PlanarGraph planarGraph1, long oldPlanarGraphId, long newPlanarGraphId) {
        LogUtils.w("oldPlanarGraphId = " + oldPlanarGraphId + ", newPlanarGraphId = " + newPlanarGraphId);
//        mFloorId = newPlanarGraphId;//智慧图的坐标切换时sdk获取到newPlanarGraphId=0不能用，使用放到loadmap（）中处理floorid
//        refeshBeaconMarkAndList();
      }
    });

    mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (mBeaconManager.getBeaconList() == null ||
            mBeaconManager.getBeaconList().size() <= 0) {
          DialogUtils.showLongToast("该楼层没有beacon数据，请导入后再巡检");
          mToggleButton.setChecked(false);
//          mToggleButton.setText("开启");
          isScan = false;
          return;
        }

        isScan = isChecked;
        if (isScan) {//打开巡检
          cheakNeedUplodeBeforeScan();
        } else {//关闭巡检
          stopScanAndSaveHistory();
        }
      }
    });

  }

  private void stopScanAndSaveHistory() {
    //关闭巡检
//    if (mBleController.isScanning()) {
    //询问保存巡检记录
    saveScanHistory();
//    }
  }

  public void stopScan() {
    //关闭巡检
    mToggleButton.setChecked(false);
  }
  /*
  上传数据并巡检
   */

  public void cheakNeedUplodeBeforeScan() {
    if (mBeaconManager.hasBeaconDataUpload()) {
      DialogUtils.showDialog(mActivity, "有未上传的beacond点位信息，请上传后再开始巡检，现在上传？", new DialogUtils.DialogCallBack() {
        @Override
        public void onOk() {
          mActivity.uploadBeaconDataAPIV2(new MapBeaconActivity.OnUploadComplete() {
            @Override
            public void onComplete() {
              startScan();
            }
          });
        }

        @Override
        public void onCancel() {
          mToggleButton.setChecked(true);
          return;
        }
      });
    } else {
      startScan();
    }
  }

  /*
  开始巡检
   */
  private void startScan() {
    mBeaconShow.setText("");//清空上一次的巡检log
    mPauseButton.setVisibility(View.VISIBLE);

    if (isFirstInspection) {
      mBeaconManager.refeshBeaconsByFloorId(mFloorId);
      isFirstInspection = false;
    }
//    addBeaconOverlay(mActivity.mBeaconList);

    scanCount = 0;
    lastTimeScaned = System.currentTimeMillis();
    mScanInfo.setVisibility(View.VISIBLE);

    headCharNum = mBeaconManager.getScanParameters().toString().length() + 11;
    mBleController.start(getActivity(), mBeaconManager.getScanParameters());
  }

  /*
  保存巡检记录
   */
  private void saveScanHistory() {

    DialogUtils.showDialog(mActivity, "是否保存本次巡检记录？", new DialogUtils.DialogCallBack() {
      @Override
      public void onOk() {
        mBleController.stop();
        mBeaconManager.saveScanHistory(mFloorListData.get(mFloorIndex).getAlias());
        mBeaconManager.setAllScaned(false);
        mBeaconListFragment.updateBeaconNum();
        mScanInfo.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.GONE);
        mPauseButton.setChecked(false);
        refeshBeaconMarkAndList();
      }

      @Override
      public void onCancel() {
        mBeaconManager.setAllScaned(false);
        mBeaconListFragment.updateBeaconNum();
        mBleController.stop();
        mScanInfo.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.GONE);
        mPauseButton.setChecked(false);
        refeshBeaconMarkAndList();
      }
    });
  }

  /*
  *  加载地图 - 根据floorId
  * */
  private void loadMap(final long floorId) {
    showProgress(mHandler, "提示", "地图加载中，请稍后...");
    LogUtils.w("开始加载地图，floorId = " + floorId);
    isLoadingMap = true;
    mDataSource.requestPlanarGraph(floorId, new DataSource.OnRequestDataEventListener<PlanarGraph>() {
      @Override
      public void onRequestDataEvent(final DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
        closeProgress(mHandler);
        if (resourceState == DataSource.ResourceState.ok ||
            resourceState == DataSource.ResourceState.CACHE) {
          LogUtils.i("resourceState = " + resourceState);
          mMapView.drawPlanarGraph(planarGraph);
          mMapView.start();


          mFloorId = floorId;
          refeshBeaconMarkAndList();
          mWhiteBg.setVisibility(View.GONE); //防止黑屏

        } else {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              DialogUtils.showShortToast("floorID=" + floorId + "加载失败:" + resourceState);
            }
          });
        }

        isLoadingMap = false;
      }
    });
  }

  //  ************************    view相关   ***********************************

  /*
  * 在地图上重新添加所有的beacon覆盖物
  * */
  public void addBeaconOverlay(List<Beacon> beacons) {
    if (beacons == null) {
      return;
    }
//    beaconNum = beacons.size();
    removeAllBeaconOverlay(); // 如果旧的，先移除

    if (mBeaconMarkList == null) {
      mBeaconMarkList = new ArrayList<>();
    }
    Context context = getActivity();
    if (context == null)
      return;

    for (Beacon beacon : beacons) {
      Mark mark = new Mark(context, new Mark.OnClickListenerForMark() {

        @Override
        public void onMarkSelect(final Mark mark) {//服务器上beacon数据点击响应
          if (isScan)
            return;//巡检不能修改

          LogUtils.i("mark.minor = " + mark.getText());
//          if (mModifyBeaconDialog == null) {
          Beacon b = mBeaconManager.getBeaconById(mark.getBeaconId());
          if (b == null) {
            DialogUtils.showShortToast("未找到id为" + mark.getBeaconId() + "的beacon数据！");
            return;
          }


          mModifyBeaconDialog = new ModifyBeaconDialog(getActivity(), b, new ModifyBeaconDialog.OnModifyBeaconDialogListener() {
            @Override
            public void onOk(final Beacon beacon) {//点击修改beacon信息对话框的确认按钮
              LogUtils.i("new mark.uuid: " + beacon.getUuid() + " major:" + beacon.getMajor() + " minor:" + beacon.getMinor());

              long ret = mBeaconManager.addBeacon(beacon);

              if (ret != -1) {
                LogUtils.i("修改beacon ret = " + ret);
                //直接上传
                DataProviderCenter.getInstance().sendBeaconAPIV2(mBeaconManager.getMapId(), mBeaconManager.getSceneId(), beacon, new HttpDataCallBack() {
                  @Override
                  public void onError(int errorCode) {

                    LogUtils.e("sendBeaconAPIV2 errorCode = " + errorCode);
                    if (errorCode == ErrorCode.CODE_NO_INTERNET) {
                      DialogUtils.showShortToast("无网络连接，无法上传！");
                    } else if (errorCode == 404) {
                      if (beacon.getAction() == Beacon.ACTION_DELETE) {
                        mBeaconManager.deleteBeacon(beacon);
                        DialogUtils.showShortToast("删除beacon成功。");
                      } else {
                        DialogUtils.showShortToast("服务器无minor=" + beacon.getMinor() + "的beacon数据，无法更新！");
                      }
                    } else {
                      DialogUtils.showShortToast("网络信号弱，无法上传，请更换wifi或网络运营商。");
                    }
//                    if (!mActivity.hasDownload) {//如果没有离线，要把当前“beacon”表中数据离线
//                      mActivity.autoDownloadOfflineData();
//                    }

                    refeshBeaconMarkAndList();
                  }

                  @Override
                  public void onComplete(Object content) {
                    if (beacon.getAction() == Beacon.ACTION_UPDATE) {
                      mBeaconManager.setActionNo(beacon, beacon.getId());
                      DialogUtils.showShortToast("上传&更新beacon成功。");

                    } else if (beacon.getAction() == Beacon.ACTION_DELETE) {
                      mBeaconManager.deleteBeacon(beacon);
                      DialogUtils.showShortToast("上传&删除beacon成功。");
                    } else {
                      DialogUtils.showShortToast("上传beacon错误，action=" + beacon.getAction() + "请检查");
                    }
                    refeshBeaconMarkAndList();
                  }
                });


              } else
                DialogUtils.showLongToast("修改beacon数据失败，请重试！");
            }
          });
          mModifyBeaconDialog.show();

        }
      });

      mark.setBeaconInfo(beacon);
      mark.init(new double[]{beacon.getX(), beacon.getY()});
      mark.setText(beacon);
      mMapView.addOverlay(mark);
      mBeaconMarkList.add(mark);
    }

  }


  /**
   * 扫描到beacon更新某个overlay
   *
   * @param minor
   */
//  public void updateBeaconOverlay(int minor) {
//    if (isLoadingMap) return;
//    if (mBeaconMarkList == null || mBeaconMarkList.size() == 0) {
//      updateBeaconText("ERROR: mBeaconMarkList = null");
//      return;
//    }
//    for (Mark mark : mBeaconMarkList) {
//
//      if (minor == mark.getMinor() && !mark.isScaned()) {
//        updateBeaconText("SUCCESS: minor=" + minor);
//        VibratorUtils.Vibrate(mActivity, 300);
//        DialogUtils.showShortToast("找到Beacon：minor=" + minor);
//
//        mark.setScanedColor(true);
//
//        LogUtils.w("更新beacon");
//        mMapView.removeOverlay(mark);  //移去再添加，防止重复打点变绿被上面的红点覆盖
//        mMapView.addOverlay(mark);
//
//        mMapView.getOverlayController().refresh();
//        refeshScanedBeaconNum();
//
//        //更新beaconlist
//        if (mBeaconListFragment.mBeaconListAdapter != null)
//          mBeaconListFragment.mBeaconListAdapter.updateScanedBeacon(minor);
//
//        LogUtils.i("find beacon : minor = " + minor);
//        break;
//      }
//    }
//  }

  /**
   * 扫描到beacon更新某些个overlay
   *
   * @param
   */
  public void updateBeaconOverlay(final List<Beacon> beaconList) {
    if (isLoadingMap) return;
    if (mBeaconMarkList == null || mBeaconMarkList.size() == 0) {
      updateBeaconText("ERROR: mBeaconMarkList = null");
      return;
    }
    if (beaconList == null || beaconList.size() == 0)
      return;
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        boolean isFind = false;
        for (Mark mark : mBeaconMarkList) {
          for (Beacon b: beaconList) {
            if (b.getMinor() == mark.getMinor() && b.getMajor() == mark.getMajor() && b.getUuid().equals(mark.getUuid()) && !mark.isScaned()) {
              updateBeaconText("<font color='#FF0000'>SUCCESS: minor=" + b.getMinor() + "</front><br/>");
              DialogUtils.showShortToast("找到Beacon：minor=" + b.getMinor());

              mark.setScanedColor(true);

              //现在没有重复的点了
//              mMapView.removeOverlay(mark);  //移去再添加，防止重复打点变绿被上面的红点覆盖
//              mMapView.addOverlay(mark);


              //更新beaconlist,会更新数据库
              if (mBeaconListFragment.mBeaconListAdapter != null)
                mBeaconListFragment.mBeaconListAdapter.updateScanedBeacon(b);

              isFind = true;
              LogUtils.i("find beacon : minor = " + b.getMinor());
              break;
            }
          }
        }

        if (isFind)
          VibratorUtils.Vibrate(mActivity, 300);

        mMapView.getOverlayController().refresh();
        refeshScanedBeaconNum();
        mBeaconManager.refeshBeaconsByFloorId(mFloorId);
//        refeshBeaconMarkAndList();
      }
    });

  }
//
//  /**
//   * overlay全部恢复为未检测到状态
//   */
//  public void resetOverlay(){
//    if (mBeaconMarkList == null || mBeaconMarkList.size() == 0) return;
//    for (Mark mark : mBeaconMarkList)
//      mark.setScanedColor(false);
//  }

  /*
  * 清除地图上beacon覆盖物
  * */
  public void removeAllBeaconOverlay() {
    if (mBeaconMarkList == null || mBeaconMarkList.size() <= 0) {
      return;
    }

    for (Mark mark : mBeaconMarkList) {
      mMapView.removeOverlay(mark);
    }

    mBeaconMarkList.clear();
  }

  /*
  * 删除指定beacon的覆盖物
  * */
  public void removeBeaconOverlay(Beacon beacon) {
    if (mBeaconMarkList == null || mBeaconMarkList.size() <= 0 || beacon == null) {
      LogUtils.e("删除beacon overlay失败");
      return;
    }

    Mark mark = null;
    for (Mark m : mBeaconMarkList) {
      if (m.getBeaconId() == beacon.getId()) { // id
        mark = m;
        break;
      }
    }

    if (mark != null) {
      LogUtils.w("删除beacon");
      mBeaconMarkList.remove(mark);
      mMapView.removeOverlay(mark);
//      mActivity.mBeaconList.remove(beacon);
    }

  }

  /*
  * 添加指定beacon的覆盖物
  * */
  public void addBeaconOverlay(Beacon beacon) {
    if (mBeaconMarkList == null || beacon == null) {
      LogUtils.i("添加beacon overlay失败");
      return;
    }

    Mark mark = new Mark(getActivity(), new Mark.OnClickListenerForMark() {
      @Override
      public void onMarkSelect(final Mark mark) {//点击beacon的响应，同初始化beaconlist时的点击响应
        if (isScan)
          return;//巡检不能修改

        LogUtils.i("beacon id=" + mark.getBeaconId());
        LogUtils.i("mark.minor = " + mark.getText());
//          if (mModifyBeaconDialog == null) {
        Beacon b = mBeaconManager.getBeaconByMinor(mark.getMinor());
        if (b == null)
          return;

        mModifyBeaconDialog = new ModifyBeaconDialog(getActivity(), b, new ModifyBeaconDialog.OnModifyBeaconDialogListener() {
          @Override
          public void onOk(final Beacon beacon) {//点击修改beacon信息对话框的确认按钮
            LogUtils.i("new mark.uuid: " + beacon.getUuid() + " major:" + beacon.getMajor() + " minor:" + beacon.getMinor());

            long ret = mBeaconManager.addBeacon(beacon);

            if (ret != -1) {
              LogUtils.i("修改beacon ret = " + ret);
              //直接上传
              DataProviderCenter.getInstance().sendBeaconAPIV2(mActivity.mPalMap.getMapId(), mActivity.mPalMap.getSceneId(), beacon, new HttpDataCallBack() {
                @Override
                public void onError(int errorCode) {

                  LogUtils.e("sendBeaconAPIV2 errorCode = " + errorCode);
                  if (errorCode == ErrorCode.CODE_NO_INTERNET) {
                    DialogUtils.showShortToast("无网络连接，无法上传！");
                  } else if (errorCode == 404) {
                    if (beacon.getAction() == Beacon.ACTION_DELETE) {
                      mBeaconManager.deleteBeacon(beacon);
                      DialogUtils.showShortToast("删除beacon成功。");
                    } else {
                      DialogUtils.showShortToast("服务器无minor=" + beacon.getMinor() + "的beacon数据，无法更新！");
                    }
                  } else {
                    DialogUtils.showShortToast("网络信号弱，无法上传，请更换wifi或网络运营商。");
                  }
//                  if (!mActivity.hasDownload) {//如果没有离线，要把当前“beacon”表中数据离线
//                    mActivity.autoDownloadOfflineData();
//                  }
                  refeshBeaconMarkAndList();
                }

                @Override
                public void onComplete(Object content) {
                  if (beacon.getAction() == Beacon.ACTION_UPDATE) {
                    mBeaconManager.setActionNo(beacon, beacon.getId());
                    DialogUtils.showShortToast("上传&更新beacon成功。");
                  } else if (beacon.getAction() == Beacon.ACTION_DELETE) {
                    mBeaconManager.deleteBeacon(beacon);
                    DialogUtils.showShortToast("上传&删除beacon成功。");
                  } else {
                    DialogUtils.showShortToast("上传beacon错误，action=" + beacon.getAction() + "请检查");
                  }
                  refeshBeaconMarkAndList();
                }
              });


            } else
              DialogUtils.showLongToast("修改beacon数据失败，请重试！");


          }
        });
        mModifyBeaconDialog.show();

      }
    });

    LogUtils.w("添加beacon");
    mark.setBeaconInfo(beacon);
    mark.init(new double[]{beacon.getX(), beacon.getY()});
    mark.setText(beacon);
    mMapView.addOverlay(mark);
    mBeaconMarkList.add(mark);
//    mActivity.mBeaconList.add(beacon);
  }

  /*
  更新地图上的beacon mark和beacon列表
   */
  public void refeshBeaconMarkAndList() {
    mBeaconManager.refeshBeaconsByFloorId(mFloorId);

    addBeaconOverlay(mBeaconManager.getBeaconList());

    refeshBeaconList();
    refeshScanedBeaconNum();
  }

  /**
   * @Author: eric3
   * @Description: 更新beaconlistfragment的视图
   * @Time 2016/12/7 10:01
   */
  public void refeshBeaconList() {
//    mBeaconListFragment.updateBeaconNum("待巡检", "待巡检");

    if (mBeaconListFragment.mBeaconListAdapter != null)
      mBeaconListFragment.mBeaconListAdapter.updateBeaconList(mBeaconManager.getBeaconList());
    else
      mBeaconListFragment.initBeaconList(mBeaconManager.getBeaconList());
  }


  //  ************************    楼层列表信息   ***********************************

  /*
  *  初始化楼层列表view
  * */
  private void initFloorListView() {
    mFloorListAdapter = new FloorListAdapter(getActivity(), mFloorListData);
    mFloorListView.setAdapter(mFloorListAdapter);
    ViewUtils.setListViewBasedOnItem(mFloorListView, 5, true);
    mFloorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // 切换楼层
        if (position != mFloorIndex) { // 不同层, 需要跳转
          hideFloorListView();
          mFloorIndex = position;
          mFloorName.setText(String.valueOf(mFloorListData.get(mFloorIndex).getAlias()));
          // 切换地图
          loadMap(mFloorListData.get(mFloorIndex).getId());


        }
      }
    });

    // 创建动画对象
    mEnterAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
    mEnterAnimation.setDuration(500);
    mExitAnimation = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
    mExitAnimation.setDuration(500);
  }

  /*
    *  显示楼层列表
    * */
  private void showFloorListView() {
    mFloorListView.setVisibility(View.VISIBLE);
    mFloorListView.startAnimation(mEnterAnimation);
  }

  /*
  *  隐藏楼层列表
  * */
  private void hideFloorListView() {
    mFloorListView.startAnimation(mExitAnimation);
    mFloorListView.setVisibility(View.GONE);
  }

  /*
    *  根据floorId找到对应索引值
    * */
  private int getDefaultFloorIndex(long defaultFloorId) {
    int index = 0;
    if (mFloorListData == null || mFloorListData.size() <= 0) {
      return index;
    }

    //巡检记录跳转楼层
    ScanHistorySerializable scanHistorySerializable = (ScanHistorySerializable) getActivity().getIntent().getSerializableExtra(Constant.TAG_BEACON_HISTORY);
    if (scanHistorySerializable != null) {
      for (Floor floor : mFloorListData) {
        if (floor.getId() == scanHistorySerializable.getFloorId()) {
          return mFloorListData.indexOf(floor);
        }
      }
    }

    // 检测floorId
    for (int i = 0; i < mFloorListData.size(); i++) {
      if (mFloorListData.get(i).getId() == defaultFloorId) {
        return i;
      }
    }

    // 检测“一层”索引
    String s = null;
    for (int i = 0; i < mFloorListData.size(); i++) {
      s = mFloorListData.get(i).getName();
      if (s != null && "一层".contains(s)) {
        return i;
      }
    }

    return index;
  }


  //  ************************    回调 - 生命周期 + event   ***********************************

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.map_floor_name: // 切换楼层
        if (mFloorListData == null || mFloorListData.size() <= 0) { // 无数据直接返回
          return;
        }
        if (mFloorListView.getVisibility() == View.GONE) { // 切换状态
          showFloorListView();
        } else {
          hideFloorListView();
        }
        break;
      case R.id.map_location: // 定位
        DialogUtils.showShortToast("定位");
        break;
      case R.id.map_zoom_in: // 放大
        mMapView.zoomIn();
        break;
      case R.id.map_zoom_out: // 缩小
        mMapView.zoomOut();
        break;
      case R.id.map_shootscreen: // 截屏
//        saveCustomViewBitmap();
        if (mBeaconShow.getVisibility() == View.VISIBLE)
          mBeaconShow.setVisibility(View.GONE);
        else
          mBeaconShow.setVisibility(View.VISIBLE);

        break;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtils.w("onResume()");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LogUtils.w("onDestroy()");
    mMapView.drop();

    mBleController.stop();
    mScanInfo.setVisibility(View.GONE);

    if (mBeaconMarkList != null && mBeaconMarkList.size() > 0) {
      removeAllBeaconOverlay();
    }
  }
  /**
   * 刷新overlay图标：被扫到是绿色图标，未被扫到是红色图标
   * @param minors  当前扫描的beacon列表
   */
//  public void updateBeaconOverlayIcon(Set<Integer> minors){
//    if (mBeaconMarkList == null || mBeaconMarkList.size() == 0) return;
//    if (minors == null || minors.size() == 0) return;
//
//    for (Mark mark : mBeaconMarkList){
//      if (minors.contains(mark.getMinor())){
//        mark.setScanedColor(true);
//      } else {
//        mark.setScanedColor(false);
//      }
//    }
//
//    mMapView.getOverlayController().refresh();
//  }
  //保存自定义view的截图
//  private void saveCustomViewBitmap() {
////    //获取自定义view图片的大小
////    Bitmap temBitmap = Bitmap.createBitmap(mMapView.getWidth(), mMapView.getHeight(), Bitmap.Config.ARGB_8888);
////    //使用Canvas，调用自定义view控件的onDraw方法，绘制图片
////    Canvas canvas = new Canvas(temBitmap);
////    mMapView.doDraw(canvas);
//
////    View v1 = mMapContainer; //获取单个View
////    v1.setDrawingCacheEnabled(true);
////    Bitmap temBitmap = v1.getDrawingCache();
//
////    Bitmap temBitmap = Bitmap.createBitmap(mMapView.getWidth(), mMapView.getHeight(), Bitmap.Config.ARGB_8888);
////    Canvas bitCanvas = new Canvas(temBitmap);
////    bitCanvas = mMapView.getHolder().lockCanvas();
//
////    if (bitCanvas != null) {
//////      把surfaceview的内容绘制到canvas 上
//////      surfaceview.doDraw(canvas);
//////      mMapView.getHolder().unlockCanvasAndPost(canvas);
////    }
//
//    View dView = getActivity().getWindow().getDecorView();
//    dView.setDrawingCacheEnabled(true);
//    dView.buildDrawingCache();
//    Bitmap temBitmap = dView.getDrawingCache();
//
//
//    //输出到sd卡
//    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//    String date = sDateFormat.format(new java.util.Date());
//    String Path = Environment.getExternalStorageDirectory() + "/蓝牙巡检工具";
//
//    File dir = new File(Path);
//    if (!dir.exists()) {
//      try {
//        //按照指定的路径创建文件夹
//        dir.mkdirs();
//      } catch (Exception e) {
//        //  handle exception
//      }
//    }
//
//    String filePath = Path + "/Beacon点位截屏" + date + ".png";
//    File file = new File(filePath);
//    try {
//      if (!file.exists()) {
//        file.createNewFile();
//      }
//      FileOutputStream foStream = new FileOutputStream(file);
//      temBitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
//      foStream.flush();
//      foStream.close();
//      DialogUtils.showLongToast("截屏文件已保存至" + filePath);
//    } catch (Exception e) {
//      LogUtils.i(e.toString());
//    }
//  }

  /*
  *  利用平台接口获取楼层数据
  * */
//  private void loadFloorData() {
//    if (mBeaconManager != null) {
//      showProgress(mHandler, "提示", "加载商场数据中...");
//      DataProviderCenter.getInstance().getFloors(mBeaconManager.getMapId(), new HttpDataCallBack<String>() {
//        @Override
//        public void onError(int errorCode) {
//          LogUtils.e("loadFloorData errorCode = " + errorCode);
//          closeProgress(mHandler);
//          if (errorCode == ErrorCode.CODE_NO_INTERNET) {
//            DialogUtils.showShortToast("无网络连接！");
//          } else {
//            DialogUtils.showShortToast("网络连接错误！");
//          }
//        }
//
//        @Override
//        public void onComplete(String content) {
//          closeProgress(mHandler);
//          LogUtils.w("floors: " + content);
//          try {
//            JSONObject object = new JSONObject(content);
//            long defaultFloorId = object.optLong("defaultFloor", -1);
//            JSONArray floors = object.optJSONArray("floors");
//            if (floors != null) {
//              mFloorListData = Floor.getFloorList(floors);
////              Collections.sort(mFloorListData);
//              initFloorListView(); // 初始化楼层列表数据
//              mFloorIndex = getDefaultFloorIndex(defaultFloorId); // 初始化第一次加载哪个楼层
//              mFloorName.setText(mFloorListData.get(mFloorIndex).getAlias());
//
//              // 开始加载地图
//              loadMap(mFloorListData.get(mFloorIndex).getId());
//
//            } else {
//              DialogUtils.showShortToast("楼层数据有误！");
//            }
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//            DialogUtils.showShortToast("楼层数据有误！");
//          }
//        }
//      });
//    } else {
//      DialogUtils.showShortToast("商场数据有误！");
//    }
//  }

  //  /*
//  加载本地楼层地图数据
//   */
//  private void loadMapOffline(long floorID){
//    showProgress(mHandler, "提示", "本地地图加载中...");
//    LogUtils.w( "开始加载本地地图，floorId = " + floorID);
//    isLoadingMap = true;
////    PlanarGraph planarGraph = mActivity.mSQLiteHelper.getPlanarGraph(mPalMap.getName(),floorID);
//
//    if (planarGraph!=null){
//      mMapView.drawPlanarGraph(planarGraph);
//      mMapView.start();
//    } else {
//      DialogUtils.showLongToast("本地地图数据加载失败,请重新下载");
//    }
//    isLoadingMap = false;
//  }
  /*
  * 利用SDK接口获取楼层数据
  * */
//  private void loadFloorData_SDK() {
//
//    // mPalMap.getMapId()拿到的时mallID，请求楼层数据需要poiId
//    if (mBeaconManager.getMapId() != -1) {
//
//      showProgress(mHandler, "提示", "加载商场楼层数据中...");
//      mDataSource.requestMap(mBeaconManager.getMapId(), new DataSource.OnRequestDataEventListener<MapModel>() {
//        @Override
//        public void onRequestDataEvent(DataSource.ResourceState state, MapModel mapModel) {
//          closeProgress(mHandler);
//          LogUtils.w("mallId->state = " + state);
//          if (state == DataSource.ResourceState.ok) {
//            final long floorID = MapModel.POI.get(mapModel);
//
//            showProgress(mHandler, "提示", "加载楼层地图中...");
//            mDataSource.requestPOIChildren(floorID, new DataSource.OnRequestDataEventListener<LocationList>() {
//              @Override
//              public void onRequestDataEvent(DataSource.ResourceState resourceState, final LocationList data) {
//                LogUtils.w("poiId->resourceState = " + resourceState);
//                if (resourceState == DataSource.ResourceState.ok) {
//                  mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                      if (data != null && data.getSize() > 0) {
//                        mFloorListData = new ArrayList<Floor>(data.getSize());
//                        Param<Long> id = new Param<Long>("id", Long.class);
//                        Param<String> name = new Param<String>("name", String.class);
//                        Param<String> address = new Param<String>("address", String.class);
//                        for (int i = 0; i < data.getSize(); i++) {
//                          LocationModel poi = data.getPOI(i);
//                          Floor floor = new Floor();
//                          floor.setId(id.get(poi));
//                          floor.setName(name.get(poi));
//                          floor.setAlias(address.get(poi));
//                          mFloorListData.add(floor);
//                        }
//
//                        initFloorListView(); // 初始化楼层列表数据
//                        mFloorIndex = getDefaultFloorIndex(1672); // 初始化第一次加载哪个楼层  ps。。。。。1672是什么gui
//                        String fname = String.valueOf(mFloorListData.get(mFloorIndex).getAlias());
//
//                        mFloorName.setText(fname);
//                        loadMap(mFloorListData.get(mFloorIndex).getId());
//                      }
//                    }
//                  });
//                } else {
//                  DialogUtils.showShortToast("floorID" + floorID + "不存在！");
//                }
//              }
//            });
//          } else {
//            LogUtils.e("地图数据加载失败：" + state);
//            DialogUtils.showShortToast("mapID" + mBeaconManager.getMapId() + "不存在！");
//          }
//        }
//      });
//
//    } else {
//
//      DialogUtils.showShortToast("商场ID传递错误！");
//    }
//  }
}

