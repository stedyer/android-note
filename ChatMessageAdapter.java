package com.shixun.zz_shixun01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

class ChatMessageAdapter extends BaseAdapter {

    private List<ChatMessage> datas;//使用构造器传递保存的数据
    private Context context;

    public ChatMessageAdapter(List<ChatMessage> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    //重写方法,判断消息的类型
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = datas.get(position);
        int flag = 0;
        if (chatMessage.getType() == ChatMessage.Type.INCOMIMG) {
            //接收消息
            flag = 0;
        }else{
            flag = 1;
        }
        return flag;//发送消息
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //初始化显示消息的控件,方便后续的使用
        ViewHolder viewHolder = new ViewHolder();
        //获取消息显示的位置
        ChatMessage chatMessage = datas.get(position);
        //判断布局是否存在
        if (convertView == null) {
            //如果没有布局,创建布局
            //等于0表示的是接收的消息
            if (getItemViewType(position) == 0) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_from_msg, parent, false);
                //获取加载进来的布局文件中控件的ID
                viewHolder.mDate = convertView.findViewById(R.id.from_msg_date);
                viewHolder.mMsg = convertView.findViewById(R.id.from_msg_content);
            } else {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_to_msg, parent, false);
                //获取加载进来的布局文件中控件的ID
                viewHolder.mDate = convertView.findViewById(R.id.to_msg_date);
                viewHolder.mMsg = convertView.findViewById(R.id.to_msg_info);
            }
            //保存数据
            convertView.setTag(viewHolder);

        } else {
            //如果有布局,直接使用即可
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置时间格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.mDate.setText(df.format(chatMessage.getDate()));
        viewHolder.mMsg.setText(chatMessage.getMsg());
        return convertView;
    }


    private final class ViewHolder {
        TextView mDate, mMsg;
    }
    //重写方法,返回Type的总数量
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
