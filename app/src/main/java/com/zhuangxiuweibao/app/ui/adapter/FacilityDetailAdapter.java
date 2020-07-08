package com.zhuangxiuweibao.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.FacilityEntity;
import com.zhuangxiuweibao.app.bean.WriteSrcEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FacilityDetailAdapter extends XrecyclerAdapter {

    List<FacilityEntity> facilityList = new ArrayList<>();
    @BindView(R.id.tv_loc_name)
    TextView tvLocName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.line)
    View line;

    public List<WriteSrcEntity> writeSrcs = new ArrayList<>();
    TimePickerView pvTime;
    private String time;

    public FacilityDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        FacilityEntity entity = facilityList.get(position);
        tvLocName.setText(entity.getName());
        etName.setFocusable(true);
        int inputType = 0;
        String etHint = "请输入";
        //1：文本 2：数字3：日期
        switch (entity.getType()) {
            case "text":
                inputType = InputType.TYPE_CLASS_TEXT;
                break;
            case "num":
                inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                break;
            case "date":
                inputType = InputType.TYPE_CLASS_DATETIME;
                etName.setFocusable(false);
                etHint = "请选择";
                break;
        }
        etName.setHint(etHint);
        etName.setInputType(inputType);
        etName.setOnClickListener(v -> {
            if (entity.getType().equals("date")) {
                setTimePicker();
                U.hideKeyboard((Activity) mContext);
            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                writeSrcs.get(position).setValue(s.toString());
            }
        });
        etName.setText(entity.getValue());
        line.setVisibility(facilityList.size() - 1 == position ? View.GONE : View.VISIBLE);
    }

    /**
     * 时间选择器
     */
    private void setTimePicker() {
        //时间选择器
        if (pvTime == null) pvTime = new TimePickerBuilder(mContext, (date, v) -> {
            time = date.getTime() / 1000 + "";
            etName.setText(DateUtils.getDateToString(date.getTime(), "yyyy-MM-dd"));
        }).build();
        pvTime.show();
    }

    public void update(List<FacilityEntity> data) {
        this.facilityList.clear();
        this.facilityList.addAll(data);
        notifyDataSetChanged();
        initData();
    }

    private void initData() {
        writeSrcs.clear();
        for (int i = 0; i < facilityList.size(); i++) {
            FacilityEntity entity = facilityList.get(i);
            writeSrcs.add(WriteSrcEntity.builder().srcid(entity.getSrcid()).value("").build());
        }
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_facility_row;
    }
}
