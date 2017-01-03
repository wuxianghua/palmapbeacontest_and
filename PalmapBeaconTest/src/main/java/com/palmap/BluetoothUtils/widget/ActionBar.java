package com.palmap.BluetoothUtils.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmap.BluetoothUtils.R;

/**
 * Created by zhang on 2015/9/10.
 */
public class ActionBar extends RelativeLayout implements View.OnClickListener{

  private RelativeLayout mContainer;
  private ImageButton mLeftBtn;
  private TextView mRightBtn;
  private EditText mSearchBox;
  private TextView mTitle;

  private OnActionBarListener onActionBarListener;

  public ActionBar(Context context) {
    super(context);

    initView(context);
  }

  public ActionBar(Context context, AttributeSet attrs) {
    super(context, attrs);

    initView(context);
  }

  public boolean isSearching(){
    if (mSearchBox==null)
    return false;

    return mSearchBox.getVisibility()==View.VISIBLE;
  }
  /*
  * 初始化actionBar上的控件对象
  * */
  private void initView(Context context){
    LayoutInflater.from(context).inflate(R.layout.widget_action_bar, this);
    mContainer = (RelativeLayout) findViewById(R.id.action_bar_container);
    mLeftBtn = (ImageButton) findViewById(R.id.btn_action_bar_left);
    mRightBtn = (TextView) findViewById(R.id.btn_action_bar_right);
    mTitle = (TextView) findViewById(R.id.action_bar_title);
    mSearchBox = (EditText) findViewById(R.id.action_bar_search_box);
  }

  /*
  *  设置title
  * */
  public void setTitle(String title){
    mSearchBox.setVisibility(GONE);
    mTitle.setVisibility(VISIBLE);
    mTitle.setText(title);
  }

  /*
  *  设置actionBox
  * */
  public void showSearchBox(TextWatcher textWatcher){
      mSearchBox.setVisibility(VISIBLE);
      mTitle.setVisibility(GONE);
      setRightText("取消");
      mSearchBox.addTextChangedListener(textWatcher);
  }
  /*
  *  设置actionBox
  * */
  public void hideSearchBox(){

      mSearchBox.setText("");
      setRightText("搜索");
      mTitle.setVisibility(VISIBLE);
      mSearchBox.setVisibility(GONE);
  }

  /*
    *  获取searchBox内容
    * */
  public String getSearchContent(){
    if (mSearchBox != null){
      return mSearchBox.getText().toString();
    }

    return null;
  }

  /*
  *  获取searchBox对象
  * */
  public EditText getSearchBox(){
    return mSearchBox;
  }

  /*
  *  设置左键可见
  * */
  public void setLeftVisible(boolean isVisible){
    if(isVisible){
      mLeftBtn.setVisibility(VISIBLE);
      mLeftBtn.setOnClickListener(this);
    } else{
      mLeftBtn.setVisibility(GONE);
    }
  }

  /*
  * 设置左侧控件
  * */
  public void setLeftBg(int resId){
    mLeftBtn.setBackgroundResource(resId);
    mLeftBtn.setOnClickListener(this);
    mLeftBtn.setVisibility(VISIBLE);
  }

  /*
  * 设置右侧控件
  * */
  public void setRightBg(int resId){
    mRightBtn.setBackgroundResource(resId);
    mRightBtn.setOnClickListener(this);
    mRightBtn.setVisibility(VISIBLE);
  }

  /*
  * 设置右侧控件
  * */
  public void setRightBg(String text, int resId){
    setRightText(text);
    if (resId != 0){
      mRightBtn.setBackgroundResource(resId);
    }
  }
  /*
 * 设置右侧控件
 * */
  public void setRightText(String text){
    mRightBtn.setText(text);
    mRightBtn.setOnClickListener(this);
    mRightBtn.setVisibility(VISIBLE);
  }

  /*
  * 获取右侧文本
  * */
  public String getRightText(){
    return mRightBtn.getText().toString();
  }

  @Override
  public void onClick(View v) {
    if (onActionBarListener != null){
      switch (v.getId()){
        case R.id.btn_action_bar_left:
          onActionBarListener.onLeft();
          break;
        case R.id.btn_action_bar_right:
          onActionBarListener.onRight();
          break;
      }
    }
  }

  public void setOnActionBarListener(OnActionBarListener onActionBarListener){
    this.onActionBarListener = onActionBarListener;
  }

  public TextView getTileText(){
    return mTitle;
  }

  /*
  *  actionBar监听事件类
  * */
  public interface OnActionBarListener{
    void onLeft();
    void onRight();
  }

}
