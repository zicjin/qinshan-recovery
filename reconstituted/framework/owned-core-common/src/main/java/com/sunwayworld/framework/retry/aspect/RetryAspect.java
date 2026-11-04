/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.retry.aspect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.retry.annotation.Retry;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Aspect
@Component
public class RetryAspect {
    private static final Logger logger = LogManager.getLogger(RetryAspect.class);

    @Around(value="@annotation(retry)")
    public Object handleResourceException(ProceedingJoinPoint pjp, Retry retry) throws Throwable {
        int maxAttempts = NumberUtils.isNumber(retry.maxAttempts()) ? NumberUtils.parseInt(retry.maxAttempts()) : NumberUtils.parseInt(ApplicationContextHelper.getConstantValue(retry.maxAttempts()), 1);
        TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        for (int i = 0; i < maxAttempts; ++i) {
            Object savepoint = null;
            try {
                savepoint = transactionStatus.createSavepoint();
                return pjp.proceed();
            }
            catch (Throwable throwable) {
                if (savepoint != null) {
                    transactionStatus.rollbackToSavepoint(savepoint);
                }
                logger.error(throwable.getMessage(), throwable);
                Class<? extends Throwable>[] noRetryFor = retry.noRetryFor();
                if (Arrays.stream(noRetryFor).anyMatch(t -> t.isAssignableFrom(throwable.getClass()))) {
                    throw throwable;
                }
                Class<? extends Throwable>[] retryFor = retry.retryFor();
                if (retryFor.length <= 0 || Arrays.stream(retryFor).anyMatch(t -> t.isAssignableFrom(throwable.getClass()))) continue;
                throw throwable;
            }
        }
        return pjp.proceed();
    }
}
