package com.pkpm.annotation;

import java.lang.annotation.*;

/**
 * 封装controller可以post方式 接收单个参数
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJson {
    String value();
}

