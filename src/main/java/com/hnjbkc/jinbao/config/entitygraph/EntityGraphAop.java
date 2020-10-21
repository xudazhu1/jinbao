package com.hnjbkc.jinbao.config.entitygraph;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.Map;

/**
 * 为dao的自定义方法动态修改实体图注解的name值(默认的不对)
 *
 * @author xudaz
 * @date 2019/8/12
 */
@Aspect
@Component
public class EntityGraphAop {


    /**
     * 指定切点
     * 匹配service的所有add方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*Dao.*(..))")
    public void method() {
    }



    /**
     * //        System.out.println("我是前置通告 ==> " + joinPoint);
     * 前置通知，方法调用前被调用
     * @param joinPoint joinPoint
     */
    @SuppressWarnings("unchecked")
    @Before("method()")
    public void doBefore( JoinPoint joinPoint ) throws NoSuchFieldException, IllegalAccessException {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();


        EntityGraph entityGraph = targetMethod.getAnnotation(EntityGraph.class);
        if ( entityGraph != null ) {

            //获取泛型
            Class<?> declaringClass = targetMethod.getDeclaringClass();
            // 获得field的泛型类型
            Type[] genericInterfaces = declaringClass.getGenericInterfaces();
            // 就把它转换成ParameterizedType对象
            ParameterizedType pType = (ParameterizedType)genericInterfaces[0];
            // 获得泛型类型的泛型参数（实际类型参数)
            Type[] tArgs = pType.getActualTypeArguments();
            //类路径
            String classPath = tArgs[0].getTypeName();

            InvocationHandler h = Proxy.getInvocationHandler(entityGraph);
            Field memberValues = h.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible( true );
            Map map  = (Map) memberValues.get( h );
            map.put( "value" , classPath.substring( classPath.lastIndexOf( ".") + 1 ) );
        }
        //如果是放行的方法

    }

}
