package us.mifeng.downloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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
    //显示进度条
    @Override
    public void onProgress(int progress) {
        pBar.setProgress(progress);
    }
}
