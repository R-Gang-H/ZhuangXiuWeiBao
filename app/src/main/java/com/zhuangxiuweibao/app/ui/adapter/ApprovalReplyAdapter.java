package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class ApprovalReplyAdapter extends RecyclerView.Adapter<ApprovalReplyAdapter.ViewHolder> {

    private Context mContext;
    private List<ApprovalEntity> list = new ArrayList<>();

    public ApprovalReplyAdapter(Context context) {
        mContext = context;
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_approval_reply, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //配置数据
        if (position == getItemCount() - 1) {
            holder.view_line1.setVisibility(View.INVISIBLE);
        } else {
            holder.view_line1.setVisibility(View.VISIBLE);
        }
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_date.setText(DateUtils.getDateToString(Long.valueOf(list.get(position).getTime())
                , "yyyy/MM/dd HH:mm:ss"));
        holder.tv_reply.setText(list.get(position).getStatus());
        GlideUtils.setGlideImg(mContext, list.get(position).getImg(),
                R.mipmap.icon_avatar, holder.rou_head);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv_name, tv_date, tv_reply;
        RoundedImageView rou_head;
        View view_line1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = itemView.findViewById(R.id.tv_index);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_reply = itemView.findViewById(R.id.tv_reply);
            rou_head = itemView.findViewById(R.id.rou_head);
            view_line1 = itemView.findViewById(R.id.view_line1);
        }
    }
}
