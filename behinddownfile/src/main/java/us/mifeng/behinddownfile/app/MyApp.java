package us.mifeng.behinddownfile.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import us.mifeng.behinddownfile.service.DownService;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tag","=======service runing=========");
        //启动服务
        Intent intent = new Intent(this, DownService.class);
        startService(intent);
    }
}
