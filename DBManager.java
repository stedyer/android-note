package com.shixun.zz_shixun01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private Context context;
    private static DBManager instance;
    //操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;
    DBManager(Context context){
        this.context = context;
        DBHelper dbHelper = new DBHelper(context,"sent_storage",null,1);
        writableDatabase = dbHelper.getWritableDatabase();

    }

    public static DBManager getInstance(Context context){
        if (instance==null){
            synchronized (DBManager.class){
                if (instance==null){
                    instance = new DBManager(context);
                }
            }

        }
        return instance;
    }

    public void add(ChatMessage msg){

        //for (int i = 0; i <msglist.size() ; i++) {
            ContentValues contentValues = new ContentValues();
            String[] strs = msg.toString().split(",");
            contentValues.put("_msg",strs[0]);
            contentValues.put("_date",strs[1]);
            contentValues.put("_type",strs[2]);
            Log.i("typeStr",strs[2]);
            writableDatabase.insert("msgs",null,contentValues);
            //writableDatabase.close();
        //}
    }

    public List<ChatMessage> query() throws ParseException {
        List<ChatMessage> result = new ArrayList<>();
        Cursor cursor = writableDatabase.query("msgs",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String msg = (String)cursor.getString(cursor.getColumnIndex("_msg"));
            String date = (String)cursor.getString(cursor.getColumnIndex("_date"));
            String type = (String)cursor.getString(cursor.getColumnIndex("_type"));
            ChatMessage o = new ChatMessage(msg+","+date+","+type);
            result.add(o);
        }
        return result;
    }

    public void del(int i){
        writableDatabase.delete("msgs","_type=?",new String[]{"INCOMING"});
        writableDatabase.delete("msgs","_type=?",new String[]{"OUTCOMING"});
        Log.i("dele","INCOMING".toString()+"");
    }
}
