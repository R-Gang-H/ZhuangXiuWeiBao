package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/4/23
 * Author: haoruigang
 * Description: 消息详情 适配器
 */
public class MessageInfoAdapter extends XrecyclerAdapter {

    ArrayList<MessageEntity> messageList = new ArrayList<>();

    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;
    @BindView(R.id.tv_msg_content)
    TextView tvMsgContent;
    @BindView(R.id.tv_msg_time)
    TextView tvMsgTime;

    public MessageInfoAdapter(List datas, Context context) {
        super(datas, context);
        this.messageList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvMsgTitle.setText(messageList.get(position).getTitle());
        tvMsgContent.setText(messageList.get(position).getContent());
        tvMsgTime.setText(DateUtils.getDateToString(
                Long.parseLong(messageList.get(position).getCreateAt()),
                "yyyy/MM/dd HH:mm"));
    }

    public void setData(List<MessageEntity> messageList) {
        this.messageList.clear();
        this.messageList.addAll(messageList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_message_info;
    }

}
