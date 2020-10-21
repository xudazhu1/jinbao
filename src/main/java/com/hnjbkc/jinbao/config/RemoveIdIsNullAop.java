package com.hnjbkc.jinbao.config;

import com.hnjbkc.jinbao.utils.MyBeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * 为提交的数据自动产生通知
 *
 * @author xudaz
 * @date 2019/8/12
 */
@Aspect
@Component
public class RemoveIdIsNullAop {


    /**
     * 指定切点
     * 匹配service的所有add方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*ServiceImpl.*add(..))")
    public void add() {
    }

    /**
     * 指定切点
     * 匹配service的所有update方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*ServiceImpl.*update(..))")
    public void update() {
    }

    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     *
     * @param pjp p
     * @return o
     */
    @Around(value = "update() || add()")
    public Object before(ProceedingJoinPoint pjp ) throws Throwable {
        Object[] args = pjp.getArgs();
        if ( args.length == 0 ) {
            return pjp.proceed();
        }
        for (Object arg : args) {
            if (arg.getClass().getName().contains("com.hnjbkc.jinbao")) {
                MyBeanUtils.removeNullField(arg);
            }
        }
        return pjp.proceed(args);
    }


}
