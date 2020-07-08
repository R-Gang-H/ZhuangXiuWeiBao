package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.ui.activity.FixedApprovalActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckerAdapter extends RecyclerView.Adapter<CheckerAdapter.Holder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<String> list = new ArrayList();
    private String isConfirm;

    public CheckerAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void update(List l,String isConfirm) {
        list.clear();
        list.addAll(l);
        this.isConfirm = isConfirm;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_add_people, parent, false);
        Holder holder = new Holder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (isConfirm.equals("2") && position != list.size()) {
            holder.img_name.setText(list.size() > 0 ? list.get(position) : "");
            holder.img_name.setBackgroundColor(Color.parseColor("#F6F6F6"));
            holder.img_del.setVisibility(View.GONE);
            return;//不再继续向下判断
        }
        if (position == list.size()) {//这里是最后一条
            holder.img_name.setVisibility(View.VISIBLE);
            holder.img_name.setText("");
            holder.img_name.setBackgroundResource(R.mipmap.ic_add);
            holder.img_del.setVisibility(View.INVISIBLE);
        } else {
            holder.img_name.setText(list.get(position));
            holder.img_name.setBackgroundColor(Color.parseColor("#F6F6F6"));
            holder.img_del.setVisibility(View.VISIBLE);
        }
        holder.img_del.setOnClickListener(v -> {
            if (mListener != null) mListener.onDel(position);
        });
        holder.img_name.setOnClickListener(v -> {
            if (mListener != null) {
                if (holder.img_name.getVisibility() == View.VISIBLE) {
                    if (position == list.size()) {
                        mListener.onAdd();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size() < 9 ? list.size() + 1 : list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView img_name;
        ImageView img_del;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img_del = itemView.findViewById(R.id.img_del);
            img_name = itemView.findViewById(R.id.img_name);
        }
    }

    public interface OnItemClickListener {
        void onDel(int position);

        void onAdd();
    }
}
