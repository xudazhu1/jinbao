package com.hnjbkc.jinbao.hqldao.annotations;

import java.lang.annotation.*;

/**
 * HasOneToManyList 的 属性注解类 String表示属性名 class表示list的泛型
 * @see HasOneToManyList
 * @author xudaz
 * @date 2019/4/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OneToManyListInfo {

    /**
     *
     * @return beans|beansList 的属性名
     */
    String propertyName() default "";

    /**
     *
     * @return beans|beansList 的list的泛型
     */
    Class propertyClass() default void.class;
}
