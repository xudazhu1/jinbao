package com.hnjbkc.jinbao.permission.dataascriptionannotation;

import java.lang.annotation.*;

/**
 * 创建时间注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreateTime {
}
