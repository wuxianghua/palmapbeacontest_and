package com.palmap.BluetoothUtils.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by zhang on 2015/4/2.
 */
public class ResourceUtils {

  /**
   * 获取字符串
   * @param resId
   * @return
   */
  public static String getStringByResId(Context context, int resId){
    return context.getResources().getString(resId);
  }

  /**
   * 获取color
   * @param resId
   * @return
   */
  public static int getColorByResId(Context context, int resId){
    return context.getResources().getColor(resId);
  }

  /**
   * 获取dimen
   * @param resId
   * @return
   */
  public static float getDimenByResId(Context context, int resId){
    return context.getResources().getDimension(resId);
  }

  /**
   * 获取drawable
   * @param resId
   * @return
   */
  public static Drawable getDrawableByResId(Context context, int resId){
    return context.getResources().getDrawable(resId);
  }

}
