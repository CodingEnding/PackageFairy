package com.codingending.packagefairy.utils;

import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 与加密相关的工具类
 * Created by CodingEnding on 2018/4/9.
 */

public class EncryptUtils {
    private EncryptUtils(){}

    //十六进制下数字到字符的映射数组
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    //转换字节数组为16进制字串
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    //获取文件的md5值
    public static String md5File(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            String md5Str = toHexString(md5.digest());
            return TextUtils.isEmpty(md5Str) ? "" : md5Str;
        } catch (Exception e) {
            System.out.println("error");
            return "";
        }
    }

    //获取字符串的md5
    public static String md5String(String originString) {
        if (originString != null){
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] results = md.digest(originString.getBytes());
                String resultString = toHexString(results);
                return TextUtils.isEmpty(resultString) ? "" :resultString.toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //对字符串进行SHA-256加密
    public static String sha256String(String originString){
        MessageDigest messageDigest;
        String encodeStr="";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(originString.getBytes("UTF-8"));
            encodeStr = toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

}
