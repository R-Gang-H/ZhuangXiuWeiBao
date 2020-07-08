package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import java.util.ArrayList;
import java.util.List;

public class ApprovalLineAdapter extends RecyclerView.Adapter<ApprovalLineAdapter.ViewHolder> {

    private Context mContext;
    private List<ApprovalEntity> list = new ArrayList<>();
    private boolean isShowLine;

    public ApprovalLineAdapter(Context context) {
        mContext = context;
    }

    public void update(List l, boolean isShowLine) {
        this.isShowLine = isShowLine;
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_timeline, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isShowLine) {
            if (position == getItemCount() - 1) {
                holder.img_line.setVisibility(View.INVISIBLE);
            } else {
                holder.img_line.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_line.setVisibility(View.INVISIBLE);
        }
        if (list.get(position).isFinish()) {
            holder.img_mark.setImageResource(R.mipmap.icon_timeline);
        } else {
            holder.img_mark.setImageResource(R.mipmap.icon_timeline2);
        }
        if (U.isEmpty(list.get(position).getTime())) {
            list.get(position).setTime("0");
        }
        holder.tv_date.setText(DateUtils.getDateToString(Long.valueOf(list.get(position).getTime()), "MM-dd"));
        holder.tv_time.setText(DateUtils.getDateToString(Long.valueOf(list.get(position).getTime()), "HH:mm"));
        holder.tv_opinion.setText(list.get(position).getStatus());
        holder.tv_name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date, tv_time, tv_name, tv_opinion;
        ImageView img_line, img_mark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_opinion = itemView.findViewById(R.id.tv_opinion);
            img_line = itemView.findViewById(R.id.img_line);
            img_mark = itemView.findViewById(R.id.img_mark);
        }
    }
}
