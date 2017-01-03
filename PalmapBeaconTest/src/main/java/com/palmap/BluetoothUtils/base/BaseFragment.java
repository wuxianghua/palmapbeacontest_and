package com.palmap.BluetoothUtils.base;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.widget.MyProgressDialog;

/**
 * Created by zhang on 2015/10/15.
 */
public class BaseFragment extends Fragment {
  private MyProgressDialog progressDialog = null;

  /*
  *  显示进度条
  * */
  protected void showProgress(String title, String msg){
    doCreateProgressDialog(title, msg);

    if (!progressDialog.isShowing()){
      progressDialog.show();
    }
  }

  /*
  *  显示进度条
  * */
  protected void showProgress(Handler handler, final String title, final String msg){
    doCreateProgressDialog(title, msg);
    if (!progressDialog.isShowing()){
      handler.post(new Runnable() {
        @Override
        public void run() {
          progressDialog.show();
        }
      });
    }
  }

  private void doCreateProgressDialog(String title, String msg){
    if (progressDialog == null){
      progressDialog = new MyProgressDialog(getActivity());
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.setIndeterminate(false);
      progressDialog.setCancelable(true);
    }

    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);
  }

  /*
  *  关闭进度条
  * */
  protected void closeProgress(){
    LogUtils.e( "BaseFragment->closeProgress()");
    if (progressDialog != null && progressDialog.isShowing()){
      progressDialog.dismiss();
    }
  }

  /*
  *  关闭进度条
  * */
  protected void closeProgress(Handler handler){
    LogUtils.i("BaseFragment->closeProgress()");
    if (progressDialog != null && progressDialog.isShowing()&&handler.getLooper()!=null){
      handler.post(new Runnable() {
        @Override
        public void run() {
          progressDialog.dismiss();
        }
      });
    }
  }

}
