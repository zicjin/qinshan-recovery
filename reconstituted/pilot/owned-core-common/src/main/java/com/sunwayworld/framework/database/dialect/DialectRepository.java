/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.List;

public class DialectRepository {
    private static final boolean kingbase = ApplicationContextHelper.getEnvironment().getProperty("spring.datasource.driver-class-name").contains("kingbase");
    private static final boolean dameng = ApplicationContextHelper.getEnvironment().getProperty("spring.datasource.driver-class-name").contains("DmDriver");
    private static volatile Dialect dialect;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Dialect getDialect() {
        if (dialect != null) return dialect;
        Class<DialectRepository> clazz = DialectRepository.class;
        synchronized (DialectRepository.class) {
            if (dialect != null) return dialect;
            List<Class<?>> dialectClassList = ClassUtils.getPredicatedClasses("com.sunwayworld", c -> !c.isInterface() && Dialect.class.isAssignableFrom((Class<?>)c));
            for (Class<?> dialectClass : dialectClassList) {
                Dialect dialect = (Dialect)ClassUtils.newInstance(dialectClass);
                if (!ApplicationContextHelper.isProfileActivated(dialect.getDatabase())) continue;
                DialectRepository.dialect = dialect;
                break;
            }
            if (dialect != null) return dialect;
            dialect = new OracleDialect();
            // ** MonitorExit[var0] (shouldn't be in output)
            return dialect;
        }
    }

    public static boolean isKingbase() {
        return kingbase;
    }

    public static boolean isDameng() {
        return dameng;
    }

    public static void setDialect(Dialect dialect) {
        DialectRepository.dialect = dialect;
    }
}
