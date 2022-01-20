package com.example.lenovo.Android_LIFE_Journal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lenovo.Android_LIFE_Journal.model.NoteInfo;

import java.util.ArrayList;
import java.util.List;

/*
實現對數據列表的CRUD
 */
public class DBdataSource {

    private static final String TAG="DBdataSource";
    private SQLiteDatabase mDatabase;
    private MyDBhelper myDBHelper;
    private Context mContext;

    public DBdataSource(Context context)
    {
        mContext=context;
        myDBHelper=new MyDBhelper(context);
    }

    //打開一個可改寫的數據庫,注意要在主 Activity 的 onResume 方法裡調用
    public void open(){
        Log.d(TAG, "database created and opened.");
        mDatabase=myDBHelper.getWritableDatabase();
    }
    //關閉一個數據庫，注意要在主 Activity 的 onPause 方法裡調用
    public void close(){
        Log.d(TAG, "database closed.");
        mDatabase.close();
    }


    public void deleteById(int id){
        mDatabase.delete(DBSchema.DataClassTable.TABLE_NAME,"_ID=?",new String[]{""+id});
    }

    /*
    插入一條紀錄
     */
    public long insert(NoteInfo obj){
        if(obj==null){
            return -1;
        }
        ContentValues values=new ContentValues();
        values.put(DBSchema.DataClassTable.COLUMN_NAME,obj.getName());
        values.put(DBSchema.DataClassTable.COLUMN_TIME,obj.getDate());
        values.put(DBSchema.DataClassTable.COLUMN_CONTENT,obj.getContent());
        values.put(DBSchema.DataClassTable.COLUMN_TYPE,obj.getType());
        long resultID=mDatabase.insert(DBSchema.DataClassTable.TABLE_NAME,null,values);

        return resultID;
    }

    /*
    提取數據中的所有數據對象
     */
    public List<NoteInfo> selectAll(){
        List<NoteInfo> objs=null;
        Cursor cursor=mDatabase.query(
                DBSchema.DataClassTable.TABLE_NAME,   //表名
                new String[]{               //要提取的字段名
                        DBSchema.DataClassTable.COLUMN_ID,
                        DBSchema.DataClassTable.COLUMN_NAME,
                        DBSchema.DataClassTable.COLUMN_TIME,
                        DBSchema.DataClassTable.COLUMN_TYPE,
                        DBSchema.DataClassTable.COLUMN_CONTENT},
                null,   //where
                null,   //where params
                null,   //groupby
                null,   //having
                null    //orderby
        );
        objs=cursorToList(cursor);
        return objs;
    }

    //將 Cursor 所引用的所有數據全部讀取出來
    private List<NoteInfo> cursorToList(Cursor cursor){
        if(cursor==null){
            return null;
        }
        List<NoteInfo> objs=new ArrayList<>();
        NoteInfo obj=null;
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                obj=readFromCursor(cursor);
                if(obj!=null){
                    objs.add(obj);
                }
                cursor.moveToNext();
            }
        }
        return  objs;
    }

    //從當前 Cursor 中讀取數據，創建一個 NoteInfo 對象
    private NoteInfo readFromCursor(Cursor cursor){
        if(cursor==null){
            return null;
        }
        NoteInfo obj=new NoteInfo();
        obj.setId((int)(cursor.getLong(cursor.getColumnIndex(DBSchema.DataClassTable.COLUMN_ID))));
        obj.setContent((cursor.getString(cursor.getColumnIndex(DBSchema.DataClassTable.COLUMN_CONTENT))));
        obj.setDate((cursor.getString(cursor.getColumnIndex(DBSchema.DataClassTable.COLUMN_TIME))));
        obj.setName((cursor.getString(cursor.getColumnIndex(DBSchema.DataClassTable.COLUMN_NAME))));
        obj.setType((cursor.getString(cursor.getColumnIndex(DBSchema.DataClassTable.COLUMN_TYPE))));
        return obj;
    }

    /*
    檢查名稱是否重複
     */
    public boolean findByName(String name){
        Cursor cursor=mDatabase.query(DBSchema.DataClassTable.TABLE_NAME,
                    new String[]{DBSchema.DataClassTable.COLUMN_NAME},
                "name=?",
                new String[]{name},
                null,
                null,
                null,
                null
                );
        if(cursor.moveToFirst()==false)
            return false;
        else return true;
    }

/*
     通過筆記的 ID 來更新筆記的樣式
 */
    public void updateType(int noteId,String type){
        ContentValues values = new ContentValues();
        values.put("type",type);
        mDatabase.update(DBSchema.DataClassTable.TABLE_NAME,values,
                "_ID=?",new String[]{""+noteId});
    }
    /*
   通過筆記的 ID 來更新筆記的名稱
    */
    public void updateName(int noteId,String newName){
        ContentValues values = new ContentValues();
        values.put("name",newName);
        mDatabase.update(DBSchema.DataClassTable.TABLE_NAME,values,
                "_ID=?",new String[]{""+noteId});
    }

    /*
    更新 ID 為 noteID 的筆記內容
     */
    public void updateContent(int noteId,String newContent){
        ContentValues values = new ContentValues();
        values.put("content",newContent);
        mDatabase.update(DBSchema.DataClassTable.TABLE_NAME,values,
                "_ID=?",new String[]{""+noteId});
    }

    /*
    通過名稱返回一個 NoteInfo 對象
     */
    public NoteInfo getByName(String InfoName){
        Cursor cursor=mDatabase.query(
                DBSchema.DataClassTable.TABLE_NAME,   //表名
                new String[]{               //要提取的字段名
                        DBSchema.DataClassTable.COLUMN_ID,
                        DBSchema.DataClassTable.COLUMN_NAME,
                        DBSchema.DataClassTable.COLUMN_TIME,
                        DBSchema.DataClassTable.COLUMN_TYPE,
                        DBSchema.DataClassTable.COLUMN_CONTENT},
                "name=?",   //where
                new String[]{InfoName},   //where params
                null,   //groupby
                null,   //having
                null    //orderby
        );
        if(cursor.moveToFirst())
        return readFromCursor(cursor);
        else return null;
    }

}