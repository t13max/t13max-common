package com.t13max.common.ioc.annotaion;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

    String value();
}
