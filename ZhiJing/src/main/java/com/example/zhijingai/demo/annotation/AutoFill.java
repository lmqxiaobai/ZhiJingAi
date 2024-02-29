package com.example.zhijingai.demo.annotation;

import com.example.zhijingai.demo.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行字段自动填充处理
 * 可以使用此注解来声明该方法的数据库操作类型，这样在AOP操作
 * 的时候获取到该注解，通过反射可以得到该注解的内容
 */
// 要求注解只能注解在方法上面
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 声明注解的类型（我们定义的数据库操作类型：UPDATE INSERT）
    OperationType value();
}
