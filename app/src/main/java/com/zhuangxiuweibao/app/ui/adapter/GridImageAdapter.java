package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.ui.activity.FixedApprovalActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * author：haoruigang
 * data：2018-4-10 17:14:12
 */
public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private int selectMax = 5;
    private Context context;

    public List<LocalMedia> selectList = new ArrayList<>();
    public List<UploadModule> uploadModules = new ArrayList<>();
    public List<String> pngOravis = new ArrayList<>();

    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        LinearLayout ll_del;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            ll_del = view.findViewById(R.id.ll_del);
        }
    }

    @Override
    public int getItemCount() {
        if (uploadModules.size() < selectMax) {
            return uploadModules.size() + 1;
        } else {
            return uploadModules.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = uploadModules.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于5张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.mipmap.addimg_1x);
            viewHolder.mImg.setOnClickListener(v ->
                    mOnAddPicClickListener.onAddPicClick());
            viewHolder.ll_del.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.ll_del.setVisibility(mOnAddPicClickListener != null ? View.VISIBLE : View.GONE);
            viewHolder.ll_del.setOnClickListener(view -> {
                int index = viewHolder.getAdapterPosition();
                // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                if (index != RecyclerView.NO_POSITION) {
                    pngOravis.remove(index);
                    uploadModules.remove(index);
                    for (LocalMedia picture : FixedApprovalActivity.pictureLists) {
                        if (picture.getPath().equals(selectList.get(index).getPath())) {
                            FixedApprovalActivity.pictureLists.remove(picture);
                            break;
                        }
                    }
                    for (LocalMedia network : FixedApprovalActivity.networkPubs) {
                        if (network.getPath().equals(selectList.get(index).getPath())) {
                            FixedApprovalActivity.networkPubs.remove(network);
                            break;
                        }
                    }
                    for (LocalMedia network : FixedApprovalActivity.networkPris) {
                        if (network.getPath().equals(selectList.get(index).getPath())) {
                            FixedApprovalActivity.networkPris.remove(network);
                            break;
                        }
                    }
                    selectList.remove(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index, uploadModules.size());
                }
            });
            UploadModule uploadModule = uploadModules.get(position);
            String path = uploadModule.getPicPath();
            String picType = uploadModule.getPictureType();
            if (picType.contains("image") || picType.contains("jpg") || picType.contains("jpeg") || picType.contains("png")) {//图片
                Glide.with(context).load(path).into(viewHolder.mImg);
            } else if (picType.contains("video") || picType.contains("mp4")) {//视频
                Glide.with(context).load(path).into(viewHolder.mImg);
            } else {
                int icon = 0;
                if (picType.contains("pdf")) {
                    icon = R.mipmap.ic_file_pdf;
                } else if (picType.contains("xls")) {
                    icon = R.mipmap.ic_file_xls;
                } else if (picType.contains("doc")) {
                    icon = R.mipmap.ic_file_doc;
                } else if (picType.contains("zip")) {
                    icon = R.mipmap.ic_file_zip;
                }else{
                    icon = R.mipmap.icon_file;
                }
                Glide.with(context).load(icon).into(viewHolder.mImg);
            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(v -> {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mItemClickListener.onItemClick(adapterPosition, v);
                });
            }
        }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
