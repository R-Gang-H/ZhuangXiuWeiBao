package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.activity.CommunitiesActivity;
import com.zhuangxiuweibao.app.ui.activity.MessageInfoActivity;
import com.zhuangxiuweibao.app.ui.activity.ShequAuditDetailActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import butterknife.BindView;

/**
 * Date: 2019/4/11
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.ui.adapter
 */
public class MessageAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_red_shape)
    CardView ivRedShape;
    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;
    @BindView(R.id.tv_housing)
    TextView tvHousing;
    @BindView(R.id.tv_msg_time)
    TextView tvMsgTime;
    @BindView(R.id.ri_icon)
    RoundedImageView riIcon;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_red_shape)
    TextView tvRedShape;

    private String xiaoqu = "1";// 1当前用户所在小区 2其他小区

    List<MessageEntity> msgData = new ArrayList<>();

    public MessageAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity msgEntity = msgData.get(position);
        CharSequence msgTitle = msgEntity.getTitle();
        CharSequence msgContent = msgEntity.getContent();
        // type:定义： 1：任务 2：通知3：审批 4：紧急求助 5：维保 6：社区交流 7.需要大妈审核的社区交流帖子
        // 8.小喇叭审核通过的通知 9.群通知
        // tagId定义：1:寻物启事2:闲置物品3:为你服务4。通知公告5.活动报名6.意见建议7.群通知
        int iconImg = 0;
        String title = "";
        switch (msgEntity.getType()) {
            case "1":// 任务
                iconImg = R.mipmap.icon_task;
                title = "任务";
                break;
            case "2":// 通知
            case "9":// 群通知
                title = "通知";
                break;
            case "3":// 审批
                iconImg = R.mipmap.icon_approval_small;
                title = "审批";
                if (msgEntity.getType2().equals("S2")// 回复有更新
                        || msgEntity.getType2().equals("S3")) {// 审批进度有更新
//                    msgTitle = Html.fromHtml(String.format("<b>%s</b><br/><font color='#333'><b>%s</b></font>&nbsp;\"%s\"",
//                            msgTitle, msgEntity.getAddUserName(), msgContent));
                    msgTitle = String.format("%s\n%s\t\"%s\"", msgTitle, msgEntity.getAddUserName(), msgContent);
                }
                break;
            case "4":// 紧急求助
                iconImg = R.mipmap.icon_sos;
                title = "紧急求助";
                break;
            case "5":// 维保
                iconImg = R.mipmap.icon_maintain;
                title = "维保服务";
                break;
            case "6":// 社区交流
                iconImg = R.mipmap.icon_header;
                break;
            case "7":// 需要大妈审核的社区交流帖子
                iconImg = R.mipmap.icon_image;
                title = "社区交流审核";
                break;
            case "8":// 小喇叭审核通过的通知
                iconImg = R.mipmap.icon_laba;
                title = "小喇叭";
                break;
        }
        switch (msgEntity.getType2()) {
            case "t1":// app升级通知
            case "t2":// 系统通知
                iconImg = R.mipmap.icon_x_notice;
                break;
            case "t3":// 社区通知
                iconImg = R.mipmap.icon_s_notice;
                break;
            case "t4":// 群通知
                iconImg = R.mipmap.icon_q_notice;
                break;
        }
        if (msgEntity.getType().equals("6")) {
            boolean isbody = msgEntity.getAddUserSex().equals("1");//1：男 2：女
            GlideUtils.setGlideImg(context, msgEntity.getAddHeadImage(), iconImg, riIcon);
            title = String.format("%s%s", msgEntity.getAddUserName().substring(0, 1), isbody ? "先生" : "女士");
        } else {
            GlideUtils.setGlideImg(context, "", iconImg, riIcon);
        }
        if (msgEntity.getType2().equals("t1")) {// 版本升级通知
            tvMsgTitle.setText("版本升级通知");
        } else {
            tvMsgTitle.setText(title);
        }
//        tvHousing.setVisibility(xiaoqu.equals("2") ? View.VISIBLE : View.GONE);
        tvMsgTime.setText(DateUtils.getDateToString(
                Long.parseLong(msgEntity.getCreateAt()), "yyyy/MM/dd HH:mm"));
        if (msgEntity.getType().equals("7")) {
            tvContent.setText(String.format("%s%s发布的新内容待审核", msgEntity.getXiaoquName(), msgEntity.getAddUserName()));
        } else {
            if (msgEntity.getType2().equals("t1")) {// 版本升级通知
                tvContent.setText("安卓版" + msgEntity.getTitle() + "升级，查看本次升级的内容。");
            } else {
                tvContent.setText(msgTitle);
            }
        }
        String messageCount = msgEntity.getMessageCount();
        ivRedShape.setVisibility(Integer.valueOf(messageCount) > 0 ? View.VISIBLE : View.GONE);
        tvRedShape.setText(messageCount);
//        ivRedShape.setVisibility(msgEntity.getIsRead().equals("2") ? View.VISIBLE : View.GONE);//未读 已读

        tvHousing.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, CommunitiesActivity.class)
                    .putExtra("shequId", msgEntity.getXiaoquId()));
        });
        holder.itemView.setOnClickListener(v -> {
            // 1：任务 2：通知3：审批 4：紧急求助 5：维保 6：社区交流 7.需要大妈审核的社区交流帖子
            // 8.小喇叭审核通过的通知 9.群通知
            switch (msgEntity.getType()) {
//                case "2":// 2：通知
//                case "6":// 6：社区交流
//                    startActivity(msgEntity, PostDetailsCommunityActivity.class);
//                    break;
                case "7":// 7.需要大妈审核的社区
                    startActivity(msgEntity, ShequAuditDetailActivity.class);
                    break;
                default:
                    startActivity(msgEntity, MessageInfoActivity.class);
                    break;
            }
        });
    }

    private void startActivity(MessageEntity msgEntity, Class z) {
        mContext.startActivity(new Intent(mContext, z)
                .putExtra("msgEntity", msgEntity));
    }

    public void setData(ArrayList<MessageEntity> msgData, String xiaoqu) {
        this.msgData.clear();
        this.msgData.addAll(msgData);
        this.xiaoqu = xiaoqu;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return msgData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_message_list;
    }
}
