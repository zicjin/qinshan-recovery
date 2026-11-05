/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.interceptor;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JdbcUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.i18n.LocaleContextHolder;

@Intercepts(value={@Signature(type=StatementHandler.class, method="prepare", args={Connection.class, Integer.class}), @Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisInterceptor
implements Interceptor {
    private static final String mappedStatementIdKey = "SUNWAY-MYBATIS-MAPPED-STATEMENT-ID";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter;
        MappedStatement mappedStatement;
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            HttpServletResponse response;
            RoutingStatementHandler statementHandler = (RoutingStatementHandler)invocation.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler)ReflectionUtils.getFieldValue(statementHandler, "delegate");
            MappedStatement mappedStatement2 = (MappedStatement)ReflectionUtils.getFieldValue(delegate, "mappedStatement");
            this.enhanceParameter(delegate, mappedStatement2);
            BoundSql boundSql = delegate.getBoundSql();
            String cleanedSql = SqlHelper.cleanDanglingWhereClause(boundSql.getSql());
            if (!cleanedSql.equals(boundSql.getSql())) {
                ReflectionUtils.setFieldValue(boundSql, "sql", cleanedSql);
            }
            if (StringUtils.startsWith(mappedStatement2.getId(), DaoMapper.class.getName()) || StringUtils.startsWith(mappedStatement2.getId(), GlobalMapper.class.getName())) {
                return invocation.proceed();
            }
            if (MybatisPageHelper.isResponseMappedStatementId() && (response = ServletUtils.getCurrentResponse()) != null && mappedStatement2.getId() != null) {
                response.setHeader(mappedStatementIdKey, mappedStatement2.getId());
            }
            if (MybatisPageHelper.isHighPerformanceDownload()) {
                this.highPerformanceDownload(invocation, delegate, mappedStatement2);
                this.reassignNoResultSql(delegate);
                return invocation.proceed();
            }
            if (MybatisPageHelper.isResponseCount()) {
                this.interceptPagination(invocation, delegate, mappedStatement2);
            }
            if (MybatisPageHelper.isResponseOptions()) {
                List<String> optionsList = this.getOptionsList(invocation, delegate, mappedStatement2);
                MybatisPageHelper.setOptionsList(optionsList);
            }
            if (MybatisPageHelper.isResponseNoListData()) {
                this.reassignNoResultSql(delegate);
            }
        } else if (invocation.getTarget() instanceof Executor && !StringUtils.startsWith((mappedStatement = (MappedStatement)invocation.getArgs()[0]).getId(), DaoMapper.class.getName()) && invocation.getArgs().length > 1 && (parameter = invocation.getArgs()[1]) != null && Map.class.isAssignableFrom(parameter.getClass())) {
            Map map = (Map)parameter;
            this.enhanceParameter(map);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }

    private void enhanceParameter(BaseStatementHandler delegate, MappedStatement mappedStatement) {
        BoundSql boundSql = delegate.getBoundSql();
        boundSql.setAdditionalParameter("_locale", I18nHelper.getLang(LocaleContextHolder.getLocale()));
        if (!StringUtils.startsWith(mappedStatement.getId(), DaoMapper.class.getName()) && LocalContextHelper.isUserLogin()) {
            boundSql.setAdditionalParameter("loginUser", LocalContextHelper.getLoginUser());
            boundSql.setAdditionalParameter("loginOrgId", LocalContextHelper.getLoginOrgId());
            boundSql.setAdditionalParameter("loginOrgName", LocalContextHelper.getLoginOrgName());
            boundSql.setAdditionalParameter("loginRoleId", LocalContextHelper.getLoginRoleId());
            boundSql.setAdditionalParameter("loginRoleName", LocalContextHelper.getLoginRoleName());
            Object currentProxy = SunwayAopContext.currentProxy();
            if (currentProxy != null && GenericDao.class.isAssignableFrom(ClassUtils.getRawType(currentProxy.getClass()))) {
                boundSql.setAdditionalParameter("service_table", ((GenericDao)currentProxy).getTable());
            }
        }
    }

    private String getLcdpScriptTable() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            return null;
        }
        String scriptPath = request.getHeader("script-path");
        if (!StringUtils.isBlank(scriptPath)) {
            int lastIndex = scriptPath.lastIndexOf(".");
            String path = scriptPath.substring(0, lastIndex);
            String tableName = (String)RedisHelper.get("SCRIPT_PATH_TABLE_MAPPING_CACHE", path);
            return tableName;
        }
        return null;
    }

    private void enhanceParameter(Map<String, Object> map) {
        Object currentProxy;
        map.put("_locale", I18nHelper.getLang(LocaleContextHolder.getLocale()));
        if (LocalContextHelper.isUserLogin()) {
            if (!map.containsKey("loginUser")) {
                map.put("loginUser", LocalContextHelper.getLoginUser());
            }
            map.put("loginOrgId", LocalContextHelper.getLoginOrgId());
            map.put("loginOrgName", LocalContextHelper.getLoginOrgName());
            map.put("loginRoleId", LocalContextHelper.getLoginRoleId());
            map.put("loginRoleName", LocalContextHelper.getLoginRoleName());
        }
        if (!map.containsKey("service_table") && (currentProxy = SunwayAopContext.currentProxy()) != null && GenericDao.class.isAssignableFrom(ClassUtils.getRawType(currentProxy.getClass()))) {
            map.put("service_table", ((GenericDao)currentProxy).getTable());
        }
    }

    private BoundSql getNewBoundSql(MappedStatement ms, BoundSql boundSql, String newSql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> additionalParams = (Map<String, Object>) ReflectionUtils.getFieldValue(boundSql, "additionalParameters");
            if (additionalParams != null) {
                additionalParams.forEach(newBoundSql::setAdditionalParameter);
            }
        } catch (Exception e) {
            for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                String prop = mapping.getProperty();
                if (!boundSql.hasAdditionalParameter(prop)) continue;
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        if (LocalContextHelper.isUserLogin()) {
            newBoundSql.setAdditionalParameter("loginUser", LocalContextHelper.getLoginUser());
            newBoundSql.setAdditionalParameter("loginOrgId", LocalContextHelper.getLoginOrgId());
            newBoundSql.setAdditionalParameter("loginOrgName", LocalContextHelper.getLoginOrgName());
            newBoundSql.setAdditionalParameter("loginRoleId", LocalContextHelper.getLoginRoleId());
            newBoundSql.setAdditionalParameter("loginRoleName", LocalContextHelper.getLoginRoleName());
        }
        newBoundSql.setAdditionalParameter("_locale", I18nHelper.getLang(LocaleContextHolder.getLocale()));
        return newBoundSql;
    }

    private void reassignNoResultSql(BaseStatementHandler delegate) {
        BoundSql boundSql = delegate.getBoundSql();
        ReflectionUtils.setFieldValue(boundSql, "sql", DialectRepository.getDialect().getLimitSql(boundSql.getSql(), 0, 0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void highPerformanceDownload(Invocation invocation, BaseStatementHandler delegate, MappedStatement mappedStatement) throws SQLException {
        PageRowBounds bindRowBounds = MybatisPageHelper.getBindPageRowBounds();
        MybatisPageHelper.clearPage();
        Objects.requireNonNull(bindRowBounds);
        ResultSetHandler resultSetHandler = (ResultSetHandler)ReflectionUtils.getFieldValue(delegate, "resultSetHandler");
        ReflectionUtils.setFieldValue(delegate, "rowBounds", bindRowBounds);
        PageRowBounds pageRowBounds = (PageRowBounds)ReflectionUtils.getFieldValue(delegate, "rowBounds");
        BoundSql boundSql = delegate.getBoundSql();
        if (pageRowBounds.getPageNumber() > 0 && pageRowBounds.getLimit() > 0) {
            ReflectionUtils.setFieldValue(boundSql, "sql", DialectRepository.getDialect().getLimitSql(boundSql.getSql(), pageRowBounds.getOffset(), pageRowBounds.getLimit()));
            ReflectionUtils.setFieldValue(delegate, "rowBounds", RowBounds.DEFAULT);
            ReflectionUtils.setFieldValue(resultSetHandler, "rowBounds", RowBounds.DEFAULT);
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            Connection connection = (Connection)invocation.getArgs()[0];
            ps = connection.prepareStatement(boundSql.getSql());
            defaultParameterHandler.setParameters(ps);
            rs = ps.executeQuery();
            MybatisPageHelper.getHighPerformanceDownloadConsumer().accept(rs);
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            throw throwable;
        }
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(ps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void interceptPagination(Invocation invocation, BaseStatementHandler delegate, MappedStatement mappedStatement) throws SQLException {
        PageRowBounds bindRowBounds = MybatisPageHelper.getBindPageRowBounds();
        MybatisPageHelper.clearPage();
        Objects.requireNonNull(bindRowBounds);
        ResultSetHandler resultSetHandler = (ResultSetHandler)ReflectionUtils.getFieldValue(delegate, "resultSetHandler");
        ReflectionUtils.setFieldValue(delegate, "rowBounds", bindRowBounds);
        PageRowBounds pageRowBounds = (PageRowBounds)ReflectionUtils.getFieldValue(delegate, "rowBounds");
        BoundSql boundSql = delegate.getBoundSql();
        if (!MybatisPageHelper.isDataOnly()) {
            ResultSet rs;
            PreparedStatement ps;
            block4: {
                String[] sumProperties = (String[])Arrays.stream(pageRowBounds.getSumProperties()).map(f -> StringUtils.removeStart(f, "ext$.")).toArray(String[]::new);
                String[] countProperties = (String[])Arrays.stream(pageRowBounds.getCountProperties()).map(f -> StringUtils.removeStart(f, "ext$.")).toArray(String[]::new);
                String selectTotalSql = SqlHelper.getSelectTotalSql(boundSql.getSql(), sumProperties, countProperties);
                BoundSql selectTotalBoundSql = this.getNewBoundSql(mappedStatement, boundSql, selectTotalSql);
                ps = null;
                rs = null;
                try {
                    Map<String, Object> map;
                    DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), selectTotalBoundSql);
                    Connection connection = (Connection)invocation.getArgs()[0];
                    ps = connection.prepareStatement(selectTotalSql);
                    defaultParameterHandler.setParameters(ps);
                    rs = ps.executeQuery();
                    if (!rs.next() || (map = JdbcUtils.mapRow(rs)).isEmpty()) break block4;
                    HashMap<String, Number> sumMap = new HashMap<String, Number>();
                    map.forEach((k, v) -> {
                        String colName = Arrays.stream(pageRowBounds.getSumProperties()).filter(n -> n.equalsIgnoreCase((String)k) || n.equalsIgnoreCase("ext$." + k)).findFirst().orElse(null);
                        if (colName == null) {
                            sumMap.put((String)k, (Number)v);
                        } else {
                            sumMap.put(colName, (Number)v);
                        }
                    });
                    pageRowBounds.setTotalMap(sumMap);
                }
                catch (Throwable throwable) {
                    JdbcUtils.closeResultSet(rs);
                    JdbcUtils.closeStatement(ps);
                    throw throwable;
                }
            }
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
        }
        if (pageRowBounds.getPageNumber() > 0 && pageRowBounds.getLimit() > 0) {
            ReflectionUtils.setFieldValue(boundSql, "sql", DialectRepository.getDialect().getLimitSql(StringUtils.replace(boundSql.getSql(), "EXT$.", ""), pageRowBounds.getOffset(), pageRowBounds.getLimit()));
            ReflectionUtils.setFieldValue(delegate, "rowBounds", RowBounds.DEFAULT);
            ReflectionUtils.setFieldValue(resultSetHandler, "rowBounds", RowBounds.DEFAULT);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> getOptionsList(Invocation invocation, BaseStatementHandler delegate, MappedStatement mappedStatement) throws SQLException {
        ArrayList<String> selectOptionsList = new ArrayList<String>();
        BoundSql boundSql = delegate.getBoundSql();
        String selectOptionsSql = StringUtils.replace(DialectRepository.getDialect().getSelectOptionsSql(boundSql.getSql(), MybatisPageHelper.getBindPageRowBounds().getOptionsProperty()), "EXT$.", "");
        BoundSql selectOptionsBoundSql = this.getNewBoundSql(mappedStatement, boundSql, selectOptionsSql);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), selectOptionsBoundSql);
            Connection connection = (Connection)invocation.getArgs()[0];
            ps = connection.prepareStatement(selectOptionsSql);
            defaultParameterHandler.setParameters(ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                String option = ConvertUtils.convert(rs.getObject(1), String.class);
                if (option == null || selectOptionsList.contains(option)) continue;
                selectOptionsList.add(option);
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            throw throwable;
        }
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(ps);
        return selectOptionsList;
    }
}
