package com.zhuangxiuweibao.app.bean;

import java.util.List;

import lombok.Data;

@Data
public class HouseEntity {
    private String houseId;
    private String houseName;
    private String xiaoquId;
    private String xiaoquName;
    private String towerNumber;
    private String cellName;
    private String doorNumber;
    private String houseType;
    private List<zoneInfo> zones;

    @Data
    public class zoneInfo {
        private String zoneId;
        private String zoneName;
        private String area;
        private String direction;
    }

}
