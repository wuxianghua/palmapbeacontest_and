package com.palmap.BluetoothUtils.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.database.ScanHistorySerializable;
import com.palmap.BluetoothUtils.main.adapter.ScanHistoryListAdapter;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.widget.ActionBar;

import java.util.List;

public class ScanHistoryActivity extends BaseActivity {
  private PalMap mPalMap;
  private ListView mHistoryListView;
  private ActionBar mActionBar;
  private TextView tv_delete;
  private TextView tv_merge;
  private TextView tv_select_all;
  private LinearLayout ll_edit;

  private List<ScanHistorySerializable> mScanHistoryList;
  private ScanHistoryListAdapter mScanHistoryListAdapter;
  private SQLiteHelper sqLiteHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan_history);

    initData();
    initView();
    initActionBar();


  }

  private void initView(){
    tv_delete = (TextView) findViewById(R.id.delete);
    tv_delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkAndDeleteItem();
      }
    });
    tv_merge = (TextView) findViewById(R.id.merge);
    tv_merge.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO: 2016/12/2  合并记录
//        checkAndMergeItem();
      }
    });
    tv_select_all = (TextView) findViewById(R.id.select_all);
    tv_select_all.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectAllItem();
      }
    });
    ll_edit = (LinearLayout) findViewById(R.id.edit_list);
    ll_edit.setVisibility(View.GONE);
    mHistoryListView = (ListView) findViewById(R.id.scan_history_list_view);


  }

  @Override
  protected void onStart() {
    super.onStart();
    

    //初始化列表
    initScanHistoryList();
  }

  private void checkAndDeleteItem() {
    DialogUtils.showDialog(this,"是否删除记录？", new DialogUtils.DialogCallBack() {
      @Override
      public void onOk() {

      }

      @Override
      public void onCancel() {

      }
    });
  }

  private void selectAllItem() {
    mScanHistoryListAdapter.setAllChecked(true);
  }

  private void initData(){
    mPalMap = (PalMap) getIntent().getSerializableExtra(Constant.TAG_MAP_OBJECT);

    sqLiteHelper = SQLiteHelper.getInstance(this);


  }
  /*
    * 初始化列表
    * */
  public void initScanHistoryList(){
    //继续巡检回来会更新巡检列表
    mScanHistoryList = sqLiteHelper.getLocalHistoryByMapID(mPalMap.getSceneId(),mPalMap.getMapId());
    if (mScanHistoryList==null||mScanHistoryList.size()==0){//没有本地巡检记录
      DialogUtils.showShortToast("mapID="+ mPalMap.getMapId()+"没有本地巡检记录");
      return;
    }

    if (mScanHistoryListAdapter == null){
      mScanHistoryListAdapter = new ScanHistoryListAdapter(this, mPalMap,mScanHistoryList);
      mHistoryListView.setAdapter(mScanHistoryListAdapter);
    }else
      mScanHistoryListAdapter.refeshScanHistoryList(mPalMap,mScanHistoryList);

  }

  /*
    * 初始化actionBar
    * */
  private void initActionBar() {
    mActionBar = (ActionBar) findViewById(R.id.action_bar);
    mActionBar.setTitle(mPalMap == null ? "巡检记录(空)" : mPalMap.getName());
    mActionBar.setLeftVisible(true);
    mActionBar.setRightBg("编辑", 0);



    mActionBar.setOnActionBarListener(new ActionBar.OnActionBarListener() {
      @Override
      public void onLeft() {
        finishActivityOnAnimation(ScanHistoryActivity.this);
      }

      @Override
      public void onRight() {
        if ("编辑".equals(mActionBar.getRightText())) {
          ll_edit.setVisibility(View.VISIBLE);
          mActionBar.setRightBg("取消", 0);
          mScanHistoryListAdapter.showSelectBox(true);
        }else if ("取消".equals(mActionBar.getRightText())) {
          ll_edit.setVisibility(View.GONE);
          mActionBar.setRightBg("编辑", 0);
          mScanHistoryListAdapter.showSelectBox(false);
        }

      }
    });
  }

}
