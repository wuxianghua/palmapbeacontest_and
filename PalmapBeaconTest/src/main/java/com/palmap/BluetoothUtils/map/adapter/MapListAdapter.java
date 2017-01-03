package com.palmap.BluetoothUtils.map.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.main.model.PalMap;
import com.palmap.BluetoothUtils.main.model.Scene;
import com.palmap.BluetoothUtils.manager.SceneMapListManager;

import java.util.List;

/**
 * Created by eric3 on 2016/9/26.
 * 支持折叠的场景地图选择列表
 */
public class MapListAdapter extends BaseAdapter {
  public static final long SCENE_IDENTIFIER = -99;


  private Context mContext;
  private List<PalMap> mPalMapList;
  private boolean isFirst;
  private int selectPosition = -1;
  private SceneMapListManager mSceneMapListManager;
  //  private long selectSceneID = -1;
  int sceneBgColor;
  int mapSelectColor;


  public MapListAdapter(Context context, List<PalMap> palMapList, Scene[] s) {
    this.mContext = context;
    sceneBgColor = mContext.getResources().getColor(R.color.blue_light);
    mapSelectColor = mContext.getResources().getColor(R.color.theme_bg_color);
//    Collections.sort(palMapList);
    this.mSceneMapListManager = new SceneMapListManager(palMapList, s);
    this.mPalMapList = mSceneMapListManager.refeshListById(0);
  }

  /**
   * @Author: eric3
   * @Description: 根据id获取palmap
   * @Time 2016/11/30 14:55
   */
  public PalMap getMapByPosition(int position) {
    if (position < mPalMapList.size() && position >= 0)
      return mPalMapList.get(position);
    else
      return null;
  }


  @Override
  public int getCount() {
    if (mPalMapList == null) {
      return 0;
    }
    return mPalMapList.size();
  }


  @Override
  public PalMap getItem(int position) {
    return mPalMapList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_map_list, null);
      viewHolder.mapName = (TextView) convertView.findViewById(R.id.map_list_name);

//      viewHolder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.btn_map);
      convertView.setTag(viewHolder);

    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    // 填内容
    String province = mPalMapList.get(position).getProvinceName();
    if (province != null && !"".equals(province))
      viewHolder.mapName.setText(province + " - " + mPalMapList.get(position).getName());
    else
      viewHolder.mapName.setText(mPalMapList.get(position).getName());

    if (mPalMapList.get(position).getMapId() != SCENE_IDENTIFIER) {

      if (position == selectPosition) {
        convertView.setBackgroundColor(mapSelectColor);
        viewHolder.mapName.setTextColor(Color.WHITE);
      } else {
        convertView.setBackgroundColor(Color.WHITE);
        viewHolder.mapName.setTextColor(Color.BLACK);
      }
    } else {//场景
      convertView.setBackgroundColor(sceneBgColor);
      viewHolder.mapName.setTextColor(Color.BLACK);
    }

    return convertView;

  }

  /**
   * @Author: eric3
   * @Description: 点击场景事件 隐藏、显示场景地图
   * @Time 2016/11/30 11:12
   */
  private void refeshSceneMapById() {
//    selectSceneID = mPalMapList.get(selectPosition).getSceneId();
    mPalMapList = mSceneMapListManager.refeshListById(mPalMapList.get(selectPosition).getSceneId());
    notifyDataSetChanged();
  }

  /**
   * @Author: eric3
   * @Description: 根据关键词搜索场景地图
   * @Time 2016/11/30 11:12
   */
  public void refeshSceneMapByKeyWord(String key) {
    mPalMapList = mSceneMapListManager.refeshListByKeyWords(key);
    notifyDataSetChanged();
  }

  /**
  * @Author: eric3
  * @Description: 点击item的响应事件
  * @Time 2016/12/26 10:24
  */
  public void setSelect(int position) {
    selectPosition = position;
    if (mPalMapList.get(selectPosition).getMapId() == SCENE_IDENTIFIER) {
      //隐藏或显示场景地图
      refeshSceneMapById();
    } else
      notifyDataSetChanged();
  }

  class ViewHolder {

    TextView mapName;

    //    LinearLayout mLinearLayout;
    TextView mEntry;
    TextView mHistory;
    TextView mDownload;

  }
}


