package com.sunwayworld.framework.log.annotation;
import java.lang.annotation.*;
import org.apache.logging.log4j.spi.StandardLevel;
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) @Inherited
public @interface Log { String value(); LogType type() default LogType.DEFAULT; StandardLevel level() default StandardLevel.INFO; }
