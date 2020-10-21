package com.hnjbkc.jinbao.hqldao.aop;


import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 为springDataJpa 添加Aop 以支持将N+1 转换为 1+1
 * @author xudaz
 * @date 2019/4/1
 */
@Aspect
@Component
public class JpaAop {
    private ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    /**
     * 指定切点
     * 匹配 com.example.demo.controller包及其子包下的所有类的所有方法
     */
    @Pointcut(value = "execution(org.springframework.data.domain.Page com.hnjbkc.jinbao..*Dao.*(..))")
    public void endWithDaoAndReturnPage(){
    }
    /**
     * 指定切点
     * 匹配 com.example.demo.controller包及其子包下的所有类的所有方法
     */
    @Pointcut(value = "execution(org.springframework.data.domain.Page com.hnjbkc.jinbao..*DaoImpl.*(..))")
    public void endWithDaoAndReturnPage4Impl(){
    }

    /**
     * 指定切点
     * 匹配 com.example.demo.controller包及其子包下的所有类的所有方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao..*Dao.*(..)) && !execution(java.util.List com.hnjbkc.jinbao..getSingleProperties(..))")
    public void endWithDaoAndReturnList(){
    }
    /**
     * 指定切点
     * 匹配 com.example.demo.controller包及其子包下的所有类的所有方法
     */
    @Pointcut(value = "execution(* com.hnjbkc.jinbao.base.BaseDaoImpl.*(..)) && !execution(java.util.List com.hnjbkc.jinbao..getSingleProperties(..))")
    public void endWithDaoAndReturnList4Impl(){
    }

    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     * @param pjp p
     * @return o
     */
    @Around("endWithDaoAndReturnPage()   ")
    @SuppressWarnings("unchecked")
    public Object arroundNull(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed  = pjp.proceed();;
        try {
            Page<Object> page = (Page<Object>) proceed;
            List<Object> listTemp = new ArrayList<>(page.getContent());
            List<Object> cascades = manyAndOneToOneAndOne.getCascades(listTemp);
            return new PageImpl<>(cascades, page.getPageable(), page.getTotalElements());
        } catch (Throwable e) {
            e.printStackTrace();
            return new PageImpl(new ArrayList());
        }
    }
    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     * @param pjp p
     * @return o
     */
    @Around("endWithDaoAndReturnList() ")
    @SuppressWarnings("unchecked")
    public Object arroundList(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed1 = pjp.proceed() ;
        try {
            if ( proceed1 instanceof  List ) {
                List proceed = (List) proceed1;
//            List listTemp = new ArrayList( proceed);
                return manyAndOneToOneAndOne.getCascades(proceed);
            }
            //如果是findById
            if ( proceed1 instanceof Optional ) {
                Optional proceed = (Optional) proceed1;
                if (  proceed.isPresent()  ) {
                    manyAndOneToOneAndOne.getCascades(new ArrayList<>(Collections.singletonList(proceed.get()))).get(0)  ;
                }
            }
            //如果是findOne
            String packageName = "com.hnjbkc.jinbao";
            if ( proceed1 != null &&  proceed1.getClass().getName().startsWith(packageName) ) {
                  manyAndOneToOneAndOne.getCascades(new ArrayList<>(Collections.singletonList( proceed1  ) ) ) ;
            }
            return proceed1;
        } catch (Throwable e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //    /**
//     * //        System.out.println("我是前置通告 ==> " + joinPoint);
//     * 前置通知，方法调用前被调用
//     * @param joinPoint joinPoint
//     */
//    @Before("endWithDaoAndReturnPage()")
//    public void doBefore(){
//
//    }
//
//    /**
//     * // 处理完请求，返回内容
//     *         System.out.println("方法的返回值 : " + ret.getClass());
//     * 处理完请求返回内容
//     * @param ret ret
//     */
//    @AfterReturning(returning = "proceed", pointcut = "endWithDaoAndReturnPage()")
//    @SuppressWarnings("unchecked")
//    public void doAfterReturning(Object proceed) {
//        Page<Object> page = (Page<Object>) proceed;
//        List<Object> listTemp = new ArrayList<>(page.getContent());
//        try {
//            List<Object> cascades = manyAndOneToOneAndOne.getCascades(listTemp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new PageImpl<>(cascades, page.getPageable(), page.getTotalElements());
//    }
//
//    /**
//     * 后置异常通知
//     * @param jp jp
//     */
//    @AfterThrowing("endWithDaoAndReturnPage()")
//    public void throwss(JoinPoint jp){
//        System.out.println("方法异常时执行..... ==> " + jp);
//    }
//
//    /**
//     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
//     * @param jp jp
//     */
//    @After("endWithDaoAndReturnPage()")
//    public void after(JoinPoint jp){
//        System.out.println("后置最终通知,final增强，不管是抛出异常或者正常退出都会执行 ==> " + jp);
//    }

}
