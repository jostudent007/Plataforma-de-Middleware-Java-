package com.projeto2.middleware.annotations;

import com.projeto2.middleware.interceptors.Interceptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InterceptBefore {
    /**
     * Um array de classes de Interceptors que devem ser executados ANTES do método.
     */
    Class<? extends Interceptor>[] value();
}