package us.mifeng.behinddownfile.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class FileUtils {
    public static boolean isSdCard(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return  true;
        }
        return false;
    }
    public static File getRoorFile(){
        if (!isSdCard()){
            return null;
        }
        File root = Environment.getExternalStorageDirectory();
        File rootFile = new File(root.getPath()+"/downloads","mkt");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }
}
