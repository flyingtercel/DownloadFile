# DownloadFile
使用HttpURLConnection实现断点续传
![断点续传](https://github.com/flyingtercel/DownloadFile/blob/master/downloader/src/main/res/mipmap-hdpi/ss.png)
什么是断点续传？
断点续传其实正如字面意思，就是在下载的断开点继续开始传输，不用再从头开始。所以理解断点续传的核心后，发现其实和很简单，关键就在于对传输中断点的把握
关键点：

对于断点续传，关键点是两个：
1. 终端知道当前的文件和上一次加载的文件是不是内容发生了变化，如果有变化，需要重新从offset 0 的位置开始下载
2. 终端记录好上次成功下载到的offset，告诉server端,server端支持从特定的offset 开始吐数据
原理：
断点续传的关键是断点，所以在制定传输协议的时候要设计好，如上图，我自定义了一个交互协议，每次下载请求都会带上下载的起始点，这样就可以支持从断点下载了，
其实HTTP里的断点续传也是这个原理，在HTTP的头里有个可选的字段RANGE，表示下载的范围。</br>
HTTP头Range字段：</br>
Range : 用于客户端到服务器端的请求，可通过该字段指定下载文件的某一段大小，及其单位。典型的格式如：</br>
 Range: bytes=0-499 下载第0-499字节范围的内容 </br>
 Range: bytes=500-999 下载第500-999字节范围的内容 </br>
 Range: bytes=-500 下载最后500字节的内容 </br>
 Range: bytes=500- 下载从第500字节开始到文件结束部分的内容</br>
```
DownLoad下载代码：</br>
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
        //myThread = new MyThread();
        //执行线程
        //pool.execute(myThread);
    }
    ```
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
    
    
    控制开始与下载
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


FileUtils文件夹创建
public class FileUtils {
    //判断是否安装SDCard
    public static boolean isSdOk(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    创建一个文件夹，用来存放下载的文件
    public static File getRootFile(){
        File sd = Environment.getExternalStorageDirectory();
        File rootFile = new File(sd,"TEMPFILE");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }
    
    
    MainActivity中点击Button按钮下载显示进度条
    public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownLoad.IProgress {
    private String path = "http://video.dameiketang.com/mkt2016%2F%E9%83%91%E7%82%9C%E4%B8%9C%2F%E5%A4%B4%E7%9A%AE%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%981.mp4";
    private DownLoad downLoad;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downLoad = new DownLoad(path,this);
        initView();
    }

    private void initView() {
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        pBar = (ProgressBar) findViewById(R.id.progress);
        pBar.setMax(100);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                downLoad.start();
                break;
            case R.id.stop:
                downLoad.stop();
                break;
        }
    }
    显示进度条
    @Override
    public void onProgress(int progress) {
        pBar.setProgress(progress);
    }
}
