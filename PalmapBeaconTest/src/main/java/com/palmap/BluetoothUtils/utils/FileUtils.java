package com.palmap.BluetoothUtils.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhang on 2015/4/29.
 */
public class FileUtils {
  private static final String DIR_NAME = "palmap";

  /*
  *  检测SDCard是否存在
  * */
  public static boolean checkoutSDCard() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /*
  *  将asserts下一个指定文件夹中所有文件copy到SDCard中
  * */
  public static void copyDirToSDCardFromAsserts(Context context, String dirName, String dirName2) {
    try {
      AssetManager assetManager = context.getAssets();
      String[] fileList = assetManager.list(dirName2);
      outputStr(dirName2, fileList); // 输出dirName2中文件名
      String dir = Environment.getExternalStorageDirectory() + File.separator + dirName;

      if (fileList != null && fileList.length > 0) {
        File file = null;

        // 创建文件夹
        file = new File(dir);
        if (!file.exists()) {
          file.mkdirs();
        } else {
          LogUtils.w(dir + "已存在.");
          deleteDirectory(dir);
          file.mkdirs();
        }

        // 创建文件
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        int len = -1;
        for (int i = 0; i < fileList.length; i++) {
          file = new File(dir, fileList[i]);
          if (!file.exists()) {
            file.createNewFile();
          }
          inputStream = assetManager.open(dirName2 + File.separator + fileList[i]);
          outputStream = new FileOutputStream(file);
          while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
          }
          outputStream.flush();
        }

        // 关流
        if (inputStream != null) {
          inputStream.close();
        }
        if (outputStream != null) {
          outputStream.close();
        }
      }
    } catch (IOException e) {
      LogUtils.e( "IOException e");
      e.printStackTrace();
    }
  }

  /*
  * 输出String[]中内容
  * 作用：输出文件夹中文件名
  * */
  public static void outputStr(String dirName, String[] listStr) {
    if (listStr != null) {
      if (listStr.length <= 0) {
        LogUtils.w( dirName + "文件为空");
      } else {
        LogUtils.w( dirName + "文件中有以下文件：");
        for (String str : listStr) {
          LogUtils.w( str);
        }
      }
    }
  }

  /*
  * 导出指定名称的数据库文件
  * */
  public static void outputDBFile(Context context, String dbName, String fileName){

    if (!checkoutSDCard()){
      LogUtils.w("SDCard不存在");
      return;
    }

    File file = context.getDatabasePath(dbName);
    LogUtils.w( "file path: " + file.getAbsolutePath());
    LogUtils.w( "file name: " + file.getName());
    if (file.exists() && file.isFile()){
      String dirName = Environment.getExternalStorageDirectory().getPath() + File.separator + DIR_NAME;
      File dirFile = new File(dirName);
      if (!dirFile.exists()){
        dirFile.mkdirs();
      }

      if (fileName == null){
        fileName = file.getName();
      }
      File outputFile = new File(dirFile, fileName + ".db");
      if (outputFile.exists()){
        outputFile.delete();
      }

      FileOutputStream fos = null;
      FileInputStream fis = null;
      try {
        outputFile.createNewFile();
        fos = new FileOutputStream(outputFile);
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = fis.read(buffer)) > 0){
          fos.write(buffer, 0,length);
        }
        fos.flush();
        LogUtils.w( "outputFile path: " + outputFile.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (fis != null){
            fis.close();
          }
          if (fos != null){
            fos.close();
          }
        }catch (Exception e){
          e.printStackTrace();
        }
      }

    } else {
      LogUtils.w("数据库文件不存在");
    }
  }
  /**
   * 删除单个文件
   * @param   filePath    被删除文件的文件名
   * @return 文件删除成功返回true，否则返回false
   */
  public static boolean deleteFile(String filePath) {
    File file = new File(filePath);
    if (file.isFile() && file.exists()) {
      return file.delete();
    }
    return false;
  }

  /**
   * 删除文件夹以及目录下的文件
   * @param   filePath 被删除目录的文件路径
   * @return  目录删除成功返回true，否则返回false
   */
  public static boolean deleteDirectory(String filePath) {
    boolean flag = false;
    //如果filePath不以文件分隔符结尾，自动添加文件分隔符
    if (!filePath.endsWith(File.separator)) {
      filePath = filePath + File.separator;
    }
    File dirFile = new File(filePath);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      return false;
    }
    flag = true;
    File[] files = dirFile.listFiles();
    //遍历删除文件夹下的所有文件(包括子目录)
    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        //删除子文件
        flag = deleteFile(files[i].getAbsolutePath());
        if (!flag) break;
      } else {
        //删除子目录
        flag = deleteDirectory(files[i].getAbsolutePath());
        if (!flag) break;
      }
    }
    if (!flag) return false;
    //删除当前空目录
    return dirFile.delete();
  }

  /**
   *  根据路径删除指定的目录或文件，无论存在与否
   *@param filePath  要删除的目录或文件
   *@return 删除成功返回 true，否则返回 false。
   */
  public static boolean DeleteFolder(String filePath) {
    File file = new File(filePath);
    if (!file.exists()) {
      return true;//文件不存在，返回删除成功
    } else {
      if (file.isFile()) {
        // 为文件时调用删除文件方法
        return deleteFile(filePath);
      } else {
        // 为目录时调用删除目录方法
        return deleteDirectory(filePath);
      }
    }
  }
}
