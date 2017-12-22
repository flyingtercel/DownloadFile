package us.mifeng.behinddownfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import us.mifeng.behinddownfile.down.DownManager;
import us.mifeng.behinddownfile.service.DownService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownManager manager;
    private String path = "http://video.dameiketang.com/mkt2016%2F%E9%83%91%E7%82%9C%E4%B8%9C%2F%E5%A4%B4%E7%9A%AE%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%981.mp4";
    private String name = path.substring(path.lastIndexOf("/")+1);
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        manager = DownService.getDownManager();
        IntentFilter filter = new IntentFilter("us.mifeng");
        registerReceiver(new MyRecive(),filter);
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
        if (manager == null){
            manager = DownService.getDownManager();
        }
        switch (v.getId()){
            case R.id.start:
                manager.addTask("11",name,path);
                break;
            case R.id.stop:
                if (manager!=null){
                    manager.stop();
                }
                break;
        }
    }
    class MyRecive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            Log.i("tag","============="+progress);
            pBar.setProgress(progress);
        }
    }
}
