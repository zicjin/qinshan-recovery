/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.cache;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CoreStudioCacheConfiguration {
    @Bean(value={"studioAdvancedConfigKeyGenerator"})
    public KeyGenerator studioAdvancedConfigKeyGenerator() {
        return new KeyGenerator(){

            @Override
            public Object generate(Object target, Method method, Object ... params) {
                if (params.length == 2) {
                    CoreStudioAdvSearchConfigBean oldItem = (CoreStudioAdvSearchConfigBean)params[0];
                    CoreStudioAdvSearchConfigBean newItem = (CoreStudioAdvSearchConfigBean)params[1];
                    return CoreStudioCacheConfiguration.getRedisKey(oldItem, newItem);
                }
                if (params.length == 1) {
                    String url = (String)params[0];
                    return CoreStudioCacheConfiguration.getLoginUserId() + "." + url;
                }
                throw new ApplicationRuntimeException("You should not be here!", new String[0]);
            }
        };
    }

    public static String getRedisKey(CoreStudioAdvSearchConfigBean oldItem, CoreStudioAdvSearchConfigBean newItem) {
        StringBuilder keyBuilder = new StringBuilder(CoreStudioCacheConfiguration.getLoginUserId()).append(".");
        if (oldItem == null) {
            keyBuilder.append(newItem.getUrl());
        } else {
            keyBuilder.append(oldItem.getUrl());
        }
        return keyBuilder.toString();
    }

    private static String getLoginUserId() {
        return Optional.ofNullable(LocalContextHelper.getLoginUserId()).orElse("-");
    }
}
