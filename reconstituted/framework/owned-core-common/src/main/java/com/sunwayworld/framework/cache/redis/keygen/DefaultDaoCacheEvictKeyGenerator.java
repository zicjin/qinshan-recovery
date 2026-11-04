/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.redis.keygen;

import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class DefaultDaoCacheEvictKeyGenerator
implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object ... params) {
        Persistable oldItem = (Persistable)params[0];
        if (oldItem != null) {
            return ConvertUtils.convert(oldItem.getId(), String.class);
        }
        return ConvertUtils.convert(((Persistable)params[1]).getId(), String.class);
    }
}
