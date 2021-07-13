package com.shixun.zz_shixun01;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;//列表

    private List<ChatMessage> pastdatas;//保存消息的列表
    private EditText inputMsg;//输入消息
    private Button sendMsg;//发送消息
    private ChatMessageAdapter adapter;
    private DBManager dbmanager;  //数据库管理工具类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("handle","hang");
        initView();
        dbmanager = new DBManager(this);
//        dbmanager.del(2);
//        dbmanager.dropTable();
        pastdatas = new ArrayList<>();
        // 数据库查询，读取旧的聊天信息
        try {
            pastdatas = dbmanager.query();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pastdatas.add(new ChatMessage("亲！有什么问题吗?", new Date(), ChatMessage.Type.INCOMIMG));



        adapter = new ChatMessageAdapter(pastdatas, this);
        listView.setAdapter(adapter);
        listView.setSelection(pastdatas.size() -1);
        }


    private void initView() {
        listView = findViewById(R.id.listView_msg);
        inputMsg = findViewById(R.id.input_msg);
        sendMsg = findViewById(R.id.btn_send);
    }
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatMessage fromMessage= (ChatMessage) msg.obj;
            pastdatas.add(fromMessage);
            adapter.notifyDataSetChanged();

            //实时显示消息,避免被遮盖
            listView.setSelection(pastdatas.size() - 1);
        }
    };

    public void send(View view) {
        final String toMsg = inputMsg.getText().toString();
        if (TextUtils.isEmpty(toMsg)) {
            Toast.makeText(MainActivity.this, "发送消息不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        //处理输入的数据
        ChatMessage toMessage = new ChatMessage();
        toMessage.setDate(new Date());//设置发送的时间
        toMessage.setMsg(toMsg);//设置发送的内容
        toMessage.setType(ChatMessage.Type.OUTCOMIMG);//设置类型
        pastdatas.add(toMessage);//添加到集合中
        dbmanager.add(toMessage);   //输入的消息存入数据库
        adapter.notifyDataSetChanged();//显示数据
        //实时显示消息,避免被遮盖
        listView.setSelection(pastdatas.size() - 1);
        //清空内容
        inputMsg.setText("");

        //网络操作,在子线程中操作,handler中接收
        new Thread() {
            @Override
            public void run() {
                super.run();
                ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
                Log.i("hhhhhh", fromMessage + "");

                Message m = Message.obtain();
                m.obj = fromMessage;
                //将数据发送出去
                handler.sendMessage(m);

                dbmanager.add(fromMessage);// 接收的消息存入数据库


            }
        }.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();


    }

    public void dele(View view) {
        new DBHelper(this,"sent_storage",null,2);
    }
}