package com.example.zhijingai.demo.constant;

/**
 *@CrossOrigin注解是Spring框架中用于处理跨域请求的一个重要注解，主要用于解决浏览器的同源策略限制问题。
 * 在前后端分离开发中，前端应用（如React、Vue等）和后端API服务不在同一域名下时，就需要处理跨域问题。
 * 此处统一封装的是允许访问的ip域名
 */
public class IpAddressConstant {
    // 开放的IP地址（允许访问的IP地址）
    public static final String IPADDRESS_OPEN = "*";
}
