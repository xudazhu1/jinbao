//package com.hnjbkc.jinbao.permission.datarequest;
//
//import com.hnjbkc.jinbao.permission.PermissionBean;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.HashSet;
//import java.util.Set;
//
//
///**
// * @author siliqinag
// * @date  2019.8.6
// */
//@Aspect
//@Component
//public class DataRequestAop {
//
//    /**
//     * 指定切点
//     * 匹配 com.example.demo.controller包及其子包下的所有类的所有方法
//     */
//
//    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*Controller.*(..))")
//    public void endWithController(){
//    }
//
//    /**
//     * 环绕通知,环绕增强，相当于MethodInterceptor
//     *
//     * @param pjp p
//     * @return o
//     */
//    @Around("endWithController()")
//    @SuppressWarnings("unchecked")
//    public Object arroundNull(ProceedingJoinPoint pjp) throws Throwable {
//        Signature signature = pjp.getSignature();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        Method targetMethod = methodSignature.getMethod();
//        //如果是放行的方法
//        if ( targetMethod.getAnnotation(IgnoreRequest.class) != null ) {
//            return pjp.proceed();
//        }
//
//        Class<?> declaringClass = targetMethod.getDeclaringClass();
//        RequestMapping annotation1 = declaringClass.getAnnotation(RequestMapping.class);
//        //类的映射路径
//        // user
//        String tag = annotation1.value()[0];
//
//
//        //方法注解集合
//        if ( targetMethod.getAnnotation(PostMapping.class) != null ) {
//            tag += "[add]";
//        } else  if ( targetMethod.getAnnotation(PutMapping.class) != null ) {
//            tag += "[update]";
//        } else  if ( targetMethod.getAnnotation(DeleteMapping.class) != null ) {
//            tag += "[delete]";
//        }
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        assert servletRequestAttributes != null;
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//
//        //直接向数据库拿当前clazz有没有对应注解的权限 有就放行 没有就拦截
//        Set<PermissionBean> permissionBeans = (HashSet<PermissionBean>)request.getSession().getAttribute("permissionBeans");
//
//        for (PermissionBean permissionBean : permissionBeans) {
//            if ( tag.equals(permissionBean.getPermissionTag()) ) {
//                return pjp.proceed();
//            }
//        }
//        throw new Exception("对不起 没有权限");
//    }
//}
//
