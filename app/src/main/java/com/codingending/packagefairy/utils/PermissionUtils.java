package com.codingending.packagefairy.utils;

import android.content.pm.PackageManager;

/**
 * 与权限相关的工具类
 * Created by CodingEnding on 2018/4/10.
 */

public class PermissionUtils {
    private PermissionUtils(){}

    /**
     * 判断是否所有的权限都被同意授予
     * @param grantResults 授权结果
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
