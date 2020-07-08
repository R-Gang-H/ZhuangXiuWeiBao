package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Date: 2019/4/24
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.bean
 */
@Builder
@Data
public class BatchEntity implements Serializable {


    /**
     * cid : 1
     * title : 财务审批
     * content : 工程项目审批流程包含5步
     * checker : [{"checkerId":"1","checkerName":"张三","checkerIcon":"1.jpg"}]
     * copyto : [{"copierId":"1","copierName":"刘经理","copierIcon":"2.jpg"}]
     */

    private String cid;
    private String bid;
    private String title;
    private String explain;
    private String modifyExplain;
    private String modifyCopy;
    private String isHint;
    private List<CheckerBean> checker;
    private List<CopytoBean> copyto;

    private String tid;
    private List<ItemsBean> items;

    @Data
    public static class CheckerBean implements Serializable {
        /**
         * checkerId : 1
         * checkerName : 张三
         * checkerIcon : 1.jpg
         */

        private String checkerId;
        private String checkerName;
        private String checkerIcon;

    }

    @Data
    public static class CopytoBean implements Serializable {
        /**
         * copierId : 1
         * copierName : 刘经理
         * copierIcon : 2.jpg
         */

        private String copierId;
        private String copierName;
        private String copierIcon;

    }

    @Data
    public static class ItemsBean implements Serializable {
        private String itemName;
        private String itemId;
    }
}
