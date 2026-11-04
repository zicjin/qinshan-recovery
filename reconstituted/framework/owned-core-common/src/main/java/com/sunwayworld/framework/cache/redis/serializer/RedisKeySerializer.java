/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.redis.serializer;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

public class RedisKeySerializer
extends StringRedisSerializer {
    private String namespace = ApplicationContextHelper.getEnvironment().getProperty("spring.redis.namespace", "");
    private boolean tenant = ApplicationContextHelper.isProfileActivated("tenant");

    @Override
    public String deserialize(@Nullable byte[] bytes) {
        String text = super.deserialize(bytes);
        if (text == null) {
            return null;
        }
        if (!StringUtils.isBlank(this.namespace)) {
            text = StringUtils.removeStart(text, this.namespace);
        }
        if (this.tenant) {
            return TenantContext.removeTenantCacheNameSuffix(text);
        }
        return text;
    }

    @Override
    public byte[] serialize(@Nullable String string) {
        if (string != null) {
            string = this.namespace + string;
            if (this.tenant) {
                if (TenantContext.endsWithAnyTenantCacheNameSuffix(string)) {
                    return super.serialize(string);
                }
                return super.serialize(string + TenantContext.getTenantCacheNameSuffix());
            }
            return super.serialize(string);
        }
        return null;
    }
}
