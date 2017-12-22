package us.mifeng.behinddownfile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import us.mifeng.behinddownfile.bean.FileInfo;

/**
 * Created by 黑夜之火 on 2017/12/22.
 */

public class DBDao {

    private final DBHelper helper;
    private SQLiteDatabase db;

    public DBDao(Context context) {
        helper = DBHelper.getHelper(context);
    }

    public void insertInfo(FileInfo info) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLInfo.FILEID, info.getFileId());
        values.put(SQLInfo.NAME, info.getName());
        values.put(SQLInfo.PATH, info.getPath());
        values.put(SQLInfo.FILEPATH, info.getFilePath());
        values.put(SQLInfo.DOWNSIZE, info.getDownSize());
        values.put(SQLInfo.TOTALSIZE, info.getTotalSize());
        db.insert(SQLInfo.TABLENAME, null, values);
        db.close();
    }

    public FileInfo queryData(FileInfo info) {
        synchronized (this){
            db = helper.getReadableDatabase();
            FileInfo result = null;
            Cursor cursor = db.query(SQLInfo.TABLENAME, null,
                    SQLInfo.FILEID + " =? and "
                            + SQLInfo.PATH +" = ? ", new String[]{info.getFileId(), info.getPath()}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String fileId = cursor.getString(cursor.getColumnIndex(SQLInfo.FILEID));
                    String name = cursor.getString(cursor.getColumnIndex(SQLInfo.NAME));
                    String path = cursor.getString(cursor.getColumnIndex(SQLInfo.PATH));
                    String filePath = cursor.getString(cursor.getColumnIndex(SQLInfo.FILEPATH));
                    long totalSize = cursor.getLong(cursor.getColumnIndex(SQLInfo.TOTALSIZE));
                    long downSize = cursor.getLong(cursor.getColumnIndex(SQLInfo.DOWNSIZE));
                    result = new FileInfo(totalSize, downSize, name, path, filePath, fileId);
                }
            }
            db.close();
            return result;
        }



    }
    public void updateData(FileInfo info){
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLInfo.DOWNSIZE,info.getDownSize());
        values.put(SQLInfo.TOTALSIZE,info.getTotalSize());
        db.update(SQLInfo.TABLENAME,values,SQLInfo.FILEID + " = ? and "
                + SQLInfo.PATH + " = ?", new String[]{info.getFileId(), info.getPath()});
        db.close();
    }

}
