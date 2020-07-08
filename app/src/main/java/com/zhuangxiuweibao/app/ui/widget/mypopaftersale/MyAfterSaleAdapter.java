package com.zhuangxiuweibao.app.ui.widget.mypopaftersale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.ui.bean.ReasonBean;
import com.zhuangxiuweibao.app.ui.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAfterSaleAdapter extends RecyclerView.Adapter<MyAfterSaleAdapter.ViewHolder> {

    private Context mContext;
    private List<ReasonBean> list=new ArrayList<>();
    private OnItemClickListener mListener;

    public MyAfterSaleAdapter(Context context, OnItemClickListener listener){
        mContext=context;
        mListener=listener;
    }

    public void update(List l){
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_reason,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_reason.setText(list.get(position).getReason());
        if (list.get(position).isSelect()){
            holder.tv_reason.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.mipmap.icon_rect_select,0);
        }else {
            holder.tv_reason.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.mipmap.icon_rect_unselect,0);
        }
        holder.tv_reason.setOnClickListener(v -> {
            if (mListener!=null) mListener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_reason;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_reason=itemView.findViewById(R.id.tv_reason);
        }
    }
}
