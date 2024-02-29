package com.example.zhijingai.demo.interceptor;

import com.example.zhijingai.demo.constant.BaseConstant;
import com.example.zhijingai.demo.properties.JwtProperties;
import com.example.zhijingai.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器,解析jwt，如果解析成功则通过（并将用户登录id保存到线程中）
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    // 去依赖注入jwt的相关配置
    @Autowired
    JwtProperties jwtProperties;

    /**
     * 校验jwt
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        // 判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 1.从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // 2.校验令牌
        try {

            // 调用jwt工具类去解析jwt令牌,然后通过原本存入的用户名获取id，将其存入到线程内
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(),token);
            // 根据在登录过后保存claims的map键值对信息去获取id
            Long empId = Long.valueOf(claims.get("userId").toString());
            // 将从jwt里面获取的id存入到线程内,这样后面有地方需要用到登录用户的id时就可以直接从线程里面调用
            // 为啥写进线程内？让id具有隔离性，当多个用户同时登录的时候不会产生数据重叠
            BaseConstant.setCurrentId(empId);

            // 3.通过，放行
            return true;
        }catch (Exception ex){
            // 4.不通过，响应401状态
            response.setStatus(401);
            return false;
        }
    }
}
