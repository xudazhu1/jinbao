package com.hnjbkc.jinbao.config.entitygraph;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyGraphIgnoreInfo {

    String fieldPath() default "";

    /**
     * 实体图排除的类型
     * @return b
     */
    MyGraphIgnoreInfoType fetchType() default MyGraphIgnoreInfoType.ONLY_SELF;

    String[] fieldList() default {};

}

