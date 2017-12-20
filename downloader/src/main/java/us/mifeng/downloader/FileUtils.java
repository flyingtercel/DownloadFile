package us.mifeng.downloader;

import android.os.Environment;

import java.io.File;

/**
 * Created by 黑夜之火 on 2017/12/19.
 */

public class FileUtils {
    //判断是否安装SDCard
    public static boolean isSdOk(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    //创建一个文件夹，用来存放下载的文件
    public static File getRootFile(){
        File sd = Environment.getExternalStorageDirectory();
        File rootFile = new File(sd,"TEMPFILE");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }
}
