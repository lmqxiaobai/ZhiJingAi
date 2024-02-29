package com.example.zhijingai.demo.aspect;

import com.example.zhijingai.demo.annotation.AutoFill;
import com.example.zhijingai.demo.constant.BaseConstant;
import com.example.zhijingai.demo.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
// 声明切面
@Aspect
// 让容器管理的bean
@Component
// 日志
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点（对哪些类的哪些方法进行拦截）
     * execution(* com.example.zhijingai.mapper.*.*(..))表示拦截在该包下的所有接口和类里面的所有方法
     * @annotation(com.example.zhijingai.demo.annotation.AutoFill) 表示拦截使用过AutoFill自定义注解的方法
     * 两个结合起来就表示拦截该包下面使用过自定义注解的方法
     */
    @Pointcut("execution(* com.example.zhijingai.mapper.*.*(..)) && @annotation(com.example.zhijingai.demo.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值（对代码进行增强的处理）
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //通过反射获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //通过反射获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity = args[0];

        //准备赋值的数据,从保存id的类中获取
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseConstant.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
