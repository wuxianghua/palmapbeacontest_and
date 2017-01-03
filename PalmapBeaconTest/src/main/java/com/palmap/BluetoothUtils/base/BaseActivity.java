package com.palmap.BluetoothUtils.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bugtags.library.Bugtags;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.utils.SystemBarTintManager;
import com.palmap.BluetoothUtils.widget.MyProgressDialog;

/**
 * Created by zhang on 2015/3/30.
 * 所有activity的父类
 *   初始化activity的公共属性，或声明activity的共享属性
 */
public abstract class BaseActivity extends FragmentActivity {
  private MyProgressDialog progressDialog = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LogUtils.w("onCreate(Bundle savedInstanceState)");
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //设置全屏
//    initStatusBar();
  }

  @Override
  protected void onResume() {
    super.onResume();
    //注：Bugtags回调 1
    Bugtags.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //注：Bugtags回调 2
    Bugtags.onPause(this);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    //注：Bugtags回调 3
    Bugtags.onDispatchTouchEvent(this, ev);
    return super.dispatchTouchEvent(ev);
  }

  /*
  *  启动activity
  * */
  protected void startActivityOnAnimation(Intent intent){
    startActivity(intent);
    overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
  }

  /*
  *  带返回参数，启动activity
  *  @param intent 带启动activity
  *  @param requestCode
  *  @params 传递的参数
  * */
  protected void startActivityForResultOnAnimation(Intent intent, int requestCode){
    startActivityForResult(intent, requestCode);
    overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
  }

  /*
  *  结束activity
  * */
  protected void finishActivityOnAnimation(Activity activity){
    activity.finish();
    overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
  }

  /*
  *  显示进度条
  * */
  protected void showProgress(String title, String msg){
    if (isFinishing()){
      return;
    }
    if (progressDialog == null){
      progressDialog = new MyProgressDialog(this);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.setIndeterminate(false);
      progressDialog.setCancelable(true);
    }

    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);

    if (!progressDialog.isShowing()){
      progressDialog.show();
    }
  }

  /*
  *  关闭进度条
  * */
  protected void closeProgress(){
    LogUtils.e( "BaseActivity->closeProgress()");
    if (progressDialog != null && progressDialog.isShowing()){
      progressDialog.dismiss();
    }
  }

  protected MyProgressDialog getProgressDialog() {
    return progressDialog;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    closeProgress();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK){
      // 如果是返回键，动画结束该activity
      finishActivityOnAnimation(this);
    }
    return super.onKeyDown(keyCode, event);
  }

  @TargetApi(19)
  private void setTranslucentStatus(boolean on){
    Window window =  getWindow();
    WindowManager.LayoutParams winParams = window.getAttributes();
    final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    final int bits_nav = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
    if (on){
      winParams.flags |= bits;
      winParams.flags |= bits_nav;
    } else {
      winParams.flags &= ~bits;
      winParams.flags &= ~bits_nav;
    }
    window.setAttributes(winParams);
  }

  /*
  * 初始化状态栏
  * */
  protected void initStatusBar(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ // 19
      setTranslucentStatus(true);
    }
    SystemBarTintManager tintManager = new SystemBarTintManager(this);
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setNavigationBarTintEnabled(true);
    tintManager.setTintResource(R.color.theme_bg_color);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    LogUtils.w( "hasFocus = " + hasFocus);
    super.onWindowFocusChanged(hasFocus);
  }
}
