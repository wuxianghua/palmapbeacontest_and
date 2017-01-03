package com.palmap.BluetoothUtils.widget;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by zhang on 2015/4/3.
 */
public class MyProgressDialog extends ProgressDialog{
  public MyProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  public MyProgressDialog(Context context) {
    super(context);
  }
}
