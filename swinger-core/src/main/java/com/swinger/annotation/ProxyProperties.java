package com.swinger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProxyProperties {
    String include() default "set(.+)";
    String prefix() default "";
    String defaultBindingPrefix() default "";
}
