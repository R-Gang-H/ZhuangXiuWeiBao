package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.SelectAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择 -单选/多选
 */
public class SelectPersonActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.et_search)
    EditText metSearch;
    @BindView(R.id.xre_list)
    RecyclerView mxreList;
    @BindView(R.id.tv_ok)
    TextView mtvOk;

    private SelectAdapter adapter;
    private List<UserBean> list = new ArrayList<>();
    //已选择的数量
    private int position, selectCount = 0;

    private String type;
    private String pid;
    private String cid;

    private SelectAdapter.SelectType mType = SelectAdapter.SelectType.RADIO;
    private String name;
    private String uid;
    private String keyword = "";
    private ArrayList<String> copytoId, checkerId;
    private List<DisclosureEntity> discList;
    private boolean isTaskNeed = false;// 发任务需求

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_person;
    }

    @Override
    public void initView() {
        mtvTitle.setText("联系人");
        LayoutManager.getInstance().initRecyclerView(mxreList, true);
        adapter = new SelectAdapter(list, this, this);
        mxreList.setAdapter(adapter);
    }

    @Override
    public void initData() {
        getDataByIntent();
    }

    private void getDataByIntent() {
        type = getIntent().getStringExtra("type");
        if (type.equals("1")) {//转给同事，单选
            mType = SelectAdapter.SelectType.RADIO;
            partersList();
        } else if (type.equals("3") || type.equals("4") || type.equals("5")) {//工作人员端发审批  单选(抄送)
            copytoId = getIntent().getStringArrayListExtra("copytoId");//抄送人
            checkerId = getIntent().getStringArrayListExtra("checkerId");//抄送人
            discList = (List<DisclosureEntity>) getIntent().getSerializableExtra("discList");//项目执行人
            position = getIntent().getIntExtra("position", -1);//第几项
            isTaskNeed = getIntent().getBooleanExtra("isTaskNeed", false);
            mType = SelectAdapter.SelectType.RADIO;
            getWorkList();
        } else {//追加审批人，多选
            mType = SelectAdapter.SelectType.MORE;
            checkerList();
        }
        metSearch.setOnEditorActionListener((v, actionId, event) -> {//搜索框内容监听
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                keyword = metSearch.getText().toString().trim();
                U.hideKeyboard(SelectPersonActivity.this);
                if (type.equals("1")) {//转给同事，单选
                    mType = SelectAdapter.SelectType.RADIO;
                    partersList();
                } else if (type.equals("3") || type.equals("4") || type.equals("5")) {//工作人员端发审批
                    mType = SelectAdapter.SelectType.RADIO;
                    getWorkList();
                } else {//追加审批人，多选
                    mType = SelectAdapter.SelectType.MORE;
                    checkerList();
                }
                return true;
            }
            return false;
        });
    }

    //21 获取工作人员列表
    private void getWorkList() {
        HttpManager.getInstance().workerList("SelectPersonActivity",
                keyword, new HttpCallBack<BaseDataModel<UserBean>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<UserBean> data) {
                        list.clear();
                        list.addAll(data.getData());
                        if (!isTaskNeed) {// 发任务需求不过滤自己
                            String uid = (String) U.getPreferences("uid", "");
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getUid().equals(uid)) {//不能展示自己的名字
                                    list.remove(i);
                                }
                            }
                        }
                        setData();//查看列表里面有已经选择的审批人或者有没有已经够选择的抄送人
                        adapter.update(list, mType);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SelectPersonActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setData() {
        if (type.equals("3") || type.equals("4")) {//抄送人 审批人
            if (copytoId == null) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                for (int i1 = 0; i1 < copytoId.size(); i1++) {
                    if (list.get(i).getUid().equals(copytoId.get(i1))) {//如果列表里面有的人已经被选择了 就不能在选择了
                        list.remove(i);//删除这一条数据
                    }
                }
            }
            if (checkerId == null) {//如果是选择抄送人的话就要先判断用户是否已经被选择为审批人  如果已经被选择为审批人 这里就不能展示已选用户
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                for (int i1 = 0; i1 < checkerId.size(); i1++) {
                    if (list.get(i).getUid().equals(checkerId.get(i1))) {//如果列表里面有的人已经被选择了 就不能在选择了
                        list.remove(i);//删除这一条数据
                    }
                }
            }
        } else if (type.equals("5")) {// 执行人
            if (U.isEmpty(discList)) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                for (int i1 = 0; i1 < discList.size(); i1++) {
                    if (list.get(i).getUid().equals(discList.get(i1).getDoerId())) {//如果列表里面有的人已经被选择了 就不能在选择了
                        list.remove(i);//删除这一条数据
                        break;
                    }
                }
            }
        }
    }

    //40 同事列表（完成）
    private void partersList() {
        HttpManager.getInstance().partersList("SelectPersonActivity",
                keyword, new HttpCallBack<BaseDataModel<UserBean>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<UserBean> data) {
                        list.clear();
                        list.addAll(data.getData());
                        adapter.update(list, mType);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SelectPersonActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //41 审批人列表（完成）
    private void checkerList() {
        HttpManager.getInstance().checkerList("SelectPersonActivity",
                keyword, new HttpCallBack<BaseDataModel<UserBean>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<UserBean> data) {
                        list.clear();
                        list.addAll(data.getData());
                        adapter.update(list, mType);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SelectPersonActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setResult() {
        Intent intent = new Intent();
        if (type.equals("3") || type.equals("4") || type.equals("5")) {//工作人员端发审批
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(uid)) {
                U.showToast("请选择同事");
                return;
            }
            intent.putExtra("name", name);
            intent.putExtra("uid", uid);
            intent.putExtra("position", position);
        } else {
            if (TextUtils.isEmpty(pid)) {
                U.showToast("请选择同事");
                return;
            }
            intent.putExtra("pid", pid);
        }
        setResult(1, intent);
        finish();
    }

    private void setResult1() {
        StringBuffer sb = new StringBuffer();
        for (UserBean bean : list) {
            if (bean.isSelect()) {
                sb.append(bean.getCid() + ",");
            }
        }
        if (TextUtils.isEmpty(sb.toString())) {
            U.showToast("请选择审批人");
            return;
        }
        cid = sb.deleteCharAt(sb.length() - 1).toString();
        Intent intent = new Intent();
        intent.putExtra("cid", cid);
        this.setResult(1, intent);
        finish();
    }

    @OnClick({R.id.rl_back_button, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_ok:
                if (SelectAdapter.SelectType.MORE.equals(mType)) setResult1();// 多选
                else setResult();// 单选
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (mType.equals(SelectAdapter.SelectType.RADIO)) {//单选
            for (int i = 0; i < list.size(); i++) {
                if (position == i) {//选中的置为true，其他置为false
                    if (list.get(position).isSelect()) {
                        /*if (type.equals("3")) {//抄送人
                            if (copytoId != null && list.get(i).getUid().equals(copytoId)) {
                                list.get(i).setSelect(true);
                            }
                        } else if (type.equals("4")) {//审批人
                            if (checkerId != null && list.get(i).getUid().equals(checkerId)) {
                                list.get(i).setSelect(true);
                            }
                        } else {*/
                        list.get(position).setSelect(false);
//                        }
                        selectCount = 0;
                    } else {
                        list.get(position).setSelect(true);
                        selectCount = 1;
                        if (type.equals("3") || type.equals("4") || type.equals("5")) {//工作人员端发审批
                            name = list.get(position).getName();
                            uid = list.get(position).getUid();
                            /*if (type.equals("3")) {//抄送人
                                if (copytoId != null && list.get(position).getUid().equals(copytoId)) {
                                    selectCount = 0;
                                }
                            } else if (type.equals("4")) {//审批人
                                if (checkerId != null && list.get(position).getUid().equals(checkerId)) {
                                    selectCount = 0;
                                }
                            }*/
                        } else {
                            pid = list.get(position).getPid();
                        }
                    }
                } else {
                    list.get(i).setSelect(false);
                }
            }
        } else {//多选
            if (list.get(position).isSelect()) {//选中的为false，则置为true，否则置为false
                list.get(position).setSelect(false);
                selectCount--;
            } else {
                list.get(position).setSelect(true);
                selectCount++;
            }
        }
        adapter.update(list, mType);
        if (selectCount > 0) {
            mtvOk.setVisibility(View.VISIBLE);
            //点击item之后不至于被隐藏掉，不能用xrecyclerView，只能用原生RecyclerView
            mxreList.scrollToPosition(position);
        } else {
            mtvOk.setVisibility(View.GONE);
        }
    }

}
