package us.mifeng.downloader;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 黑夜之火 on 2017/12/19.
 */

public class DownLoad {
    //下载地址
    private String path;
    //判断是否下载
    private boolean isDown = false;
    //文件下载了多少
    private long downSize = 0;
    //创建根目录对象
    private File rootFile;
    //文件的名字
    private String name;
    private String TAG = "tag";
    //文件对象
    private File file;
    //随机读取流
    private RandomAccessFile raf;
    //创建线程池对象
    private final ThreadPoolExecutor pool;
    //线程声明
    private MyThread myThread;
    //文件的总长度
    private long totalLength;
    private Handler handler = new Handler();
    private IProgress progress;
    public DownLoad(String path,IProgress progress) {
        this.path = path;
        this.progress = progress;
        //根据路径获取文件的名字
        name = path.substring(path.lastIndexOf("/") + 1);
        //创建线程池
        pool = new ThreadPoolExecutor(5,5,50, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(2000));
    }

    class MyThread extends Thread {

        @Override
        public void run() {
            downLoad();
        }
    }

    public void downLoad() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //如果是第一次，则创建文件对象，并且获取文件的长度
            if (downSize < 1) {
                rootFile = FileUtils.getRootFile();
                file = new File(rootFile, name);
                //创建文件的读取流对象
                raf = new RandomAccessFile(file, "rwd");
                totalLength = conn.getContentLength();
            } else if (downSize == totalLength) {
                Log.i(TAG, "downLoad: 已经下载完成");
            } else {
                //设置文件  写入的位置
                raf.seek(downSize);
                //设置文件请求的位置
                conn.setRequestProperty("Range", "bytes=" + downSize + "-");
                //打开链接
                conn.connect();
            }
            //下面是对文件的写入操作
            InputStream stream = conn.getInputStream();
            byte[] by = new byte[1024];
            int len = 0;

            long endTime = System.currentTimeMillis();
            while ((len = stream.read(by)) != -1 && isDown) {
                raf.write(by, 0, len);
                downSize += len;
                final double dd = downSize / (totalLength * 1.0);
                if (System.currentTimeMillis() - endTime > 1000) {
                    /**
                     *  数字格式设置
                     *  #：代表当数字存在时显示，当数字不存在就不显示
                     *  0：代表着数字没有是用0代替。
                     */
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format(dd*100);
                    Log.i(TAG, "downLoad: " + value);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.onProgress((int) (dd*100));
                        }
                    });
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (conn!=null){
                conn.disconnect();
            }
        }

    }


    //控制开始与下载
    public void start() {
        isDown = true;
        if (myThread == null){
            myThread = new MyThread();
            pool.execute(myThread);
        }else{
            pool.execute(myThread);
        }

    }
    //控制暂停下载
    public void stop() {
        isDown = false;
        if (myThread!=null){
            pool.remove(myThread);
            myThread = null;
        }
    }
    public interface IProgress{
        void onProgress(int progress);
    }
}
