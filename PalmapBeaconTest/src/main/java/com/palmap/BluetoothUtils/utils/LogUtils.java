package com.palmap.BluetoothUtils.utils;

import android.util.Log;

/**
 * Created by eric3 on 2016/11/21.
 */

public class LogUtils {
  private static final boolean openLog = true;
  private static final boolean showLine = false;


  private static String getCallName(){
    StackTraceElement stack[] = Thread.currentThread().getStackTrace();

    if (stack==null)
      return "LogUtil get stack faild!";

    if (stack.length<5)
      return "LogUtil get stack error!";

    String className = stack[4].getClassName();
    if (className==null)
      return "LogUtil get class name faild!";

    char cn[] = className.toCharArray();
    int i=cn.length-1;
    for (;i>-1;i--){
      if (cn[i]=='.')
        break;
    }
    if (className.length()<i+2)
      return "LogUtil get class name error!";

    String callName=className.substring(i+1)+"->"+stack[4].getMethodName();
    if (showLine)
      callName+=" line "+stack[4].getLineNumber();

    return callName;
  }

  public static void i(String msg){
    if (openLog)
    Log.i(getCallName(),msg);
  }
  public static void d(String msg){
    if (openLog)
    Log.d(getCallName(),msg);
  }
  public static void e(String msg){
    if (openLog)
    Log.e(getCallName(),msg);
  }
  public static void w(String msg){
    if (openLog)
    Log.w(getCallName(),msg);
  }
}
