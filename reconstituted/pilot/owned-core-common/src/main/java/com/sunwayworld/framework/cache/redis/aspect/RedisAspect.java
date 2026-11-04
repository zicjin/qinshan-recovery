/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.redis.aspect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.Duration;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name={"sunway.testing.stress.enabled"}, havingValue="false", matchIfMissing=true)
public class RedisAspect {
    public static final String PERMANENT_NAME_PREFIX = "PERM_";

    @Around(value="within(org.springframework.data.redis.cache.DefaultRedisCacheWriter)")
    public Object doRedisOperation(ProceedingJoinPoint pjp) throws Throwable {
        long expiredSec;
        String value;
        Duration ttl;
        Object[] args;
        String name;
        if ("DefaultRedisCacheWriter.put(..)".contains(pjp.getSignature().toShortString()) && !StringUtils.startsWith(name = (String)(args = pjp.getArgs())[0], PERMANENT_NAME_PREFIX) && ((ttl = (Duration)args[3]) == null || ttl.isZero()) && !StringUtils.isBlank(value = ApplicationContextHelper.getConstantValue("REDIS_MAX_EXPIRED_SEC")) && (expiredSec = NumberUtils.parseLong(value)) > 0L) {
            args[3] = Duration.ofSeconds(expiredSec);
            return pjp.proceed(args);
        }
        return pjp.proceed();
    }
}
