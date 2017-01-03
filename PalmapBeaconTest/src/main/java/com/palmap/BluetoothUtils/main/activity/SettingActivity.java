package com.palmap.BluetoothUtils.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.impl.OnDeleteComplete;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.utils.FileSizeUtils;

public class SettingActivity extends BaseActivity {
private Button btn_delete;
  private Button btn_help;
  private Button btn_update;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    btn_delete = (Button) findViewById(R.id.btn_delete_all);
    btn_delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SQLiteHelper.askAndDeleteAllTable(SettingActivity.this, new OnDeleteComplete() {
          @Override
          public void onComplete() {
//            refeshCashSize();
            btn_delete.setText("删除缓存（0KB）");
          }
        });//删除离线的beacon和floorid数据

      }
    });

    btn_help = (Button)findViewById(R.id.btn_help);
    btn_help.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SettingActivity.this,HelpActivity.class));
      }
    });

    refeshCashSize();
  }

  private void refeshCashSize(){
    double fs1=FileSizeUtils.getFileOrFilesSize(Constant.OFFLINE_DATA_PATH,FileSizeUtils.SIZETYPE_KB);
    double fs2=FileSizeUtils.getFileOrFilesSize(Constant.OFFLINE_DATABASE_PATH,FileSizeUtils.SIZETYPE_KB);
    btn_delete.setText("删除缓存（"+(fs1+fs2-16.0)+"KB）");
  }


}
