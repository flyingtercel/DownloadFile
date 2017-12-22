package us.mifeng.behinddownfile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class DBHelper extends SQLiteOpenHelper {
    private String TAG = "tag";
    public static DBHelper dbHeler;
    private DBHelper(Context context) {
        super(context, SQLInfo.DBNAME, null, SQLInfo.VERSION);
    }
    //定义方法，用来直接获取DbHelper对象
    public static DBHelper getHelper(Context context){
        if (dbHeler == null){
            dbHeler = new DBHelper(context);
        }
        return dbHeler;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+
                SQLInfo.TABLENAME+"("+SQLInfo.FILEID+ " integer primary key, "+
                SQLInfo.NAME+" text not null ,"+
                SQLInfo.PATH+" text not null ,"+
                SQLInfo.FILEPATH+" text not null ,"+
                SQLInfo.TOTALSIZE+" long not null ,"+
                SQLInfo.DOWNSIZE+" long not null )";
        Log.i(TAG, "onCreate: ====="+sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
