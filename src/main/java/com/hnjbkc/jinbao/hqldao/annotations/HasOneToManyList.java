package com.hnjbkc.jinbao.hqldao.annotations;


import java.lang.annotation.*;

/**
 * 是否包含一对多list属性的注解 用于处理 jpa N+1的问题
 * @author xudaz
 * @date 2019/4/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasOneToManyList {
    /**
     *
     * @return OneToManyListInfo beans|beanList的详细信息
     */
    OneToManyListInfo[] hasClasses() default {};
}
