package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.text.Html;
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

    private ArrayList<MessageEntity> messageList = new ArrayList<>();
    private String addUserName;

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
        MessageEntity messages = messageList.get(position);
        // 1：任务 2：通知3：审批 4：紧急求助 5：维保 6：社区交流 7.需要大妈审核的社区交流帖子
        // 8.小喇叭审核通过的通知 9.群通知
        CharSequence msgTitle = messages.getTitle();
        CharSequence msgContent = messages.getContent();
        switch (messages.getType()) {
//            case "1":// 1.任务
//                //1任务，2群通知 ，3审批
//                msgContent = "任务";
//                break;
//            case "2":
//                msgContent = "通知";
//                break;
            case "3":// 3.审批
                msgTitle = Html.fromHtml(String.format("<b>%s</b>", msgTitle));
//            case "9":// 9.群通知
                //1任务，2群通知 ，3审批
                if (messages.getType2().equals("S2")// 回复有更新
                        || messages.getType2().equals("S3")) {// 审批进度有更新
                    msgContent = Html.fromHtml(String.format("<font color='#333'><b>%s</b></font>\t\"%s\"", addUserName, msgContent));
                }
                break;
        }
        tvMsgTitle.setText(msgTitle);
        tvMsgContent.setText(msgContent);
        tvMsgTime.setText(DateUtils.getDateToString(
                Long.parseLong(messages.getCreateAt()),
                "yyyy/MM/dd HH:mm"));
    }

    public void setData(List<MessageEntity> messageList, String addUserName) {
        this.messageList.clear();
        this.messageList.addAll(messageList);
        this.addUserName = addUserName;
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
