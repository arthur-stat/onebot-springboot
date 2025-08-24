package com.arth.bot.authorization.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectAuthInterceptors {
    DirectAuthInterceptor[] value();
}