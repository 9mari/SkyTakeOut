package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.* (..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){}

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinpoint){

        //获取方法签名，method是signature的实现类提供了更多详细的方法
        MethodSignature signature = (MethodSignature) joinpoint.getSignature();
        //获取方法上的注解
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //获取方法的入参否则无法修改
        Object[] args = joinpoint.getArgs();
        Object arg = args[0];
        //准备好参数
        Long id = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();


        if (annotation.value() == OperationType.UPDATE){
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,id);
                setCreateTime.invoke(arg,now);
                setCreateUser.invoke(arg,id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
