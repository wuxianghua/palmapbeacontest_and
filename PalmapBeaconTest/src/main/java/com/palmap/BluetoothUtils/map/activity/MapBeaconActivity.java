package com.palmap.BluetoothUtils.map.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.http.DataProviderCenter;
import com.palmap.BluetoothUtils.http.HttpDataCallBack;
import com.palmap.BluetoothUtils.http.HttpErrorUtil;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.manager.BeaconManager;
import com.palmap.BluetoothUtils.map.adapter.MyFragmentPagerAdapter;
import com.palmap.BluetoothUtils.map.fragment.BeaconListFragment;
import com.palmap.BluetoothUtils.map.fragment.MapFragment;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.utils.PhoneUtils;
import com.palmap.BluetoothUtils.widget.ActionBar;
import com.palmap.BluetoothUtils.widget.NoScrollViewPager;
import com.palmaplus.nagrand.core.Engine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/10/15.
 */
public class MapBeaconActivity extends BaseActivity {
  private static boolean BEACON_INFO_CHANGED = false;
  private static final int NAV_ITEM_NUM = 2;
  public ActionBar mActionBar;
  private ArrayList<Fragment> mFragmentList;
  private ArrayList<LinearLayout> mNavViewList;
  private ArrayList<TextView> mNavViewImgList;
  private NoScrollViewPager mViewPager;
  private MapFragment mMapFragment;
  private BeaconListFragment mBeaconListFragment;

  private BeaconManager mBeaconManager;

  public PalMap mPalMap; // 当前商场
//  public Scene mScene; // 当前场景
//  public String appKey; // 当前场景appKey   放到palmap当中
  private int mCurrIndex; // 当前索引编号

  public static boolean hasDownload;




//  //用于自动下载
//  private MapDataSerializable mMapDataSerializable = null;
//  private DataSource mDataSourceDownload;
//  private List<Floor> mFloorListDataDownload;

  private final String TAG = "MapBeaconActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_beacon);

    initData();

    initView();

    initEngine();

  }

  @Override
  protected void onStart() {
    super.onStart();
    if(!mMapFragment.isScan()) {
      if (PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)) {
        DialogUtils.showShortToast("加载在线数据");
        loadBeaconDataAPIV2();
      } else if (mBeaconManager.isBeaconDownload() == true) {
        DialogUtils.showShortToast("加载离线数据");
        loadBeaconDataOffline();
      } else {
        DialogUtils.showLongToast("没有离线数据，请连接网络");
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 检测网络是否连接,同步本地数据
    if (PhoneUtils.isNetWorkConnected(BluetoothUtilsApplication.instance)) {
      if (mBeaconManager.hasBeaconDataUpload()) {
        DialogUtils.showDialog(this, "有未上传的beacon数据，是否立即上传？", new DialogUtils.DialogCallBack() {
          @Override
          public void onOk() {

            uploadBeaconDataAPIV2(new OnUploadComplete() {
              @Override
              public void onComplete() {
//                   DialogUtils.showShortToast("上传完成！");
              }
            });

          }

          @Override
          public void onCancel() {

          }
        });
      }
    }
  }

  /*
        * 接受传递的参数或初始化本界面数据
        * */
  private void initData() {
    mPalMap = (PalMap) getIntent().getSerializableExtra(Constant.TAG_MAP_OBJECT);
//    appKey = getIntent().getStringExtra(Constant.TAG_APPKEY);
    hasDownload = getIntent().getBooleanExtra(Constant.TAG_HAS_DOWNLOAD,false);

      //初始化蓝牙点位管理器
    mBeaconManager = new BeaconManager(this,mPalMap,getIntent().getBooleanExtra(Constant.TAG_FROM_HISTORY,false));

  }

  /*
  *  根据appkey初始化引擎
  * */
  private void initEngine() {
    Engine engine = Engine.getInstance();
    engine.startWithLicense(mPalMap.getAppKey(), this); // 设置验证license

  }


  /*
  * 初始化actionBar
  * */
  private void initActionBar() {
    mActionBar = (ActionBar) findViewById(R.id.action_bar);
    mActionBar.setTitle(mPalMap == null ? "商场地图" : mPalMap.getName());
    mActionBar.setLeftVisible(true);
    mActionBar.setRightBg("上传", 0);


    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
      @Override
      public void onLeft() {
        if (mMapFragment.isScan()){
          DialogUtils.showDialog(MapBeaconActivity.this, "正在扫描Beacon，是否停止扫描？", new DialogUtils.DialogCallBack() {
            @Override
            public void onOk() {
              //关闭巡检
              mMapFragment.stopScan();
            }

            @Override
            public void onCancel() {

            }
          });

        }else {
          MapBeaconActivity.this.finishActivityOnAnimation(MapBeaconActivity.this);
        }
      }

      @Override
      public void onRight() {
        if ("上传".equals(mActionBar.getRightText())) { // 上传beacon数据

          uploadBeaconDataAPIV2(new OnUploadComplete() {
            @Override
            public void onComplete() {

            }
          });
        }
//          else if ("编辑".equals(mActionBar.getRightText())) {
//          mActionBar.setRightBg("删除", 0);
//          if (mCurrIndex == 1) {
//            ((BeaconListFragment) mFragmentList.get(1)).showSelectBox(true);
//          }
//          // do something
//          DialogUtils.showShortToast("编辑");
//        } else if ("删除".equals(mActionBar.getRightText())) {
//          mActionBar.setRightBg("编辑", 0);
//          if (mCurrIndex == 1) {
//            ((BeaconListFragment) mFragmentList.get(1)).showSelectBox(false);
//          }
//          // 删除数据，同时更新view
//          mSQLiteHelper.deleteBeacons(mBeaconList);
//          ((BeaconListFragment) mFragmentList.get(1)).showBeaconList(mBeaconList);
//        } else if ("地图显示".equals(mActionBar.getRightText())) {
//          mActionBar.setRightBg("删除", 0);
//          if (mCurrIndex == 1) {
//            ((BeaconListFragment) mFragmentList.get(1)).showSelectBox(true);
//          }
//          // do something
//          DialogUtils.showShortToast("编辑");
//        } else if ("取消".equals(mActionBar.getRightText())) {
//          mActionBar.setRightBg("删除", 0);
//          if (mCurrIndex == 1) {
//            ((BeaconListFragment) mFragmentList.get(1)).showSelectBox(true);
//          }
//          // do something
//          DialogUtils.showShortToast("编辑");
//        }
      }
    });
  }


  /*
  * 初始化view控件
  * */
  private void initView() {
    initActionBar();

    // 初始化界面导航控件
    mNavViewList = new ArrayList<LinearLayout>(NAV_ITEM_NUM);
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_beacon_map_ll); // map
    linearLayout.setOnClickListener(new OnNavViewListener(0));
    mNavViewList.add(linearLayout);
    linearLayout = (LinearLayout) findViewById(R.id.map_beacon_beacon_ll); // beacon
    linearLayout.setOnClickListener(new OnNavViewListener(1));
    mNavViewList.add(linearLayout);

    mNavViewImgList = new ArrayList<TextView>(NAV_ITEM_NUM);
    mNavViewImgList.add((TextView) findViewById(R.id.map_beacon_map_img));
    mNavViewImgList.add((TextView) findViewById(R.id.map_beacon_beacon_img));

    // 初始化viewPager
    mViewPager = (NoScrollViewPager) findViewById(R.id.map_beacon_view_pager);
    mViewPager.setFocusable(false);
    mFragmentList = new ArrayList<Fragment>(NAV_ITEM_NUM);
    mMapFragment = new MapFragment();

//    Bundle bundle = new Bundle();
//    bundle.putSerializable(Constant.TAG_BEACON_MANAGER_OBJECT, mPalMap);
//    mMapFragment.setArguments(bundle);
    mFragmentList.add(mMapFragment);
    mBeaconListFragment = new BeaconListFragment();
//    bundle = new Bundle();
//    bundle.putSerializable(Constant.TAG_MAP_OBJECT, mPalMap);
//    mBeaconListFragment.setArguments(bundle);
    mFragmentList.add(mBeaconListFragment);


    mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList)); // 设置适配器
    mViewPager.setCurrentItem(0); //设置当前显示标签页为第一页
    changeNavSelectedItem(0); // 第一项为选中项
    mViewPager.setOnPageChangeListener(new OnMyPageChangeListener()); //页面变化时的监听器
    mViewPager.setOnTouchListener(new View.OnTouchListener() {//禁止左右滑动，防止地图滑动误操作
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });

  }

  /*
  * 设置导航栏选中项
  * */
  private void changeNavSelectedItem(int index) {
    // 设置textView
    if (index == 0) { // 由列表界面到地图界面
      mNavViewImgList.get(0).setBackgroundResource(R.drawable.ico_map_map_sel);
      mNavViewImgList.get(1).setBackgroundResource(R.drawable.ico_map_list_nor);

//      if (SQLiteHelper.isDataChanged()) {
//        LogUtils.w("更新beacon数据");
//        mBeaconManager.refeshBeaconsByFloorId(((MapFragment) mFragmentList.get(0)).mFloorId);
//        ((MapFragment) mFragmentList.get(0)).addBeaconOverlay(mBeaconManager.getBeaconList());
//      }
    } else { // 由地图界面到列表界面
      mNavViewImgList.get(1).setBackgroundResource(R.drawable.ico_map_list_sel);
      mNavViewImgList.get(0).setBackgroundResource(R.drawable.ico_map_map_nor);


    }

    // 设置actionBar
    if (index == 0) {//地图页面
      mActionBar.setTitle(mPalMap == null ? "商场地图" : mPalMap.getName());
      mActionBar.setLeftVisible(true);
      mActionBar.setRightBg("上传", 0);
    } else if (index == 1) {//beacon页面
      mActionBar.setTitle("Beacon点位列表");
      mActionBar.setLeftVisible(false);
//      mActionBar.setRightBg("编辑", 0);
      ((BeaconListFragment) mFragmentList.get(1)).showSelectBox(false);
    }
  }


  /*

  *  导航栏监听类
  * */
  public class OnNavViewListener implements View.OnClickListener {
    private int index = 0;

    public OnNavViewListener(int index) {
      this.index = index;
    }

    @Override
    public void onClick(View v) {
      mViewPager.setCurrentItem(index);
    }

  }

  /*
  * viewPager页面切换监听类
  * */
  public class OnMyPageChangeListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int i, float v, int i1) {
//      LogUtils.e("exp", "onPageScrolled -> i = " + i + ": v = " + v + "; i1 = " + i1);
    }

    @Override
    public void onPageSelected(int i) {
      LogUtils.e("onPageSelected -> i = " + i);
      mCurrIndex = i;
      changeNavSelectedItem(mCurrIndex); // 设置导航栏选中项

//      if (mCurrIndex == 1) {
////        ((BeaconListFragment) mFragmentList.get(1)).showBeaconList(mBeaconList);
//
//      }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
      LogUtils.e("onPageScrollStateChanged -> i = " + i);
    }
  }

  /*
  用于在mapfragment更新beaconlistfragment的ui
   */
  public ArrayList<Fragment> getmFragmentList() {
    return mFragmentList;
  }


  /*
    * 下载beacon数据库
    * 用工作线程
    * */
  private void loadBeaconDataAPIV2() {
    if ( mPalMap != null) {
      showProgress("提示", "加载Beacon数据中...");
      DataProviderCenter.getInstance().getBeaconsAPIV2(mPalMap.getMapId(), mPalMap.getSceneId(), new HttpDataCallBack<String>() {
        @Override
        public void onError(int errorCode) {
          LogUtils.e("errorCode = " + errorCode);
          closeProgress();
          HttpErrorUtil.showErrorToast(errorCode);
        }

        @Override
        public void onComplete(String content) {
          closeProgress();
          try {
            JSONObject object = new JSONObject(content);

            // 解析返回beacon数据，并存入本地数据库
            mBeaconManager.initBeaconListFromJson(object);

            showProgress("提示", "beacon数据正在插入数据库，请稍后...");

                mBeaconManager.saveBeaconsToDb();

                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    closeProgress();
                    mMapFragment.refeshBeaconMarkAndList();
                  }
                });

          } catch (JSONException e) {
            e.printStackTrace();
            DialogUtils.showShortToast("beacon点位数据错误");
          }

        }
      });
    } else {
      DialogUtils.showShortToast("数据传输有误，请返回，重新进入！");
    }
  }


  public void uploadBeaconDataAPIV2(final OnUploadComplete onUploadComplete) {//将修改过的本地数据库中data信息上传服务器,可能需要反射调用，故用public
    //新接口
    List<Beacon> beaconList = mBeaconManager.getUploadBeaconList();

    showProgress("提示", "beacon数据上传中,请稍后0%...");
    DataProviderCenter.getInstance().sendBeaconsAPIV2(this, mPalMap.getMapId(), mPalMap.getSceneId(), beaconList, new HttpDataCallBack() {
      @Override
      public void onError(int errorCode) {
        closeProgress();
        LogUtils.e("sendBeaconsAPIV2 errorCode = " + errorCode);
        if (errorCode == ErrorCode.CODE_NO_INTERNET) {
          DialogUtils.showShortToast("无网络连接，无法上传！");
        } else if (errorCode == 400) {
          DialogUtils.showLongToast("beacon参数不合法，请检查(如uuid为36个字符)，上传失败！");
        } else {
          DialogUtils.showShortToast("网络信号弱，无法上传，请更换wifi或网络运营商。");
        }
      }

      @Override
      public void onComplete(Object content) {
        closeProgress();
        DialogUtils.showShortToast("批量上传beacon完毕。");
        onUploadComplete.onComplete();
      }
    }, new DataProviderCenter.OnUpdateProcess() {
      @Override
      public void onUpdate(int percent) {
        showProgress("提示", "beacon数据上传中,请稍后" + percent + "%...");
      }
    });

  }


  public interface OnUploadComplete {
    void onComplete();
  }

  public int getBeaconNum() {
    return mBeaconManager.getBeaconNum();
  }

  @Override
  protected void onDestroy() {
//    mSQLiteHelper.setDataChanged(false);
    super.onDestroy();

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mMapFragment.isScan()){
        DialogUtils.showDialog(MapBeaconActivity.this, "正在扫描Beacon，是否停止扫描？", new DialogUtils.DialogCallBack() {
          @Override
          public void onOk() {
            //关闭巡检
            mMapFragment.stopScan();
          }

          @Override
          public void onCancel() {

          }
        });

      }else {
        MapBeaconActivity.this.finishActivityOnAnimation(MapBeaconActivity.this);
      }
    }
    return true;
  }


  /*
  从本地数据库获取beacon信息
   */
  private void loadBeaconDataOffline() {
    showProgress("提示", "加载离线Beacon数据中...");
    mBeaconManager.refeshBeaconsByFloorId( ((MapFragment) mFragmentList.get(0)).mFloorId);//更新beacon

    closeProgress();

    //初始化便于修改巡检状态
    mBeaconListFragment.initBeaconList(mBeaconManager.getBeaconList());
  }

  /*
  判断新添加的beacon是否与现有(map下)重复
   */
  public int isAlreadyExists(Beacon beacon, int minor) {
    List<Beacon> beaconList = null;

    beaconList = mBeaconManager.getBeaconList();
    for (Beacon b : beaconList) {
      if (minor == b.getMinor() &&
          beacon.getMajor() == b.getMajor() &&
          beacon.getUuid().equals(b.getUuid())) {
        return minor;
      }
    }
    return -1;
  }



  public BeaconManager getmBeaconManager() {
    return mBeaconManager;
  }
}
