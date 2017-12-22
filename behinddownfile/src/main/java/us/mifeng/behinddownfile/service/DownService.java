package us.mifeng.behinddownfile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import us.mifeng.behinddownfile.down.DownManager;

public class DownService extends Service {
    private static DownManager manager;
    public DownService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.manager = new DownManager(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (manager == null){
            this.manager = new DownManager(getApplicationContext());
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
    //写一个方法，用来去获取Manager对象
    public static DownManager getDownManager(){
        Log.i("tag","============="+manager );
        return manager;
    }
}
