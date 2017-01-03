//package com.palmap.BluetoothUtils.main.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.TextView;
//
//import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
//import com.palmap.BluetoothUtils.R;
//import com.palmap.BluetoothUtils.base.BaseActivity;
//import com.palmap.BluetoothUtils.database.SQLiteHelper;
//import com.palmap.BluetoothUtils.http.DataProviderCenter;
//import com.palmap.BluetoothUtils.http.HttpDataCallBack;
//import com.palmap.BluetoothUtils.http.model.ErrorCode;
//import com.palmap.BluetoothUtils.main.constant.Constant;
//import com.palmap.BluetoothUtils.main.model.Scene;
//import com.palmap.BluetoothUtils.tools.DialogUtils;
//import com.palmap.BluetoothUtils.widget.ActionBar;
//import com.palmap.BluetoothUtils.widget.widget.OnWheelScrollListener;
//import com.palmap.BluetoothUtils.widget.widget.WheelView;
//import com.palmap.BluetoothUtils.adapters.ArrayWheelAdapter;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import java.util.Calendar;
//
///**
// * Created by zhang on 2015/10/12.
// */
//public class SceneActivity extends BaseActivity implements OnWheelScrollListener,View.OnClickListener{
//  private ActionBar mActionBar;
//  private WheelView mSceneView;
//  private TextView mEntryScene;
//  private TextView mNoData;
//  private boolean offlineMode;
//
//  private Scene[] mSceneArray;
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_scene);
//
//    initData();
//
//    initActionBar();
//
//    initView();
//
//    if (offlineMode){
//      loadDataOffline();
//    }else{
//      loadData();
//    }
//  }
//
//private void initData(){
//  offlineMode = (Boolean) getIntent().getSerializableExtra(Constant.TAG_HAS_DOWNLOAD);
//}
//
//  /*
//  *  初始化actionBar
//  * */
//  private void initActionBar() {
//    mActionBar = (ActionBar) findViewById(R.id.action_bar);
//    mActionBar.setTitle("场景选取");
//    mActionBar.setLeftVisible(true);
//    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
//      @Override
//      public void onLeft() {
//        SceneActivity.this.finishActivityOnAnimation(SceneActivity.this);
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
//  private void initView(){
//    mNoData = (TextView) findViewById(R.id.scene_no_scene_data);
//    mNoData.setVisibility(View.GONE);
//
//    mSceneView = (WheelView) findViewById(R.id.scene_choose_scene);
//    mSceneView.addScrollingListener(this);
//    mSceneView.setVisibleItems(2);
//
//    mEntryScene = (TextView) findViewById(R.id.scene_entry_scene);
//    mEntryScene.setOnClickListener(this);
//    mEntryScene.setClickable(false);
//    mEntryScene.setBackgroundResource(R.drawable.btn_bg_shape);
//  }
//
//  /*
//  *  加载场景数据
//  * */
//  private void loadData(){
//    showProgress("提示", "加载场景列表中...");
//    DataProviderCenter.getInstance().getScenes(BluetoothUtilsApplication.userName, null, new HttpDataCallBack<String>() {
//      @Override
//      public void onError(int errorCode) {
//        LogUtils.e("SceneActivity", "errorCode = " + errorCode);
//        closeProgress();
//        if (errorCode == ErrorCode.CODE_NO_INTERNET){
//          DialogUtils.showShortToast("无网络连接！");
//        } else{
//          DialogUtils.showShortToast("网络连接错误！");
//        }
//      }
//
//      @Override
//      public void onOk(String content) { // 返回JSONArray型数据
//        closeProgress();
//        try {
//          JSONArray jsonArray = new JSONArray(content);
//          if (jsonArray != null && jsonArray.length() > 0){
//            mSceneArray = Scene.getSceneArray(jsonArray);
//            mSceneView.setViewAdapter(new ArrayWheelAdapter<Scene>(SceneActivity.this, mSceneArray));
//            mSceneView.setCurrentItem(1);
//            mEntryScene.setClickable(true);
//            mEntryScene.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//
//            //离线
//            SQLiteHelper sqLiteHelper = new SQLiteHelper(SceneActivity.this);
//            sqLiteHelper.createDatabaseTable(SQLiteHelper.TABLE_USER,null);
//            sqLiteHelper.saveObject(SQLiteHelper.SCENEARRAY,mSceneArray,BluetoothUtilsApplication.userName);
//
//          } else {
//            DialogUtils.showShortToast("返回数据有误！");
//          }
//        } catch (JSONException e) {
//          e.printStackTrace();
//          DialogUtils.showShortToast("返回数据有误！");
//        }
//      }
//    });
//  }
//  /*
//  *  加载离线场景数据
//  * */
//  private void loadDataOffline(){
//    SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
//    mSceneArray = (Scene[]) sqLiteHelper.getObject(SQLiteHelper.SCENEARRAY,BluetoothUtilsApplication.userName);
//    mSceneView.setViewAdapter(new ArrayWheelAdapter<Scene>(SceneActivity.this, mSceneArray));
//    mSceneView.setCurrentItem(1);
//    mEntryScene.setClickable(true);
//    mEntryScene.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//  }
//
//  @Override
//  public void onClick(View v) {
//    switch (v.getId()){
//      case R.id.scene_entry_scene:
//        Intent intent = new Intent(SceneActivity.this, MallSelectActivity.class);
//        intent.putExtra(Constant.TAG_APPKEY, mSceneArray[mSceneView.getCurrentItem()]);
//        intent.putExtra(Constant.TAG_HAS_DOWNLOAD, offlineMode);
//        SceneActivity.this.startActivityOnAnimation(intent);
//        break;
//    }
//  }
//
//  @Override
//  public void onScrollingStarted(WheelView wheel) {
//
//  }
//
//  @Override
//  public void onScrollingFinished(WheelView wheel) {
//    if (wheel == mSceneView){
//      LogUtils.w("SceneActivity", "onScrollingFinished->" + mSceneArray[wheel.getCurrentItem()]);
//      if (wheel.getCurrentItem() == 0){
//        mEntryScene.setClickable(false);
//        mEntryScene.setBackgroundResource(R.drawable.btn_bg_shape);
//      } else {
//        mEntryScene.setClickable(true);
//        mEntryScene.setBackgroundResource(R.drawable.btn_bg_shape_hover);
//      }
//    }
//  }
//  private long mLastTime=0;
//  @Override
//  public boolean onKeyDown(int keyCode, KeyEvent event) {
//    if (keyCode == KeyEvent.KEYCODE_BACK){
//      if(Constant.isBellnet){
//        // 如果是返回键，动画结束该activity
//        long current = Calendar.getInstance().getTimeInMillis();
//        if ((current - mLastTime) / 1000 >= 2){
//          DialogUtils.showShortToast("再按一次返回退出");
//          mLastTime = current;
//          return true;
//        } else {
//          finish();
//          System.exit(0);
//
////          BluetoothUtilsApplication.exit();
//        }
//      }
//
//    }
//    return super.onKeyDown(keyCode, event);
//  }
//
//}
