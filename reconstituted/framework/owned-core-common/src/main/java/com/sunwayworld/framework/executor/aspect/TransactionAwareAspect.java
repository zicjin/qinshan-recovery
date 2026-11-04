/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor.aspect;

import java.sql.Connection;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAwareAspect {
    private static ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal();

    @Around(value="execution(* javax.sql.DataSource.getConnection(..))")
    public Object aspectConnection(ProceedingJoinPoint pjp) throws Throwable {
        Connection connection = CONNECTION_HOLDER.get();
        if (connection == null) {
            return pjp.proceed();
        }
        return connection;
    }

    public static void setLocalConnection(Connection connection) {
        CONNECTION_HOLDER.set(connection);
    }

    public static void removeLocalConnection() {
        CONNECTION_HOLDER.remove();
    }
}
