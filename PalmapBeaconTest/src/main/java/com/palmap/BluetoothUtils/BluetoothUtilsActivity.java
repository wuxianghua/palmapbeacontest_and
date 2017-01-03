package com.palmap.BluetoothUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.main.activity.LoginActivity;
import com.palmap.BluetoothUtils.main.constant.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class BluetoothUtilsActivity extends BaseActivity {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bluetooth_utils);

    if (!Constant.isBellnet){
      ImageView imageView = (ImageView)findViewById(R.id.iv_logo);
      imageView.setImageResource(R.drawable.logo_login);
    }else {
      ImageView imageView = (ImageView)findViewById(R.id.iv_logo);
      imageView.setImageResource(R.drawable.bellnet_logo);
      TextView textView = (TextView)findViewById(R.id.tv_right);
      textView.setVisibility(View.GONE);
    }

    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Intent intent = new Intent(BluetoothUtilsActivity.this, LoginActivity.class);
        startActivityOnAnimation(intent);

        new Timer().schedule(new TimerTask() {
          @Override
          public void run() {
            finish();
          }
        }, 1000);
      }
    }, 1500);

  }
}
