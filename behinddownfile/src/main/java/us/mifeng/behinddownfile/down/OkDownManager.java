package us.mifeng.behinddownfile.down;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import us.mifeng.behinddownfile.bean.FileInfo;
import us.mifeng.behinddownfile.db.DBDao;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class OkDownManager {
    private String TAG = "tag";
    private ThreadPoolExecutor pool;
    private FileInfo info;
    private MyThread thread;
    private boolean isDown = false;
    private Context context;
    private RandomAccessFile raf;
    private final DBDao dao;

    public OkDownManager(Context context,ThreadPoolExecutor pool, FileInfo info,boolean isFirstSave) {
        this.pool = pool;
        this.info = info;
        this.context = context;
        dao = new DBDao(context);
        if (isFirstSave){
            dao.insertInfo(info);
        }
    }
    public void start(){
        if (thread == null){
            isDown = true;
            thread = new MyThread();
            pool.execute(thread);
        }else{
            pool.execute(thread);
        }
    }

    public void stop() {
        if (thread!=null){
            isDown = false;
            pool.remove(thread);
            thread = null;
        }
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            downLoadData();
            //httpUrldownLoadData();
        }
    }

    private void httpUrldownLoadData() {
        try {
            URL url = new URL(info.getPath());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            File file = new File(info.getFilePath());
            if (!file.exists()){
                raf = new RandomAccessFile(file,"rwd");
                int contentLength = conn.getContentLength();
                info.setTotalSize(contentLength);
                dao.updateData(info);
            }else{
                if (raf == null){
                    raf = new RandomAccessFile(file,"rwd");
                }
                raf.setLength(info.getDownSize());
                conn.setRequestProperty("Range","bytes="+info.getDownSize()+"-");
                conn.connect();
            }

            if (info.getDownSize()< info.getTotalSize()){
                Log.i(TAG, "downLoadData: 文件已经下载完成");
            }
            Log.i(TAG, "downLoadData: ====3====="+info.getTotalSize());

            InputStream ins = conn.getInputStream();
            int len = 0;
            byte[]by = new byte[1024];
            long endTime = System.currentTimeMillis();
            Log.i(TAG, "httpUrldownLoadData: ======4===========");
            while ((len = ins.read(by))!=-1 && isDown){
                raf.write(by,0,len);
                info.setDownSize(info.getDownSize()+len);
                if (System.currentTimeMillis() - endTime>1000){
                    double dd = info.getDownSize()/(info.getTotalSize()*1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format(dd*100)+"%";
                    Log.i(TAG, "httpUrldownLoadData: ===="+value);
                    dao.updateData(info);
                    Intent intent = new Intent("us.mifeng");
                    intent.putExtra("progress",(int)(dd*100));
                    context.sendBroadcast(intent);
                }
            }

        }catch (Exception e){
            // e.getMessage();
            Log.i(TAG, "downLoadData:===s==="+e.getMessage());
        }


    }
    private void downLoadData() {
        try {
            File file = new File(info.getFilePath());
            if (!file.exists()){
                raf = new RandomAccessFile(file,"rwd");
                long contentLength = getContentLength(info.getPath());
                info.setTotalSize(contentLength);
                dao.updateData(info);
            }else{
                if (raf == null){
                    raf = new RandomAccessFile(file,"rwd");
                }
                raf.setLength(info.getDownSize());
            }
            if (info.getDownSize()< info.getTotalSize()){
                Log.i(TAG, "downLoadData: 文件已经下载完成");
            }
            Log.i(TAG, "downLoadData: =========");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url(info.getPath()).
                    addHeader("Range","bytes="+info.getDownSize()+"-").build();
            Response response = client.newCall(request).execute();
            InputStream ins = response.body().byteStream();
            int len = 0;
            byte[]by = new byte[1024];
            long endTime = System.currentTimeMillis();
            while ((len = ins.read(by))!=-1 && isDown){
                raf.write(by,0,len);
                info.setDownSize(info.getDownSize()+len);
                if (System.currentTimeMillis() - endTime>1000){
                    double dd = info.getDownSize()/(info.getTotalSize()*1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format(dd*100)+"%";
                    Log.i(TAG ,"=====sss========"+value);
                    dao.updateData(info);
                    Intent intent = new Intent("us.mifeng");
                    intent.putExtra("progress",(int)(dd*100));
                    context.sendBroadcast(intent);
                }
            }

        }catch (Exception e){
           // e.getMessage();
            Log.i(TAG, "downLoadData:===s==="+e.getMessage());
        }


    }
    //创建方法，去获取文件大小
    private long getContentLength(String path) throws IOException {
        //Log.i(TAG, "getContentLength: "+path);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(path).build();
        Response response = client.newCall(request).execute();
        long contentLength = response.body().contentLength();
        response.body().close();
        return contentLength;
    }
}
