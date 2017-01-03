package com.palmap.BluetoothUtils.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;
import com.palmap.BluetoothUtils.main.model.Beacon;
import com.palmaplus.nagrand.view.overlay.OverlayCell;

/**
 * Created by zhang on 2015/11/25.
 */
public class Mark extends LinearLayout implements OverlayCell{

  private TextView mTextView;
  private ImageView mIconView;
  private LinearLayout mLinearLayout;

  private int beaconId;
  private int minor;//beacon的minor
  private int major;//beacon的major
  private String uuid;//beacon的uuid
  private boolean mIsScaned;

  private double[] mGeoCoordinate;

  private OnClickListenerForMark onClickListenerForMark;

  public Mark(Context context, OnClickListenerForMark onClickListenerForMark) {

    super(context);
    this.onClickListenerForMark = onClickListenerForMark;

    initView();
    mIsScaned = false;
  }

  private void initView() {
    View root =  LayoutInflater.from(getContext()).inflate(R.layout.item_mark, this);
//    root.setOnClickListener(this);
    mIconView = (ImageView) findViewById(R.id.mark_icon);
    mTextView = (TextView) findViewById(R.id.mark_text);
    mLinearLayout = (LinearLayout) findViewById(R.id.mark);
    mLinearLayout.setEnabled(true);
    mLinearLayout.setClickable(true);
    mLinearLayout.setFocusable(true);

    //OnClickListener放在mark上不起作用，
    // 怀疑mapview吃掉了触摸事件，
    // 所以放到mLinearLayout上
    mLinearLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onClickListenerForMark == null){
          return;
        }
        onClickListenerForMark.onMarkSelect(Mark.this);
      }
    });

  }

  /*
  * 设置mark文本
  * */
  public void setText(Beacon beacon){
    minor = beacon.getMinor();
    mTextView.setText(minor+"");
    major = beacon.getMajor();
    uuid = beacon.getUuid();
  }

  /*
  * 获取mark文本
  * */
  public String getText(){
    return mTextView.getText().toString();
  }

  /*
  * 设置mark图标
  * */
  public void setIcon(int resId){
    mIconView.setBackgroundResource(resId);
  }

  /**
   * @param isSelected
   */
  public void setScanedColor(boolean isSelected){
    if (isSelected){
      mIconView.setImageResource(R.drawable.dot_green_small);

      mIsScaned = true;

    } else {
      mIconView.setImageResource(R.drawable.dot_red_small);
      mIsScaned = false;
    }
  }

  public int getMinor() {
    return minor;
  }

  public int getMajor() {
    return major;
  }

  public String getUuid() {
    return uuid;
  }

  @Override
  public void init(double[] doubles) {
    mGeoCoordinate = doubles;
  }

  @Override
  public double[] getGeoCoordinate() {
    return mGeoCoordinate;
  }

  @Override
  public void position(double[] doubles) {
    setX((float) doubles[0] - getWidth() / 2);
    setY((float) doubles[1] - getHeight() / 9 * 4);
  }



  public interface OnClickListenerForMark{
    void onMarkSelect(Mark mark);
  }

  public boolean isScaned(){
    return mIsScaned;
  }

  public int getBeaconId(){ return beaconId;}
  public void setBeaconIdId(int beaconId){ this.beaconId = beaconId;}

  public void setBeaconInfo(Beacon b){ this.beaconId = b.getId();
  setScanedColor(b.isScaned());}

}
