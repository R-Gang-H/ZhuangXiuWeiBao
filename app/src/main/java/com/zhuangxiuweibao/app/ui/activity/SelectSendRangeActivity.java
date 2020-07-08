package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.SelectSendRangeAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectSendRangeActivity extends BaseActivity implements ViewOnItemClick {


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitleLeft;
    @BindView(R.id.companyList)
    XRecyclerView companyList;
    @BindView(R.id.btnComplete)
    TextView btnComplete;
    @BindView(R.id.rl_next_button)
    RelativeLayout rl_next_button;
    @BindView(R.id.tv_title_right)
    TextView tv_title_right;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    private List<UserBean> allList = new ArrayList<>();
    private List<List<UserBean>> objList = new ArrayList<>();//所有级别的商品集合的列表
    private SelectSendRangeAdapter adapter;
    private String groupId = "0";
    private StringBuffer companyId = new StringBuffer(), departmentId = new StringBuffer(), name = new StringBuffer();
    private boolean isSelect = false;
    private int pageNum = 0, position;


    @Override
    public int getLayoutId() {
        return R.layout.activity_select_send_range;
    }

    @Override
    public void initView() {
        tvTitleLeft.setText("选择发送范围");
        initList();
    }

    /**
     * 初始化列表
     */
    private void initList() {
        LayoutManager.getInstance().init(this).iniXrecyclerView(companyList);
        adapter = new SelectSendRangeAdapter(allList, this, this);
        companyList.setAdapter(adapter);
    }

    @Override
    public void initData() {
        getData();
    }

    private void getData() {
        HttpManager.getInstance().getGroup("SelectSendRangeActivity", groupId, new HttpCallBack<BaseDataModel<UserBean>>() {
            @Override
            public void onSuccess(BaseDataModel<UserBean> data) {
                List<UserBean> userBeans = new ArrayList<>();
                userBeans.addAll(data.getData());
                pageNum++;//每次请求成功之后都要加页面
                if (!objList.contains(userBeans)) {//不包含这一层的话就添加这一层
                    objList.add(userBeans);
                }
                setData(userBeans);
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(SelectSendRangeActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void setData(List<UserBean> data) {

        if (data != null && data.size() > 0) {
            loadView.setVisibility(View.GONE);
            companyList.setVisibility(View.VISIBLE);
            btnComplete.setVisibility(View.VISIBLE);
            loadView.delayShowContainer(true);//展示有数据
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getIsCompany().equals("2")) {//	1表示公司 2表示部门
                    tv_title_right.setText("全选");
                    rl_next_button.setVisibility(View.VISIBLE);//如果是组织的话和可以选择全部组织
                }
            }
            adapter.update(data);
        } else {
            loadView.setVisibility(View.VISIBLE);
            companyList.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            loadView.setNoShown(true);//展示暂无内容
        }

    }


    @OnClick({R.id.rl_back_button, R.id.btnComplete, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                if (pageNum == 2) {
                    if (objList.get(pageNum - 1) != null) {//只有第二层不为空的时候才删除
                        objList.remove(pageNum - 1);
                    }
                    pageNum--;
                    setData(objList.get(0));

                } else {
                    finish();
                }
                break;
            case R.id.btnComplete:
                if (pageNum == 1) {//证明是公司 没有分部
                    int select = 0;
                    for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {
                        if (objList.get(pageNum - 1).get(i).isSelect()) {
                            String fh = "";
                            if (i > 0) {
                                fh = ",";
                            }
                            select++;
                            name.append(objList.get(pageNum - 1).get(i).getName() + fh);
                            companyId.append(objList.get(pageNum - 1).get(i).getGroupId() + fh);
                        }
                    }
                    if (select == 0) {
                        Toast.makeText(this, "请选择公司", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (select > 1) {//选择多个公司
                        name.append("等" + select + "个公司");
                    }
                } else {//判断有分部
                    int selectNum = 0;
                    for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {
                        if (objList.get(pageNum - 1).get(i).isSelect()) {
                            if (i > 0) {
                                departmentId.append(",");
                            }
                            selectNum++;
                            departmentId.append(objList.get(pageNum - 1).get(i).getGroupId());//在提交的时候进行添加判断
                        }
                    }
                    if (selectNum == objList.get(pageNum - 1).size()) {
                        name.append("");//全选的话就不能
                    } else {
                        if (selectNum == 0) {//一个都没有选择
                            Toast.makeText(this, "请选择分部", Toast.LENGTH_SHORT).show();
                            break;
                        } else {//选择了几个
                            for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {
                                if (objList.get(pageNum - 1).get(i).isSelect()) {
                                    name.append(objList.get(pageNum - 1).get(i).getName());//选择单个的话添加一个名字就好
                                }
                            }
                        }
                    }

                }
                Intent intent = new Intent();
                if (departmentId.length() != 0) {//有分部
                    intent.putExtra("departmentId", departmentId.toString());// .deleteCharAt(departmentId.length() - 1)
                }
                intent.putExtra("groupId", groupId).putExtra("name", name.toString())
                        .putExtra("companyId", companyId.toString());// .deleteCharAt(companyId.length() - 1)
                setResult(7, intent);
                finish();
                break;
            case R.id.rl_next_button://全选
                if (isSelect == false) {
                    for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {

                        objList.get(pageNum - 1).get(i).setSelect(true);
                    }
                    isSelect = true;
                } else {
                    for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {
                        objList.get(pageNum - 1).get(i).setSelect(false);
                    }
                    isSelect = false;
                }
                adapter.update(objList.get(pageNum - 1));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (pageNum == 1) {
            for (int i = 0; i < objList.get(pageNum - 1).size(); i++) {
                if (i == position - 1) {//证明选择的是
                    //点击事件
                    groupId = objList.get(pageNum - 1).get(position - 1).getGroupId();
                    name.append(objList.get(pageNum - 1).get(position - 1).getName());//如果有分部的话就要保存这一级的公司名字
                    if (companyId.length() > 0) {
                        companyId.delete(0, companyId.length());//有的会重新选择
                    }
                    if (i > 0) {
                        companyId.append(",");
                    }
                    companyId.append(groupId);//如果有分部的话就要保存这一级的公司Id
                    getData();
                }
            }

        }

    }
}
