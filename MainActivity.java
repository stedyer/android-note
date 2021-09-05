package com.shixun.zz_shixun01;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;//列表

    private List<ChatMessage> pastdatas;//保存消息的列表
    private EditText inputMsg;//输入消息
    private Button sendMsg;//发送消息
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("handle", "hang");
        initView();
        pastdatas = new ArrayList<>();

        //读取文件,读取旧的聊天信息
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(openFileInput("msgs.txt"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true){
                int hanRead = inputStream.read(buffer);
                if (hanRead<0){
                    break;
                }
                baos.write(buffer,0,hanRead);
            }
            Log.i("empty",baos.toString().isEmpty()+"");
            if(!baos.toString().isEmpty()){
                String[] allmsgs = baos.toString().split("\n");
                for (int i= 0; i <allmsgs.length ; i++) {
                    String[] msg = allmsgs[i].split(",");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(msg[1]);
                    ChatMessage.Type type = ChatMessage.Type.valueOf(msg[2]);
                    ChatMessage chatMessage = new ChatMessage(msg[0],date,type);
                    pastdatas.add(chatMessage);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        pastdatas.add(new ChatMessage("亲！有什么问题吗?", new Date(), ChatMessage.Type.INCOMIMG));
        save();
        adapter = new ChatMessageAdapter(pastdatas, this);
        listView.setAdapter(adapter);
        listView.setSelection(pastdatas.size() - 1);

    }


    private void initView() {
        listView = findViewById(R.id.listView_msg);
        inputMsg = findViewById(R.id.input_msg);
        sendMsg = findViewById(R.id.btn_send);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatMessage fromMessage = (ChatMessage) msg.obj;
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
            }
        }.start();
    }

    public void save(){
        BufferedOutputStream bos = null;
        try {

            bos = new BufferedOutputStream(openFileOutput("msgs.txt",Context.MODE_PRIVATE));
            for (int i = 0;i<pastdatas.size();i++){
                ChatMessage cm = pastdatas.get(i);
                String msgstr = cm.getMsg();
                Date date = cm.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datestr = sdf.format(date);
                String typestr = cm.getType().toString();
                bos.write((msgstr+","+datestr+","+typestr).getBytes());
                if (i<pastdatas.size()-1) bos.write("\n".getBytes());
                bos.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bos!=null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();

    }

    public void dele(View view) {

        Toast.makeText(this, "清除成功!", Toast.LENGTH_SHORT).show();
        pastdatas.removeAll(pastdatas);
        //save();
        adapter.notifyDataSetChanged();
    }
}