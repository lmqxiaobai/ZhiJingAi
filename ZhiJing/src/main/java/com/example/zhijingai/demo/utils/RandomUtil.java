package com.example.zhijingai.demo.utils;

/**
 * 随机生成工具类
 */
public class RandomUtil {
    /**
     * 随机生成6位数字
     * @return
     */
    public static String randomCode(){
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < 6;i++){
            code.append((int)(Math.random()*10));
        }
        return code.toString();
    }
}
