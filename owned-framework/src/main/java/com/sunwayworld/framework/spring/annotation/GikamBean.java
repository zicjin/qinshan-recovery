package com.sunwayworld.framework.spring.annotation;
import java.lang.annotation.*;
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) @Documented
@org.springframework.context.annotation.Conditional(GikamCondition.class)
public @interface GikamBean { }
