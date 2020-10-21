package com.hnjbkc.jinbao.permission.dataascriptionannotation;

import java.lang.annotation.*;

/**
 * 数据归属职位
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BelongJob {
}
