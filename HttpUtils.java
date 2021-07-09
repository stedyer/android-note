package com.shixun.zz_shixun01;


import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;

public class HttpUtils {

    private static final String URL = "http://www.tuling123.com/openapi/api";
    private static final String APT_KEY = "90feef51109345c19f4fa94e32659c58";


    //发送消息
    public static ChatMessage sendMessage(String msg) {
        ChatMessage chatMessage = new ChatMessage();
        String jsonStr = doGet(msg);
        Log.i("ChatMessage", jsonStr);
        Gson gson = new Gson();
        RequestResult result;
        try {
            result = gson.fromJson(jsonStr, RequestResult.class);
            chatMessage.setMsg(result.getText());
        } catch (Exception e) {
            e.printStackTrace();
            chatMessage.setMsg("服务器繁忙,请稍后再试!");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(ChatMessage.Type.INCOMIMG);
        return chatMessage;
    }


    //网络请求处理
    public static String doGet(String msg) {
        String result = "";
        String url = setParams(msg);
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        try {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int hasRead = is.read(buf);
                if (hasRead < 0) {
                    break;
                }
                baos.write(buf, 0, hasRead);
            }
            baos.flush();
            result = new String(baos.toByteArray());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    //拼接URL
    private static String setParams(String msg) {
        String url = "";

        try {
            url = URL + "?key=" + APT_KEY + "&info=" + URLEncoder.encode(msg, "utf-8");
            //http://www.tuling123.com/openapi/api?key=180f54b9ad614ee8b4939e26471f764b&info=讲个笑话
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}