package com.hnjbkc.jinbao.config.entitygraph;

import java.lang.annotation.*;

/**
 * @author xudaz
 */
@Target({ElementType.FIELD , ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyGraphIgnore {

    MyGraphIgnoreInfo []  ignoreFields();

}
