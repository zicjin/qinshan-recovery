/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.data.aspect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value=200)
public class DataValidatorAspect {
    @Around(value="@annotation(validateWith)")
    public Object doValidate(ProceedingJoinPoint pjp, ValidateDataWith validateWith) throws Throwable {
        Object[] args = pjp.getArgs();
        DataValidator validator = ApplicationContextHelper.getBean(validateWith.value());
        if (!validator.isValid(args)) {
            return null;
        }
        return pjp.proceed();
    }
}
