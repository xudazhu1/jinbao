package com.hnjbkc.jinbao.permission.datarequest;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreRequest {
}
