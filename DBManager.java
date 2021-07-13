package com.shixun.zz_shixun01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            String msgstr,dstr,typestr;
            msgstr = msg.getMsg();
            dstr = msg.getDate().toString();
            typestr = msg.getType().toString();
            contentValues.put("_msg",msgstr);
            contentValues.put("_date",dstr);
            contentValues.put("_type",typestr);
            Log.i("typeStr",msgstr+"\t"+dstr+"\t"+typestr);
            writableDatabase.insert("msgs",null,contentValues);
            //writableDatabase.close();
        //}
    }

    public List<ChatMessage> query() throws ParseException {
        List<ChatMessage> result = new ArrayList<>();
        Cursor cursor = writableDatabase.query("msgs",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String msg = (String)cursor.getString(cursor.getColumnIndex("_msg"));
            String datestr = (String)cursor.getString(cursor.getColumnIndex("_date"));
            String typestr = (String)cursor.getString(cursor.getColumnIndex("_type"));
            SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sDF.parse(datestr);
            ChatMessage.Type type = ChatMessage.Type.valueOf(typestr);
            ChatMessage o = new ChatMessage(msg,date,type);
            result.add(o);
        }
        return result;
    }

    public void del(int i){
        DBHelper dbHelper = new DBHelper(context,"sent_storage",null,1);
        SQLiteDatabase deltmanger = dbHelper.getWritableDatabase();
        deltmanger.delete("msgs","_type=?",new String[]{"INCOMING"});
        deltmanger.delete("msgs","_type=?",new String[]{"OUTCOMING"});
        Log.i("dele","INCOMING".toString()+"");
    }
    public void dropTable(){
        DBHelper dbHelper = new DBHelper(context,"sent_storage",null,2);

    }
}
