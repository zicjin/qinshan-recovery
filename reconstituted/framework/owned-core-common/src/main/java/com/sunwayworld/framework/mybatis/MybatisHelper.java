/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.mybatis.MybatisStrictMap;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisDraftService;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisStatementService;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class MybatisHelper {
    private static final Logger logger = LogManager.getLogger(MybatisHelper.class);
    private static ThreadLocal<String> KEY_PREFIX = new ThreadLocal();

    public static SqlSessionFactory getSqlSessionFactory() { return ApplicationContextHelper.getBean(SqlSessionFactory.class); }

    public static Connection getConnection() {
        return DataSourceUtils.getConnection(ApplicationContextHelper.getBean(DataSource.class));
    }

    public static Connection getNewConnection() {
        try {
            return ApplicationContextHelper.getBean(DataSource.class).getConnection();
        }
        catch (SQLException sqle) {
            throw new ApplicationRuntimeException(sqle);
        }
    }

    public static void updateRuntimeConfiguration() {
        Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
        for (String field : new String[]{"mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments"}) {
            Map oldMap = (Map)ReflectionUtils.getFieldValue(configuration, field);
            MybatisStrictMap newMap = new MybatisStrictMap(field + " UpdatableCollection", oldMap);
            ReflectionUtils.setFieldValue(configuration, field, newMap);
        }
    }

    public static void updateRuntimeMapper() {
        CoreAdminMybatisStatementService statementService = ApplicationContextHelper.getBean(CoreAdminMybatisStatementService.class);
        List<CoreAdminMybatisStatementBean> statementList = statementService.selectActiveList(null, null);
        if (!statementList.isEmpty()) {
            Map<String, List<CoreAdminMybatisStatementBean>> mapperIdMap = statementList.stream().collect(Collectors.groupingBy(CoreAdminMybatisStatementBean::getMapperId));
            mapperIdMap.forEach((mapperId, v) -> {
                String mapperContent = MybatisHelper.getMapperContent(mapperId);
                if (!StringUtils.isBlank(mapperContent)) {
                    Map<String, List<CoreAdminMybatisStatementBean>> menuIdMap = v.stream().collect(Collectors.groupingBy(CoreAdminMybatisStatementBean::getMenuId));
                    menuIdMap.forEach((menuId, l) -> {
                        String copyMapperContent = mapperContent;
                        for (CoreAdminMybatisStatementBean statement : statementList) {
                            copyMapperContent = copyMapperContent.replaceAll(MybatisHelper.getMapperSelectStatementRegex(statement.getStatementId()), statement.getStatementXml());
                        }
                        MybatisHelper.updateRuntimeMapper(copyMapperContent, menuId, mapperId);
                    });
                } else {
                    logger.warn("No mapper content for mapperId :{}", mapperId);
                }
            });
        }
    }

    public static void updateRuntimeMapper(String menuId, String mapperId) {
        String mapperContent = MybatisHelper.getMapperContent(mapperId);
        if (StringUtils.isBlank(mapperContent)) {
            logger.warn("No mapper content for mapperId :{}", (Object)mapperId);
            return;
        }
        CoreAdminMybatisStatementService statementService = ApplicationContextHelper.getBean(CoreAdminMybatisStatementService.class);
        List<CoreAdminMybatisStatementBean> statementList = statementService.selectActiveList(menuId, mapperId);
        for (CoreAdminMybatisStatementBean statement : statementList) {
            mapperContent = mapperContent.replaceAll(MybatisHelper.getMapperSelectStatementRegex(statement.getStatementId()), statement.getStatementXml());
        }
        MybatisHelper.updateRuntimeMapper(mapperContent, menuId, mapperId);
    }

    public static void setDraftMapper(String menuId, String mapperId) {
        String mapperContent = MybatisHelper.getMapperContent(mapperId);
        if (StringUtils.isBlank(mapperContent)) {
            logger.warn("No mapper content for mapperId :{}", (Object)mapperId);
            return;
        }
        CoreAdminMybatisStatementService statementService = ApplicationContextHelper.getBean(CoreAdminMybatisStatementService.class);
        List<CoreAdminMybatisStatementBean> statementList = statementService.selectActiveList(menuId, mapperId);
        CoreAdminMybatisDraftService draftService = ApplicationContextHelper.getBean(CoreAdminMybatisDraftService.class);
        CoreAdminMybatisDraftBean draftFilter = new CoreAdminMybatisDraftBean();
        draftFilter.setMenuId(menuId);
        draftFilter.setMapperId(mapperId);
        List<CoreAdminMybatisDraftBean> draftList = draftService.selectList(draftFilter, new Order[0]);
        for (CoreAdminMybatisDraftBean draft : draftList) {
            mapperContent = mapperContent.replaceAll(MybatisHelper.getMapperSelectStatementRegex(draft.getStatementId()), draft.getStatementXml());
        }
        for (CoreAdminMybatisStatementBean statement : statementList) {
            if (!draftList.stream().noneMatch(d -> d.getMenuId().equals(statement.getMenuId()) && d.getMapperId().equals(statement.getMapperId()) && d.getStatementId().equals(statement.getStatementId()))) continue;
            mapperContent = mapperContent.replaceAll(MybatisHelper.getMapperSelectStatementRegex(statement.getStatementId()), statement.getStatementXml());
        }
        MybatisHelper.updateRuntimeMapper(mapperContent, "draft$" + menuId, mapperId);
    }

    public static void removeDraftMapper(String menuId, String mapperId) {
        MybatisHelper.removeRuntimeMapper("draft$" + menuId + "$" + mapperId);
    }

    public static void removeRuntimeMapper(String resource) {
        Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
        Set loadedResourcesSet = (Set)ReflectionUtils.getFieldValue(configuration, "loadedResources");
        loadedResourcesSet.remove(resource);
        Map mappedStatements = (Map)ReflectionUtils.getFieldValue(configuration, "mappedStatements");
        mappedStatements.remove(resource);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateRuntimeMapper(String mapperXmlContent, String prefix, String resource) {
        try {
            if (!StringUtils.isBlank(prefix)) {
                resource = prefix + "$" + resource;
                MybatisHelper.setKeyPrefix(prefix);
            }
            Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
            Set loadedResourcesSet = (Set)ReflectionUtils.getFieldValue(configuration, "loadedResources");
            loadedResourcesSet.remove(resource);
            XMLMapperBuilder builder = new XMLMapperBuilder(new ByteArrayInputStream(mapperXmlContent.getBytes()), configuration, resource, configuration.getSqlFragments());
            builder.parse();
        }
        finally {
            MybatisHelper.removeKeyPrefix();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String getStatementContent(String mappedStatementId) {
        Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
        Map mappedStatements = (Map)ReflectionUtils.getFieldValue(configuration, "mappedStatements");
        MappedStatement ms = (MappedStatement)mappedStatements.get(mappedStatementId);
        String resource = ms.getResource();
        int index = Math.max(resource.lastIndexOf("com/sunwayworld"), resource.lastIndexOf("com\\sunwayworld"));
        String name = "/" + StringUtils.replace(resource.substring(index), "\\", "/");
        if (!StringUtils.endsWith(name, "xml")) {
            int length = name.length();
            while (length > 0 && !StringUtils.endsWith(name, "xml")) {
                name = name.substring(0, length - 1);
                length = name.length();
            }
        }
        try (InputStream is = MybatisHelper.class.getResourceAsStream(name);){
            String mapperContent = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining(System.lineSeparator()));
            String simpleId = mappedStatementId.substring(mappedStatementId.lastIndexOf(".") + 1);
            String string = mapperContent.replaceAll(MybatisHelper.getMapperSelectStatementRegex(simpleId), "");
            return string;
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String getMapperContent(String mapperId) {
        String mappedStatementId = mapperId + ".selectByCondition";
        Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
        Map mappedStatements = (Map)ReflectionUtils.getFieldValue(configuration, "mappedStatements");
        MappedStatement ms = (MappedStatement)mappedStatements.get(mappedStatementId);
        if (ms == null) {
            return null;
        }
        String resource = ms.getResource();
        int index = Math.max(resource.lastIndexOf("com/sunwayworld"), resource.lastIndexOf("com\\sunwayworld"));
        String name = "/" + StringUtils.replace(resource.substring(index), "\\", "/");
        if (!StringUtils.endsWith(name, "xml")) {
            int length = name.length();
            while (length > 0 && !StringUtils.endsWith(name, "xml")) {
                name = name.substring(0, length - 1);
                length = name.length();
            }
        }
        try (InputStream is = MybatisHelper.class.getResourceAsStream(name);){
            String string = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));
            return string;
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }

    public static String getMapperContent(Class<? extends GenericMapper<?>> mapperClazz) {
        return MybatisHelper.getMapperContent(mapperClazz.getName());
    }

    public static <T> void correctValue(List<ColumnContext> columnContextList, Map<String, Object> map) {
        MybatisHelper.correctValue(columnContextList.stream().collect(Collectors.toMap(ColumnContext::getColumnName, ColumnContext::getType)), map);
    }

    public static <T> void correctValue(Map<String, Class<?>> columnTypeMap, Map<String, Object> map) {
        if (CollectionUtils.isEmpty(columnTypeMap)) {
            return;
        }
        if (map.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Class<?> columnType;
            Object value = entry.getValue();
            if (value == null || (columnType = columnTypeMap.get(entry.getKey())) == null || columnType.isAssignableFrom(value.getClass())) continue;
            entry.setValue(ConvertUtils.convert(value, columnType));
        }
    }

    public static void setKeyPrefix(String prefix) {
        KEY_PREFIX.set(prefix);
    }

    public static void removeKeyPrefix() {
        KEY_PREFIX.remove();
    }

    public static String getKeyPrefix() {
        return KEY_PREFIX.get();
    }

    private static final String getMapperSelectStatementRegex(String statementId) {
        return "<(?i)select\\s*id=\"" + statementId + "[\\S\\s]+?<\\s*/(?i)select\\s*>";
    }
}
