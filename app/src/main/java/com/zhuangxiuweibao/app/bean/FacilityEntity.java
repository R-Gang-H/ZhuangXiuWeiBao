package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class FacilityEntity implements Serializable {

    private String locId;
    private String locationName;
    private String unit;
    private String isDone;

    private String srcid;
    private String name;
    private String type;
    private String value;

}
