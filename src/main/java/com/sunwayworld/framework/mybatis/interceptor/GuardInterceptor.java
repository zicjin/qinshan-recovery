package com.sunwayworld.framework.mybatis.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SystemUtils;

/**
 * 添加mybatis的警卫功能
 * 
 * @author zhangjr@sunwayworld.com 2022年11月22日
 */
@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class))
public class GuardInterceptor implements Interceptor {
    private static final Logger logger = LogManager.getLogger(GuardInterceptor.class);
    
    private Boolean guard;
    private Integer memoryThreshold;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!isGuard()) {
            return invocation.proceed();
        }
        
        Object[] args = invocation.getArgs();
        
        Statement stmt = (Statement) args[0];
        
        ReflectionUtils.setFieldValue(invocation, "args", new Object[] { newStatementInstance(stmt) });
        
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }
    
    //--------------------------------------------------------------------------------
    // 私有方法
    //--------------------------------------------------------------------------------
    private Boolean isGuard() {
        if (guard == null) {
            synchronized (GuardInterceptor.class) {
                if (guard == null) {
                    guard = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.guard.enabled", Boolean.class, false);
                }
            }
        }
        
        return guard;
    }
    
    private Integer getMemoryThreshold() {
        if (memoryThreshold == null) {
            synchronized (GuardInterceptor.class) {
                if (memoryThreshold == null) {
                    memoryThreshold = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.guard.momery-threshold", Integer.class, 0);
                }
            }
        }
        
        return memoryThreshold;
    }
    
    private Statement newStatementInstance(Statement stmt) {
        InvocationHandler handler = new StatementInvocationHandler(stmt);
        ClassLoader cl = PreparedStatement.class.getClassLoader();
        return (Statement) Proxy.newProxyInstance(cl, new Class[]{Statement.class, PreparedStatement.class, CallableStatement.class}, handler);
    }
    
    private class StatementInvocationHandler implements InvocationHandler {
        private final Statement stmt;
        
        public StatementInvocationHandler(Statement stmt) {
            this.stmt = stmt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getResultSet".equals(method.getName())) {
                ResultSet resultSet = (ResultSet) method.invoke(stmt, args);
                
                return newResultSetInstance(resultSet);
            }
            
            return method.invoke(stmt, args);
        }
    }
    
    private ResultSet newResultSetInstance(ResultSet resultSet) {
        InvocationHandler handler = new ResultSetInvocationHandler(resultSet);
        ClassLoader cl = ResultSet.class.getClassLoader();
        return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
    }
    
    private class ResultSetInvocationHandler implements InvocationHandler {
        private static final int CHECK_MEMORY_INTERVAL = 2000;
        
        private final ResultSet resultSet;
        
        private int qty = 0;
        
        public ResultSetInvocationHandler(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("next".equals(method.getName())) {
                if (++qty % CHECK_MEMORY_INTERVAL == 0) {
                    int memoryThreshold = getMemoryThreshold();
                    
                    int ratio = SystemUtils.getMemoryUsageRatio();
                    
                    if (ratio > memoryThreshold) {
                        logger.warn("内存使用率：" + ratio + "，已超过阈值：" + memoryThreshold);
                        
                        throw new ApplicationWarningException("EXECUTOR.RESOURCE_MONITORING.MEMORY_USAGE_OVERLOAD");
                    }
                }
            }
            
            return method.invoke(resultSet, args);
        }
    }
}
