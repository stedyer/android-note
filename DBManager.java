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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            msgstr = msg.getMsg();
            Date date = msg.getDate();
            dstr = sdf.format(date);
            typestr = msg.getType().toString();
            contentValues.put("_msg",msgstr);
            contentValues.put("_date",dstr);
            contentValues.put("_type",typestr);
            Log.i("typeStr",msgstr+"\t"+dstr+"\t"+typestr);
            writableDatabase.insert("msgs",null,contentValues);
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
//
//        int msgflag01 = writableDatabase.delete("msgs","_id=?",new String[]{1+""});
//        int msgflag02 = writableDatabase.delete("msgs", "_id=?", new String[]{2+""});
//        Log.i("ifdele",msgflag01+""+msgflag02);
        writableDatabase.execSQL("delete from msgs");
    }
    public void dropTable(){
        DBHelper dbHelper = new DBHelper(context,"sent_storage",null,2);

    }
}
