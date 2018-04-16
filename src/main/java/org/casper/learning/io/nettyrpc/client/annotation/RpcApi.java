package org.casper.learning.io.nettyrpc.client.annotation;

import org.casper.learning.io.nettyrpc.client.Callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcApi {

    public String api() default "";
    public String version() default "1";
    public Class<? extends Callback> callback() default Callback.class;
}
