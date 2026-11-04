/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.sunwayworld.cloud.module.lcdp.base.service.LcdpBaseService;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.utils.SpringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class LcdpScriptSyncListener
implements MessageListener {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String script = new String(message.getBody());
        String className = JavaCompilerManager.getClassName(script);
        String string = className.intern();
        synchronized (string) {
            if (!ApplicationContextHelper.getApplicationContext().containsBean(className.toLowerCase())) {
                Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
                SpringUtils.registerBean(className.toLowerCase(), clazz);
                if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(className.toLowerCase()).getClass())) {
                    LcdpBaseService scriptService = (LcdpBaseService)SpringUtils.getBean(className.toLowerCase());
                    String tableName = scriptService.getTable();
                    RedisHelper.put("TABLE_SCRIPT_MAPPING_CACHE", tableName, className.toLowerCase());
                }
            }
        }
    }
}
