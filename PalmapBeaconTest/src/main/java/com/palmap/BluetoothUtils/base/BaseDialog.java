package com.palmap.BluetoothUtils.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/11/10.
 */
public class BaseDialog extends Dialog {
  private static List<BaseDialog> cDialogList;

  public BaseDialog(Context context) {
    super(context, R.style.myDialogTheme);
    addDialog(context);
  }

  public BaseDialog(Context context, int theme) {
    super(context, theme);
    addDialog(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LogUtils.w("onCreate(Bundle savedInstanceState)");
    super.onCreate(savedInstanceState);
  }

  /*
    * 将新创建的dialog放入链表
    * */
  private void addDialog(Context context){

    if (cDialogList == null){
      cDialogList = new ArrayList<BaseDialog>();
    }
    cDialogList.add(this);
  }

  /*
  * 设置对话框在窗口中显示的位置
  * */
  public void showDialog(int x, int y){
    LogUtils.w( "x = " + x + "; y = " + y);
    Window window = this.getWindow();
    WindowManager.LayoutParams wl = window.getAttributes();
    wl.x = x;
    wl.y = y;
    wl.gravity = Gravity.LEFT | Gravity.TOP;
    window.setAttributes(wl);
    show();
  }

  /*
  *  清楚该界面所有 dialog， 防止窗口泄露
  *  可以在退出activity时调用
  * */
  public static void clearDialogList(){
    if (cDialogList != null && cDialogList.size() > 0){
      for (BaseDialog dialog : cDialogList){
        if (dialog != null){
          dialog.dismiss();
        }
      }

      cDialogList.clear();
      cDialogList = null;
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    LogUtils.w("hasFocus = " + hasFocus);
    super.onWindowFocusChanged(hasFocus);
  }
}
