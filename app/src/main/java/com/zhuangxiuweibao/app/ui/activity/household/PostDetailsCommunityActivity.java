package com.zhuangxiuweibao.app.ui.activity.household;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.ui.activity.AboutUsActivity;
import com.zhuangxiuweibao.app.ui.activity.BaseActivity;
import com.zhuangxiuweibao.app.ui.activity.ImgDetailsActivity;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;

import java.util.Arrays;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

//51页面 帖子详情  社区交流 社区通知
public class PostDetailsCommunityActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.tv_audit_tag)
    TextView tvAuditTag;
    @BindView(R.id.tv_mTitle)
    TextView tvMTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_link)
    TextView tvLinkURL;
    @BindView(R.id.imgList)
    RecyclerView recyclerView;

    private String linkUrl;
    private MessageEntity msgEntity, shequDetail;
    private GridImageAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_post_details_community;
    }

    @Override
    public void initView() {
        initList();
    }

    private void initList() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, null);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            UserManager.getInstance().isReads(msgEntity.getEventId());//处理为已读消息
            switch (msgEntity.getType()) {
                case "2":// 社区通知
                    tvTitle.setText(R.string.notice);
                    ivAvatar.setImageResource(R.mipmap.ic_notify_detail);
                    if (msgEntity.getType2().equals("t1")) {// 版本升级通知
                        tvName.setText("版本升级通知");
                        tvMTitle.setText("安卓版" + msgEntity.getTitle() + "升级");
                    } else {
                        tvName.setText("通知");
                        tvMTitle.setText(msgEntity.getTitle());
                    }
                    tvContent.setText(U.getEmoji(msgEntity.getContent()));
                    tvData.setText(DateUtils.getDateToString(Long.valueOf(msgEntity.getCreateAt()), "yyyy/MM/dd HH:mm"));
                    linkUrl = msgEntity.getLinkUrl().trim();
                    tvLinkURL.setVisibility(U.isNotEmpty(linkUrl) ? View.VISIBLE : View.GONE);
                    tvLinkURL.setText(tvLinkURL.getText() + "\t\t" + linkUrl);
                    recyclerView.setVisibility(View.GONE);
                    break;
                case "6":// 社区交流
                case "8":// 8.小喇叭审核通过的通知
                    tvTitle.setText(R.string.string_shqu_comm);
                    String eventId = msgEntity.getXiaolabaId().equals("0")?msgEntity.getEventId():msgEntity.getXiaolabaId();//小喇叭详情id是==0,是社区交流进来，否小喇叭审核进来的
                    getRelease(eventId);
                    break;
                case "7":// 大妈审核社区交流
                    shequDetail = (MessageEntity) getIntent().getSerializableExtra("shequDetail");
                    setShequeAuditDetail();
                    break;
            }
            String tagName = "";
            switch (msgEntity.getTagId()) {// 1寻物启事 2闲置物品 3为你服务 4.通知公告 5.活动报名 6.意见建议 999不加标签
                case "1":
                    tagName = "寻物启事";
                    break;
                case "2":
                    tagName = "闲置物品";
                    break;
                case "3":
                    tagName = "为你服务";
                    break;
                case "4":
                    tagName = "通知公告";
                    break;
                case "5":
                    tagName = "活动报名";
                    break;
                case "6":
                    tagName = "意见建议";
                    break;
                default:
                    tvAuditTag.setVisibility(View.GONE);
                    break;
            }
            tvAuditTag.setText(tagName);
        }

    }

    private void setShequeAuditDetail() {
        tvTitle.setText(R.string.string_shqu_comm);
        GlideUtils.setGlideImg(this, shequDetail.getAddUserIcon(), R.mipmap.icon_header, ivAvatar);
        boolean isbody = shequDetail.getAddUserSex().equals("1");//1：男 2：女
        String title = U.isNotEmpty(shequDetail.getAddUserName()) ? String.format("%s%s",
                shequDetail.getAddUserName().substring(0, 1), isbody ? "先生" : "女士") : "管理员";
        tvName.setText(title);
        tvData.setText(DateUtils.getDateToString(Long.valueOf(shequDetail.getTime()), "yy/MM/dd HH:mm"));
        tvMTitle.setVisibility(View.GONE);
        String tagName = "";
        switch (shequDetail.getTag()) {// 1寻物启事 2闲置物品 3为你服务 999不加标签
            case "1":
                tagName = "寻物启事";
                break;
            case "2":
                tagName = "闲置物品";
                break;
            case "3":
                tagName = "为你服务";
                break;
            default:
                tvAuditTag.setVisibility(View.GONE);
                break;
        }
        tvAuditTag.setText(tagName);
        tvContent.setText(U.getEmoji(shequDetail.getContent()));
        linkUrl = shequDetail.getLinkUrl().trim();
        tvLinkURL.setVisibility(U.isNotEmpty(linkUrl) ? View.VISIBLE : View.GONE);
        tvLinkURL.setText(tvLinkURL.getText() + "\t\t" + linkUrl);
        setImages(shequDetail.getImages().split("#"));
    }

    private void setImages(String[] images) {
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            adapter.pngOravis = Arrays.asList(images);
            for (String image : images) {
                adapter.uploadModules.add(UploadModule.builder().picPath(image).pictureType("jpg").build());
            }
            adapter.setSelectMax(images.length);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        }
    }

    private void getRelease(String eventId) {
        HttpManager.getInstance().doGetRelease(this.getLocalClassName(), eventId,
                new HttpCallBack<BaseDataModel<MessageEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        shequDetail = data.getData().get(0);
                        setShequeAuditDetail();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ShequAuditDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(PostDetailsCommunityActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("ShequAuditDetailActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_link})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_link:// 查看链接
                if (!linkUrl.contains("http")) {
                    U.showToast("该链接有误");
                    return;
                }
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "3")
                        .putExtra("linkUrl", linkUrl));
                break;
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        startActivity(new Intent(this, ImgDetailsActivity.class)
                .putExtra("orderData", shequDetail)
                .putExtra("index", position));
    }
}
