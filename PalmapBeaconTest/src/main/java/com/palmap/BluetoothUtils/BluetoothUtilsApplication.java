package com.palmap.BluetoothUtils;

import android.app.Application;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.palmap.BluetoothUtils.database.SQLiteHelper;
import com.palmap.BluetoothUtils.main.constant.Constant;
import com.palmap.BluetoothUtils.utils.DialogUtils;
import com.palmap.BluetoothUtils.utils.FileUtils;

import okhttp3.OkHttpClient;

/**
 * Created by zhang on 2015/10/12.
 */
public class
BluetoothUtilsApplication extends Application {
  public static BluetoothUtilsApplication instance;
  public static String accessToken = ""; // 用户名和密码正确时，会返回token值，用于后面请求服务器参数
  public static String userName = "";

  @Override
  public void onCreate() {
    super.onCreate();

    instance = this;



    //Bugtags在这里初始化
    BugtagsOptions options = new BugtagsOptions.Builder().
        trackingLocation(true).//是否获取位置，默认 true
        trackingCrashLog(true).//是否收集crash，默认 true
        trackingConsoleLog(true).//是否收集console log，默认 true
        trackingUserSteps(true).//是否收集用户操作步骤，默认 true
        trackingNetworkURLFilter("(.*)").//自定义网络请求跟踪的 url 规则，默认 null
        build();
    Bugtags.start("ac60e2b06e4dc8b16b2afe68bc25e7df", this, Bugtags.BTGInvocationEventNone,options);

    // copy字体文件和lur配置文件
    if (FileUtils.checkoutSDCard()) {
      FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, "font");
      FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, Constant.LUR_NAME);
    } else {
      DialogUtils.showShortToast("未找到SDCard");
    }

    //调试插件
    Stetho.initializeWithDefaults(this);

    new OkHttpClient.Builder()
        .addNetworkInterceptor(new StethoInterceptor())
        .build();

//    Engine engine = Engine.getInstance();
//    engine.startWithLicense("e0e2f83788a340e68f98bc03597809c1", this); // 设置验证license，可以通过开发者平台去查找自己的license

  }

  @Override
  public void onTerminate() {
    SQLiteHelper.closeDatabase();
    super.onTerminate();
  }
}
