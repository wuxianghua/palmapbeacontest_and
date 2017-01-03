package com.palmap.BluetoothUtils.http.model;

/**
 * 抽象类，用于返回数据处理
 * Created by Overu on 2014/5/4.
 */
public class DataBridge<T> {

    private T data;

    public DataBridge(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
