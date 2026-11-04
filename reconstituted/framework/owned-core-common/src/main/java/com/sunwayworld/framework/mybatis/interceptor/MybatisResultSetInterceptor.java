/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.interceptor;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.mybatis.interceptor.MybatisColumnTypeHolder;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Intercepts(value={@Signature(type=ResultSetHandler.class, method="handleResultSets", args={Statement.class}), @Signature(type=ResultSetHandler.class, method="handleCursorResultSets", args={Statement.class})})
public class MybatisResultSetInterceptor
implements Interceptor {
    private static final Logger logger = LogManager.getLogger(MybatisResultSetInterceptor.class);
    private Boolean guard;
    private Integer memoryThreshold;
    private Integer resultThreshold;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Statement stmt = (Statement)args[0];
        ReflectionUtils.setFieldValue(invocation, "args", new Object[]{this.newStatementInstance(stmt)});
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Boolean isGuard() {
        if (this.guard != null) return this.guard;
        Class<MybatisResultSetInterceptor> clazz = MybatisResultSetInterceptor.class;
        synchronized (MybatisResultSetInterceptor.class) {
            if (this.guard != null) return this.guard;
            this.guard = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.guard.enabled", Boolean.class, false);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.guard;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Integer getMemoryThreshold() {
        if (this.memoryThreshold != null) return this.memoryThreshold;
        Class<MybatisResultSetInterceptor> clazz = MybatisResultSetInterceptor.class;
        synchronized (MybatisResultSetInterceptor.class) {
            if (this.memoryThreshold != null) return this.memoryThreshold;
            this.memoryThreshold = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.guard.momery-threshold", Integer.class, 0);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.memoryThreshold;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Integer getResultThreshold() {
        if (this.resultThreshold != null) return this.resultThreshold;
        Class<MybatisResultSetInterceptor> clazz = MybatisResultSetInterceptor.class;
        synchronized (MybatisResultSetInterceptor.class) {
            if (this.resultThreshold != null) return this.resultThreshold;
            this.resultThreshold = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.guard.result-threshold", Integer.class, 0);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.resultThreshold;
        }
    }

    private Statement newStatementInstance(Statement stmt) {
        StatementInvocationHandler handler = new StatementInvocationHandler(stmt);
        ClassLoader cl = PreparedStatement.class.getClassLoader();
        return (Statement)Proxy.newProxyInstance(cl, new Class[]{Statement.class, PreparedStatement.class, CallableStatement.class}, handler);
    }

    private ResultSet newResultSetInstance(ResultSet resultSet) {
        ResultSetInvocationHandler handler = new ResultSetInvocationHandler(resultSet);
        ClassLoader cl = ResultSet.class.getClassLoader();
        return (ResultSet)Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
    }

    private class ResultSetInvocationHandler
    implements InvocationHandler {
        private static final int CHECK_MEMORY_INTERVAL = 100;
        private final ResultSet resultSet;
        private int qty = 0;

        public ResultSetInvocationHandler(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("next".equals(method.getName())) {
                ++this.qty;
                int resultThreshold = MybatisResultSetInterceptor.this.getResultThreshold();
                if (resultThreshold > 0 && this.qty > resultThreshold) {
                    throw new ApplicationWarningException("\u6570\u636e\u67e5\u8be2\u6570\u91cf\u5df2\u8d85\u8fc7\u9608\u503c\uff1a" + resultThreshold, new String[0]);
                }
                if (this.qty % 100 == 0) {
                    int memoryThreshold = MybatisResultSetInterceptor.this.getMemoryThreshold();
                    int ratio = SystemUtils.getMemoryUsageRatio();
                    if (ratio > memoryThreshold) {
                        logger.warn("\u5185\u5b58\u4f7f\u7528\u7387\uff1a" + ratio + "\uff0c\u5df2\u8d85\u8fc7\u9608\u503c\uff1a" + memoryThreshold);
                        throw new ApplicationWarningException("EXECUTOR.RESOURCE_MONITORING.MEMORY_USAGE_OVERLOAD", new String[0]);
                    }
                }
            }
            if ("getClob".equals(method.getName())) {
                Clob clob = (Clob)method.invoke(this.resultSet, args);
                if (clob == null) {
                    return null;
                }
                String value = ConvertUtils.convert(clob, String.class);
                return ConvertUtils.convert(value, Clob.class);
            }
            if ("getObject".equals(method.getName())) {
                Object object = method.invoke(this.resultSet, args);
                if (object == null) {
                    return null;
                }
                if (object instanceof Clob) {
                    Clob clob = (Clob)object;
                    String value = ConvertUtils.convert(clob, String.class);
                    return ConvertUtils.convert(value, Clob.class);
                }
                if (object instanceof String) {
                    String value = (String)object;
                    return value;
                }
                return object;
            }
            if ("getNString".equals(method.getName()) || "getString".equals(method.getName())) {
                String value = (String)method.invoke(this.resultSet, args);
                return value;
            }
            return method.invoke(this.resultSet, args);
        }
    }

    private class StatementInvocationHandler
    implements InvocationHandler {
        private final Statement stmt;

        public StatementInvocationHandler(Statement stmt) {
            this.stmt = stmt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getResultSet".equals(method.getName())) {
                ResultSet resultSet = (ResultSet)method.invoke(this.stmt, args);
                HashMap columnTypeMap = new HashMap();
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int qty = rsmd.getColumnCount();
                for (int i = 1; i <= qty; ++i) {
                    String columnName = rsmd.getColumnLabel(i);
                    String dataType = rsmd.getColumnTypeName(i);
                    int scale = rsmd.getScale(i);
                    columnTypeMap.put(columnName, DialectRepository.getDialect().getJavaType(columnName, dataType, scale));
                }
                MybatisColumnTypeHolder.setColumnTypeMap(columnTypeMap);
                if (!MybatisResultSetInterceptor.this.isGuard().booleanValue()) {
                    return resultSet;
                }
                return MybatisResultSetInterceptor.this.newResultSetInstance(resultSet);
            }
            return method.invoke(this.stmt, args);
        }
    }
}
