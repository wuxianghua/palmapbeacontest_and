package com.palmap.BluetoothUtils.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseDialog;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmap.BluetoothUtils.map.activity.MapBeaconActivity;
import com.palmap.BluetoothUtils.utils.DialogUtils;

/**
 * Created by zhang on 2015/11/23.
 * 修改beacon的对话框 changeed by eric 2016.9.21
 */
public class ModifyBeaconDialog extends BaseDialog implements View.OnClickListener{
  private EditText mUUID;
  private EditText mMajor;
  private EditText mMinor;
  private TextView mOK;
  private TextView mCancel;
  private Beacon mBeacon = null;
  private ToggleButton mEdit;
  private Button mMove;
  private Button mDelete;

  private MapBeaconActivity mContext;
  private OnModifyBeaconDialogListener onModifyBeaconDialogListener;

  public ModifyBeaconDialog(Context context, Beacon beacon, OnModifyBeaconDialogListener onModifyBeaconDialogListener){//修改现有beacon
    super(context);
    this.mContext = (MapBeaconActivity)context;
    this.mBeacon = beacon;

    this.onModifyBeaconDialogListener = onModifyBeaconDialogListener;
  }

  public ModifyBeaconDialog(Context context, OnModifyBeaconDialogListener onModifyBeaconDialogListener) {
    super(context);
    this.mContext = (MapBeaconActivity)context;
    this.onModifyBeaconDialogListener = onModifyBeaconDialogListener;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_add_beacon);
    setCanceledOnTouchOutside(false);

    mUUID = (EditText) findViewById(R.id.add_beacon_uuid);
    mMajor = (EditText) findViewById(R.id.add_beacon_major);
    mMinor = (EditText) findViewById(R.id.add_beacon_minor);
    mOK = (TextView) findViewById(R.id.add_beacon_ok);
    mOK.setOnClickListener(this);
    mCancel = (TextView) findViewById(R.id.add_beacon_cancel);
    mCancel.setOnClickListener(this);
    mMove = (Button)findViewById(R.id.btn_move);
    mDelete = (Button)findViewById(R.id.btn_delete);
    if (mBeacon==null){
      mDelete.setVisibility(View.GONE);
    }else{
      mDelete.setVisibility(View.VISIBLE);
    }
    mDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DialogUtils.showDialog(mContext,"是否删除该beacon？",new DialogUtils.DialogCallBack() {
          @Override
          public void onOk() {

            if(mBeacon != null){
              if (onModifyBeaconDialogListener != null){
                mBeacon.setAction(Beacon.ACTION_DELETE);
                onModifyBeaconDialogListener.onOk(mBeacon);
              }
              dismiss();
            }else{
              DialogUtils.showShortToast("错误，不能删除！");
            }
          }
          @Override
          public void onCancel() {
          }
        });
      }
    });
    mEdit = (ToggleButton)findViewById(R.id.tbtn_edit);
    mEdit.setChecked(false);
    mEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){//编辑
          mUUID.setEnabled(true);
          mMajor.setEnabled(true);
          mMinor.setEnabled(true);
        }else {//不能编辑
          mUUID.setEnabled(false);
          mMajor.setEnabled(false);
          mMinor.setEnabled(false);
        }
      }
    });
    mUUID.setEnabled(false);
    mMajor.setEnabled(false);
    mMinor.setEnabled(false);

    if(mBeacon !=null){
      mUUID.setText(mBeacon.getUuid());
      mMajor.setText(mBeacon.getMajor()+"");
      mMinor.setText(mBeacon.getMinor()+"");
    }else{
      if (mContext.getmBeaconManager().getBeaconList()!=null) {
          mUUID.setText(mContext.getmBeaconManager().getDefaultUuid());
          mMajor.setText(mContext.getmBeaconManager().getDefaultMajor()+"");
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.add_beacon_cancel: // 关闭对话框
        this.dismiss();
        break;

      case R.id.add_beacon_ok: // 添加beacon
        Beacon beacon = null;
        if(mBeacon != null){
          beacon = mBeacon;
          beacon.setAction(Beacon.ACTION_UPDATE);//更新
        }else{
          beacon = new Beacon();
          beacon.setAction(Beacon.ACTION_ADD);//添加

        }
        beacon.setScaned(false);

        String txt;
        txt = mUUID.getText().toString();
        if (TextUtils.isEmpty(txt)){
          DialogUtils.showShortToast("UUID不能为空");
          return;
        }else if (txt.length()!=36){
          DialogUtils.showShortToast("UUID长度不正确，请检查");
          return;
        }
        beacon.setUuid(txt);

        txt = mMajor.getText().toString();
        if (TextUtils.isEmpty(txt)){
          DialogUtils.showShortToast("Major不能为空");
          return;
        }
        beacon.setMajor(Integer.valueOf(txt));

        txt = mMinor.getText().toString();
        if (TextUtils.isEmpty(txt)){
          DialogUtils.showShortToast("Minor不能为空");
          return;
        }
        //beacon参数重复检查
        int t = mContext.isAlreadyExists(beacon,Integer.valueOf(txt));
        if(t != -1){
          DialogUtils.showShortToast("该地图下已有minor值为"+t+"的点位，无法重复添加。");
          return;
        }
        beacon.setMinor(Integer.valueOf(txt));

        if (onModifyBeaconDialogListener != null){
          onModifyBeaconDialogListener.onOk(beacon);
        }
        dismiss();
        break;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK){ // 按返回键，不销毁对话框
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  public interface OnModifyBeaconDialogListener {
    void onOk(Beacon beacon);
  }
}
