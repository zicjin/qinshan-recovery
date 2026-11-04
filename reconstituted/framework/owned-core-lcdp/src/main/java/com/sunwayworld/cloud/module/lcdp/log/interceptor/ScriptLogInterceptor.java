/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.log.interceptor;

import com.sunwayworld.cloud.module.lcdp.log.ScriptLogWebSocket;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.log.MybatisLogger;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts(value={@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class}), @Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}), @Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ScriptLogInterceptor
implements Interceptor {
    public static final ThreadLocal<Boolean> MYBATIS_MESSAGE_ENABLE_HOLDER = new ThreadLocal();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!Boolean.TRUE.equals(MYBATIS_MESSAGE_ENABLE_HOLDER.get())) {
            return invocation.proceed();
        }
        Object[] args = invocation.getArgs();
        MappedStatement stat = (MappedStatement)args[0];
        String mapperId = stat.getId();
        mapperId = mapperId.replace("$DEV", "");
        int dotSecondLastIndex = mapperId.substring(0, mapperId.lastIndexOf(".")).lastIndexOf(".") + 1;
        String simpleSignature = mapperId.substring(dotSecondLastIndex) + " ";
        ScriptLogWebSocket console = ApplicationContextHelper.getBean(ScriptLogWebSocket.class);
        MybatisLogger.addConsumer(message -> console.pushInfoLog(simpleSignature + message));
        long now = System.currentTimeMillis();
        Object result = invocation.proceed();
        long duration = System.currentTimeMillis() - now;
        double timeConsuming = (double)duration / 1000.0;
        console.pushInfoLog(simpleSignature + "<==      TimeConsuming: " + timeConsuming + "s");
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }

    public static void enable() {
        MYBATIS_MESSAGE_ENABLE_HOLDER.set(true);
    }

    public static void disable() {
        MYBATIS_MESSAGE_ENABLE_HOLDER.set(false);
    }
}
