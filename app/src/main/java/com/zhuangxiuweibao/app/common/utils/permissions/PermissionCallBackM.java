package com.zhuangxiuweibao.app.common.utils.permissions;

/**
 * author: haoruigang
 * date: on 2018-4-3 10:09:48
 * description:
 */
public interface PermissionCallBackM {
    void onPermissionGrantedM(int requestCode, String... perms);

    void onPermissionDeniedM(int requestCode, String... perms);
}
