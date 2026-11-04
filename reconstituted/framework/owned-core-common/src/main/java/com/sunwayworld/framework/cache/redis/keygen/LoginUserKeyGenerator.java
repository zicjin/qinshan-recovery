/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.redis.keygen;

import com.sunwayworld.framework.context.LocalContextHelper;
import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class LoginUserKeyGenerator
implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object ... params) {
        return LocalContextHelper.getLoginUserId();
    }
}
