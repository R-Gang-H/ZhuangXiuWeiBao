package com.zhuangxiuweibao.app.bean;

import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.ui.bean.DemandBean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Date: 2019/4/18
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.bean
 */
@Data
public class NeedEntity implements Serializable {

    private String houseId;
    private DemandBean zone;
    private String describe;
    private List<UploadModule> images;
    private List<String> pngOravis;

    private boolean isVoice;
    private float seconds;
    private String filePath;

}
