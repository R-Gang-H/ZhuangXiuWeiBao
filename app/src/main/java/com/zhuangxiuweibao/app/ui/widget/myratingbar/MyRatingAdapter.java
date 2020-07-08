package com.zhuangxiuweibao.app.ui.widget.myratingbar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.ui.bean.ImgBean;

import java.util.ArrayList;
import java.util.List;

public class MyRatingAdapter extends RecyclerView.Adapter<MyRatingAdapter.ViewHolder> {

    private List<ImgBean> list = new ArrayList<>();
    private Context mContext;
    private OnStarChangeListener mListener;

    public MyRatingAdapter(Context context) {
        mContext = context;
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    public void setListener(OnStarChangeListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_rating, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img_star.setImageResource(list.get(position).getRes());
        holder.img_star.setOnClickListener(v -> {
            if (mListener != null) mListener.change(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_star;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_star = itemView.findViewById(R.id.img_star);
        }
    }

    public interface OnStarChangeListener {
        void change(int position);
    }
}
