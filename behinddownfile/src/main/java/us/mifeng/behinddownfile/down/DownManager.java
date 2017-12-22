package us.mifeng.behinddownfile.down;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import us.mifeng.behinddownfile.bean.FileInfo;
import us.mifeng.behinddownfile.db.DBDao;
import us.mifeng.behinddownfile.file.FileUtils;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class DownManager {
    private File rootFile;
    private final ThreadPoolExecutor pool;
    private Context context;
    private final DBDao dbDao;
    private OkDownManager manager;
    private Handler handler;

    public DownManager(Context context) {
        dbDao = new DBDao(context);
        this.context = context;
        rootFile = FileUtils.getRoorFile();
        pool = new ThreadPoolExecutor(
                5,5,50, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000));
    }
    public void addTask(final String fileId, final String name, final String path){

        File file = new File(rootFile,name);
        FileInfo info = new FileInfo(0,0,name,path,file.getPath(),fileId);
        boolean isFirstSave = true;
        if (dbDao.queryData(info) != null){
            info = dbDao.queryData(info);
            isFirstSave = false;
        }
        manager = new OkDownManager(context,pool,info,isFirstSave);
        manager.start();
    }
    public void stop(){
        if (manager!=null){
            manager.stop();
        }
    }
}
