package com.codingending.packagefairy.utils;

/**
 * 与格式相关的工具类
 * Created by CodingEnding on 2018/4/21.
 */

public class FormatUtils {
    private FormatUtils(){}

    /**
     * 如果源字符串包含空格，就默认返回空格后的后缀字符串
     * 主要用于防止字符串过长影响UI效果
     * @param source 源字符串
     * @return 目标字符串
     */
    public static String getSpacePostfix(String source){
        if(source.contains(" ")){//默认返回空格后的内容（如“阿里巴巴 钉钉”就返回“钉钉”）
            String[] temp=source.split(" ");
            if(temp.length>1){
                return temp[1];
            }
        }
        return source;
    }
}
