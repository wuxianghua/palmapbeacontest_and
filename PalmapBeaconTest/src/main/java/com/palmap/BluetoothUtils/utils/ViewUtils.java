package com.palmap.BluetoothUtils.utils;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by zhang on 2015/9/14.
 */
public class ViewUtils {

  /*
  *  根据item项设置listView宽高
  *  @param isMeasuredWidth 是否测量width
  * */
  public static void setListViewBasedOnItem(ListView listView, int maxLineNum, boolean isMeasuredWidth){
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null){
      return;
    }

    int totalHeight = 0;
    int maxWidth = 0;
//    int lineNum = maxLineNum > 0 ? maxLineNum : 1; // 至少为1

    View listItem = null;
    LogUtils.e("listAdapter.getCount() = " + listAdapter.getCount());
    for (int i = 0; i < maxLineNum && i < listAdapter.getCount(); i++){
      listItem = listAdapter.getView(i, null, listView);
      listItem.measure(0, 0);
      totalHeight += listItem.getMeasuredHeight();
      int width = listItem.getMeasuredWidth();
      if (width > maxWidth) maxWidth = width;
    }

//    totalHeight += listView.getDividerHeight() * (lineNum - 1); // 加上分割线高度
    totalHeight += listView.getDividerHeight() * maxLineNum; // 加上分割线高度

    ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
    layoutParams.height = totalHeight;
    if (isMeasuredWidth){
      layoutParams.width = maxWidth;
    }
    listView.setLayoutParams(layoutParams);

  }

  /*
  *  测量view的宽高
  * */
  public static Point measureView(View view){
    if (view == null){
      return null;
    }

    Point point = new Point();
    int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    int h =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    view.measure(w,h);
    point.x = view.getMeasuredWidth();
    point.y = view.getMeasuredHeight();

    return point;
  }

}
