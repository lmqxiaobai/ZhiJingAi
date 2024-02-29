package com.example.zhijingai.demo.constant;
/**
 * 在登录成功后将登录者的id从jwt令牌中取出来存入线程内，然后在需要的地方从线程内取出来。
 * 为什么要这么做？
 * 因为如果有多个用户在同时进行操作（同时设置用户id和获取用户id导致数据不一致），会存在线程安全问题（信息共享，覆盖）产生的多线程问题
 * 这个时候就可以将这种操作设置为线程，多个用户多个线程，保证数据隔离
 */
public class BaseConstant {
    // 创建一个存储long类型值的线程对象
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    // 使用ThreadLocal方法将数据存入到线程内
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    // 使用ThreadLocal方法将数据从线程内取出
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    // 移出数据
    public static void removeCurrentId(){
        threadLocal.remove();
    }
}
