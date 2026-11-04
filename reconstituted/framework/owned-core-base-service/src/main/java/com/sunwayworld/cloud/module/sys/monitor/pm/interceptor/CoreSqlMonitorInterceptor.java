/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.interceptor;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.cloud.module.sys.monitor.pm.service.CoreSystemMonitorSqlService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.log.MybatisLogger;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
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
public class CoreSqlMonitorInterceptor
implements Interceptor {
    private static long longQueryTime = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.long-query-time", Long.TYPE, 0L);
    private static final ThreadLocal<StringBuilder> MYBATIS_MESSAGE_HOLDER = new ThreadLocal();
    private static String sqlMonitorMapperId = "com.sunwayworld.cloud.module.sys.monitor.pm.persistent.mapper.CoreSystemMonitorSqlMapper.selectByCondition";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            MappedStatement ms = (MappedStatement)invocation.getArgs()[0];
            if (sqlMonitorMapperId.equals(ms.getId())) {
                Object object = invocation.proceed();
                return object;
            }
            MybatisLogger.addConsumer(m -> CoreSqlMonitorInterceptor.offerMybatisMessage(m));
            long now = System.currentTimeMillis();
            LocalDateTime executetime = LocalDateTime.now();
            Object result = invocation.proceed();
            long duration = System.currentTimeMillis() - now;
            if (longQueryTime > 0L && duration >= longQueryTime && MYBATIS_MESSAGE_HOLDER.get() != null) {
                String mybatisLog = MYBATIS_MESSAGE_HOLDER.get().toString().replaceAll("==> ", "").replaceAll("<== ", "");
                int sqlEndIdx = mybatisLog.indexOf("Parameters");
                int sqlStartIdx = mybatisLog.indexOf("Preparing") + 11;
                String rawSql = sqlEndIdx == -1 ? mybatisLog.substring(sqlStartIdx) : mybatisLog.substring(sqlStartIdx, sqlEndIdx);
                if (StringUtils.contains(rawSql, "T_CORE_SYSTEM_MONITOR_SQL") || StringUtils.contains(rawSql, "T_CORE_I18N_MESSAGE")) {
                    Object object = result;
                    return object;
                }
                Double timeConsuming = (double)duration / 1000.0;
                CoreSystemMonitorSqlService service = ApplicationContextHelper.getBean(CoreSystemMonitorSqlService.class);
                List coreSystemMonitorSqlBeanList = service.selectListByFilter(SearchFilter.instance().match("SQLSOURCE", ms.getId()).filter(MatchPattern.SEQ), new Order[0]);
                if (coreSystemMonitorSqlBeanList.size() == 0) {
                    CoreSystemMonitorSqlBean coreSystemMonitorSqlBean = new CoreSystemMonitorSqlBean();
                    String[] sqlParamPlaceholders = rawSql.split("\\?");
                    if (sqlParamPlaceholders.length > 0) {
                        StringBuilder replaceParamSql = new StringBuilder();
                        int beginIndex = sqlEndIdx + 12;
                        String sqlParam = mybatisLog.substring(beginIndex, mybatisLog.indexOf("\n", beginIndex)).replace(" ", "");
                        if (!StringUtils.isEmpty(sqlParam)) {
                            String[] sqlParams = sqlParam.split(",");
                            for (int i = 0; i < sqlParamPlaceholders.length; ++i) {
                                if (i < sqlParams.length) {
                                    if (!StringUtils.isEmpty(sqlParams[i])) {
                                        int leftIdx = sqlParams[i].indexOf("(");
                                        int rightIdx = sqlParams[i].indexOf(")");
                                        if (leftIdx > -1 && rightIdx > -1 && "String".equals(sqlParams[i].substring(leftIdx + 1, rightIdx))) {
                                            sqlParams[i] = "'" + sqlParams[i].substring(0, leftIdx) + "'";
                                        }
                                    }
                                    replaceParamSql.append(sqlParamPlaceholders[i]).append(sqlParams[i]);
                                    continue;
                                }
                                replaceParamSql.append(sqlParamPlaceholders[i]);
                            }
                            coreSystemMonitorSqlBean.setSql(replaceParamSql.toString());
                        } else {
                            coreSystemMonitorSqlBean.setSql(rawSql);
                        }
                    } else {
                        coreSystemMonitorSqlBean.setSql(rawSql);
                    }
                    coreSystemMonitorSqlBean.setSqlSource(ms.getId());
                    coreSystemMonitorSqlBean.setExecuteTime(executetime);
                    coreSystemMonitorSqlBean.setSqlTimes(1);
                    coreSystemMonitorSqlBean.setTimeConsuming(timeConsuming);
                    if (!LocalContextHelper.isUserLogin()) {
                        coreSystemMonitorSqlBean.setTriggerCategory("\u7cfb\u7edf");
                    } else {
                        coreSystemMonitorSqlBean.setTriggerCategory("\u7528\u6237(" + LocalContextHelper.getLoginUserName() + ")");
                    }
                    service.insert(coreSystemMonitorSqlBean);
                } else {
                    CoreSystemMonitorSqlBean oldSqlMonitorBean = (CoreSystemMonitorSqlBean)coreSystemMonitorSqlBeanList.get(0);
                    if ("1".equals(oldSqlMonitorBean.getRevisionFlag())) {
                        Object object = result;
                        return object;
                    }
                    oldSqlMonitorBean.setExecuteTime(executetime);
                    oldSqlMonitorBean.setTimeConsuming(timeConsuming);
                    oldSqlMonitorBean.setSqlTimes(oldSqlMonitorBean.getSqlTimes() + 1);
                    if (!LocalContextHelper.isUserLogin()) {
                        oldSqlMonitorBean.setTriggerCategory("\u7cfb\u7edf");
                    } else {
                        oldSqlMonitorBean.setTriggerCategory("\u7528\u6237(" + LocalContextHelper.getLoginUserName() + ")");
                    }
                    service.update(oldSqlMonitorBean);
                }
            }
            Object object = result;
            return object;
        }
        finally {
            MYBATIS_MESSAGE_HOLDER.remove();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }

    public static void offerMybatisMessage(String message) {
        if (longQueryTime > 0L) {
            StringBuilder sb = MYBATIS_MESSAGE_HOLDER.get();
            if (sb == null) {
                sb = new StringBuilder();
                MYBATIS_MESSAGE_HOLDER.set(sb);
            }
            sb.append(message);
            if (sb.length() > 0) {
                sb.append("\n");
            }
        }
    }
}
