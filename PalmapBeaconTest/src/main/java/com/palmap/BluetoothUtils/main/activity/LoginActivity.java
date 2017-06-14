package com.palmap.BluetoothUtils.main.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.palmap.BluetoothUtils.BluetoothUtilsApplication;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.base.BaseActivity;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.http.DataProviderCenter;
import com.palmap.BluetoothUtils.http.HttpDataCallBack;
import com.palmap.BluetoothUtils.http.model.ErrorCode;
import com.palmap.BluetoothUtils.impl.OnDeleteComplete;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.LogUtils;
import com.palmap.BluetoothUtils.widget.ActionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by zhang on 2015/10/12.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
  private ActionBar mActionBar;
  private EditText mUserName;
  private EditText mPwd;
  private TextView mLogin;
  private TextView mTvSetting;
  private TextView mTvOfflone;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    initView();

  }

  /*
  * 初始化view控件
  * */
  private void initView() {
    mTvSetting = (TextView)findViewById(R.id.tv_setting);
    mTvSetting.setOnClickListener(this);
    mTvOfflone = (TextView)findViewById(R.id.tv_offline);
    mTvOfflone.setOnClickListener(this);


    mUserName = (EditText) findViewById(R.id.loading_et_user_name);
    mPwd = (EditText) findViewById(R.id.loading_et_password);

    mLogin = (TextView) findViewById(R.id.loading_btn_login);
    mLogin.setOnClickListener(this);

    if (Constant.isDebug) {
//      mUserName.setText("799505946@qq.com");
//      mPwd.setText("zhiluji123123");
//
      mUserName.setText("1641623024@qq.com");
      mPwd.setText("Palmap+2017");

//      mUserName.setText("469344063@qq.com");
//      mPwd.setText("niefei159915");//办公室定位

//      mUserName.setText("qiaoli.zhou@palmaplus.com");
//      mPwd.setText("sll243656500");

//      mUserName.setText("runzhi.cai@palmaplus.com");
//      mPwd.setText("crz5201314oh");

//      mUserName.setText("425678086@qq.com");
//      mPwd.setText("Palmap+2016");

//      mUserName.setText("2032239378@qq.com");
//      mPwd.setText("oiR6PVx8wZvUrksP");//蔡润之

//      mUserName.setText("chenchen.yu@palmaplus.com");
//      mPwd.setText("daniel007ycc");//虞晨晨


//      mUserName.setText("2303869773@qq.com");
//      mPwd.setText("Palmap+2016");//王炜
    }
    if (Constant.isFeiFan) {
      mUserName.setText("17lin@163.com");
      mPwd.setText("Palmap+2016");//非凡
    }
  }

  @Override
  protected void onResume() {
    if(Constant.isBellnet) {
      doLogin("qiaoli.zhou@palmaplus.com","sll243656500");
      finish();
    }
    super.onResume();
  }

  @Override
  public void onClick(View v) {
    String userName;
    String pwd;
    switch (v.getId()){
      case R.id.loading_btn_login://登陆
        // 检测用户名和密码是否输入
        userName = getUserName();
        pwd = getPwd();

        if ("".equals(userName)||"".equals(pwd))
          return;

        doLogin(userName,pwd);

        break;
      case R.id.tv_setting://设置
        startActivity(new Intent(LoginActivity.this,SettingActivity.class));
        break;

      case R.id.tv_offline://离线登陆
        // 检测用户名和密码是否输入
        userName = getUserName();
        pwd = getPwd();

        if ("".equals(userName)||"".equals(pwd))
          return;

        doLoginOffline(userName,pwd);
        break;

      default:
        break;
    }


  }

  private String getUserName(){
    // 检测用户名是否输入
    String userName = mUserName.getText().toString();
    if ("".equals(userName)){
      DialogUtils.showShortToast("用户名不能为空！");
      return "";
    }
    return userName;
  }
  private String getPwd(){
    // 检测密码是否输入
    String pwd = mPwd.getText().toString();
    if ("".equals(pwd)){
      DialogUtils.showShortToast("密码不能为空！");
      return "";
    }
    return pwd;
  }

  private void doLogin(String userName,String pwd){
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
      DialogUtils.showShortToast("抱歉，您的手机不支持低功耗蓝牙，无法巡检");
      return;
    }

    BluetoothUtilsApplication.userName = userName; // 记录用户名，用于后面请求参数

//        // 测试用
//        Intent intent = new Intent(LoginActivity.this, MapBeaconActivity.class);
//        this.startActivityOnAnimation(intent);
//        if (intent != null){
//          return;
//        }

    // 向服务器验证用户名和密码是否正确
    showProgress("提示", "请稍后...");
    DataProviderCenter.getInstance().login(userName, pwd, new HttpDataCallBack<String>() {
      @Override
      public void onError(int errorCode) {
        LogUtils.e("errorCode = " + errorCode);
        closeProgress();
        if (errorCode == ErrorCode.CODE_NO_INTERNET){
          DialogUtils.showShortToast("无网络连接，请先连接网络,或尝试离线登陆。");
        } else if (errorCode == 401){
          DialogUtils.showShortToast("用户名或密码错误！");
        }else {
          DialogUtils.showShortToast("现网络不稳定，请切换其它wifi或4G网络");
        }
      }

      @Override
      public void onComplete(String content) {
        closeProgress();
        try {
          JSONObject jsonObject = new JSONObject(content);

          String token = jsonObject.optString(Constant.TAG_ACCESS_TOKEN, "");
          if (!"".equals(token)){
            BluetoothUtilsApplication.accessToken = token;
            LogUtils.w( "token: " + token);

            //离线
            SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(LoginActivity.this);
            sqLiteHelper.saveObject(SQLiteHelper.USERNAME,getUserName(),getUserName());
            sqLiteHelper.saveObject(SQLiteHelper.PWD,getPwd(),getUserName());


              //进入地图选择界面
              Intent intent = new Intent(LoginActivity.this, MapSelectActivity.class);
              //是否已离线
              intent.putExtra(Constant.TAG_HAS_DOWNLOAD, false);
              LoginActivity.this.startActivityOnAnimation(intent);


          } else {
            DialogUtils.showShortToast("返回数据有误！");
          }
        } catch (JSONException e) {
          e.printStackTrace();
          DialogUtils.showShortToast("返回数据有误！");
        }
      }
    });
  }

  private long mLastTime=0;

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    SharedPreferences setting = getSharedPreferences(Constant.APP_CONFIG, 0);
    Boolean user_first = setting.getBoolean("FIRST",true);
    if(user_first){//第一次
      setting.edit().putBoolean("FIRST", false).commit();
      DialogUtils.showLongToast("首次运行巡检工具，建议删除原来的缓存文件。");
      SQLiteHelper.askAndDeleteAllTable(this, new OnDeleteComplete() {
        @Override
        public void onComplete() {
          DialogUtils.showShortToast("删除完毕");
        }
      });//删除离线的beacon和floorid数据
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK){
      // 如果是返回键，动画结束该activity
      long current = Calendar.getInstance().getTimeInMillis();
      if ((current - mLastTime) / 1000 >= 2){
        DialogUtils.showShortToast("再按一次返回退出");
        mLastTime = current;
        return true;
      } else {
        finish();
        System.exit(0);
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void doLoginOffline(String userName,String pwd){
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
      DialogUtils.showShortToast("抱歉，您的手机不支持低功耗蓝牙，无法巡检");
      return;
    }
    BluetoothUtilsApplication.userName = userName; // 记录用户名，用于后面请求参数

    SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(this);
    String p = sqLiteHelper.getUserPwd(BluetoothUtilsApplication.userName);
    if (pwd.equals(p)){

        //进入地图选择界面
        Intent intent = new Intent(LoginActivity.this, MapSelectActivity.class);
        //是否已离线
        intent.putExtra(Constant.TAG_HAS_DOWNLOAD, true);
        LoginActivity.this.startActivityOnAnimation(intent);

    }else if(p != null){
      DialogUtils.showLongToast("用户名或密码错误，请检查。");
    }else{
      DialogUtils.showLongToast("该用户没有离线数据，请尝试其他用户。");
    }

  }
}
