//package com.palmap.BluetoothUtils.database;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//
///**
// * Created by eric on 2016/9/13.
// * 创建和更新数据库
// * 任务：管理离线map数据,将对象序列化为字节流字符串，然后将字节流字符串以TEXT类型存储在数据库中
// */
//public class SQLiteHelper_Map {
//    public static final String DATABASE_NAME = "PalmapDB";
//    public static final String TABLE_NAME_DEFAULT = "map";
//
//    public static final String DATABASE_CREATE_DEFAULT = "create table if not exists mapClasstable" +
//            "(_id integer primary key autoincrement,classtabledata text)";
//
//
//
//    private Context mContext;
//    private volatile SQLiteDatabase mSqLiteDatabase;
//    private String mDatabaseName = DATABASE_NAME;
//    private String mTableName = TABLE_NAME_DEFAULT;
//
//
//
//    private final String TAG = "SQLiteHelper_Map";
//
//    public void createDatabaseTable(String tableName) {
//        this.mTableName = tableName;
//        mSqLiteDatabase = mContext.openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);
//        mSqLiteDatabase.execSQL(getCreatTableSQL(mTableName));
//        mSqLiteDatabase.close();
//    }
//
//    private String getCreatMapTableSQL(String tableName) {
//        return "create table if not exists " +tableName+
//                "(_id integer primary key autoincrement,classtabledata text)";
//    }
//    private String getInsertMapTableSQL(String tableName) {
//        return "insert into "+tableName+" (classtabledata) values(?)";
//    }
//
//    /**
//     * 保存离线map
//     * @param
//     */
//    public void saveObject(MapDataSerializable planarGraphSerializable) {
//        if (mSqLiteDatabase==null || !mSqLiteDatabase.isOpen()){
//            mSqLiteDatabase = mContext.openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);
//        }
//
//        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
//            objectOutputStream.writeObject(planarGraphSerializable);
//            objectOutputStream.flush();
//            byte data[] = arrayOutputStream.toByteArray();
//            objectOutputStream.close();
//            arrayOutputStream.close();
//
//
//            mSqLiteDatabase.execSQL(getInsertTableSQL(mTableName), new Object[] { data });
//            mSqLiteDatabase.close();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 获取离线map
//     * @param
//     */
//    public MapDataSerializable getObject() {
//        if (mSqLiteDatabase==null || !mSqLiteDatabase.isOpen()){
//            mSqLiteDatabase = mContext.openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);
//        }
//
//        MapDataSerializable planarGraphSerializable = null;
//
//        Cursor cursor = mSqLiteDatabase.rawQuery("select * from classtable", null);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                byte data[] = cursor.getBlob(cursor.getColumnIndex(classtabledata));
//                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
//                try {
//                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
//                    planarGraphSerializable = (MapDataSerializable) inputStream.readObject();
//                    inputStream.close();
//                    arrayInputStream.close();
//                    break;//这里为了测试就取一个数据
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        return planarGraphSerializable;
//
//    }
//
//    /*
//    判断表名是否存在
//     */
//    public boolean isTableNameExist(String tableName){
//        if (mSqLiteDatabase==null || !mSqLiteDatabase.isOpen()){
//            mSqLiteDatabase = mContext.openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);
//        }
//        Cursor cursor = mSqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
//        while(cursor.moveToNext()){
//            //遍历出表名
//            String name = cursor.getString(0);
//            if (tableName.equals(name)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//}
