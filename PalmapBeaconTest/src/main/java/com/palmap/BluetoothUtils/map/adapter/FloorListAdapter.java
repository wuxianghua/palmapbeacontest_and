package com.palmap.BluetoothUtils.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.main.model.Floor;

import java.util.List;

/**
 * Created by zhang on 2015/9/14.
 */
public class FloorListAdapter extends BaseAdapter {

  private Context mContext;
  private List<Floor> mFloorList;

  public FloorListAdapter(Context context, List<Floor> floorList){
    this.mContext = context;
    this.mFloorList = floorList;
  }

  @Override
  public int getCount() {
    if (mFloorList == null){
      return 0;
    }
    return mFloorList.size();
  }

  @Override
  public Floor getItem(int position) {
    return mFloorList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null&&mContext!=null){
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_floor_list, null);
      viewHolder.floorName = (TextView) convertView.findViewById(R.id.map_floor_list_item);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    // 设置控件内容
    viewHolder.floorName.setText(mFloorList.get(position).getAlias());

    return convertView;
  }

  class ViewHolder{
    TextView floorName;
  }

}
