/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.service.impl;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigColumnBean;
import com.sunwayworld.cloud.module.item.query.persistent.dao.CoreQueryConfigDao;
import com.sunwayworld.cloud.module.item.query.persistent.mapper.CoreQueryMapper;
import com.sunwayworld.cloud.module.item.query.service.CoreQueryConfigColumnService;
import com.sunwayworld.cloud.module.item.query.service.CoreQueryConfigService;
import com.sunwayworld.cloud.module.item.support.helper.CoreQueryConfigHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreQueryConfigServiceImpl
implements CoreQueryConfigService {
    @Autowired
    private CoreQueryConfigDao configDao;
    @Autowired
    @Lazy
    private CoreQueryConfigColumnService columnService;
    @Autowired
    private CoreQueryMapper queryMapper;

    @Override
    public CoreQueryConfigDao getDao() {
        return this.configDao;
    }

    @Override
    @Transactional
    public String insert(RestJsonWrapperBean wrapper) {
        CoreQueryConfigBean config = wrapper.parseUnique(CoreQueryConfigBean.class);
        config.setId(ApplicationContextHelper.getNextSequence("T_CORE_QUERY_CONFIG"));
        this.getDao().insert(config);
        return config.getId();
    }

    @Override
    @Transactional
    public void postUpdate(List<String> updatedColumnNameList, List<String> idList) {
        if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "sql")) {
            String id = idList.get(0);
            CoreQueryConfigBean config = (CoreQueryConfigBean)this.getDao().selectById(id);
            if (StringUtils.isEmpty(config.getSql())) {
                CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
                filter.setConfigId(config.getId());
                this.columnService.getDao().deleteBy((CoreQueryConfigColumnBean)filter, "CONFIGID");
            } else {
                List insertColumnIdList;
                CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
                filter.setConfigId(config.getId());
                List<CoreQueryConfigColumnBean> columnList = this.columnService.selectList(filter, new Order[0]);
                String sql = this.getReplacedSql(config.getSql());
                List<String> columnIdList = SqlHelper.getSqlColumnList(sql);
                columnIdList.removeIf(c -> CoreQueryConfigHelper.IGNORE_COLUMN_LIST.contains(c));
                List deleteColumnList = columnList.stream().filter(c -> columnIdList.stream().noneMatch(i -> i.equalsIgnoreCase(c.getColumnId()))).collect(Collectors.toList());
                if (!deleteColumnList.isEmpty()) {
                    this.columnService.getDao().deleteBy(deleteColumnList, new String[0]);
                }
                if (!(insertColumnIdList = columnIdList.stream().filter(i -> columnList.stream().noneMatch(c -> i.equalsIgnoreCase(c.getColumnId()))).collect(Collectors.toList())).isEmpty()) {
                    int qty = insertColumnIdList.size();
                    ArrayList<CoreQueryConfigColumnBean> insertColumnList = new ArrayList<CoreQueryConfigColumnBean>();
                    List<Long> configColumnIdList = ApplicationContextHelper.getNextIdentityList(qty);
                    long maxOrderNo = columnList.stream().mapToLong(CoreQueryConfigColumnBean::getOrderNo).max().orElse(1L);
                    for (int i2 = 0; i2 < qty; ++i2) {
                        CoreQueryConfigColumnBean column = new CoreQueryConfigColumnBean();
                        column.setId(configColumnIdList.get(i2));
                        column.setConfigId(id);
                        column.setColumnId((String)insertColumnIdList.get(i2));
                        column.setOrderNo(maxOrderNo + (long)i2 + 1L);
                        column.setLastSuspendedFlag("0");
                        insertColumnList.add(column);
                    }
                    this.columnService.getDao().insert(insertColumnList);
                }
            }
        }
    }

    @Override
    @Transactional
    public void save(String id, RestJsonWrapperBean wrapper) {
        CoreQueryConfigBean config = (CoreQueryConfigBean)this.getDao().selectById(id);
        if (StringUtils.isEmpty(config.getSql())) {
            CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
            filter.setConfigId(config.getId());
            this.columnService.getDao().deleteBy((CoreQueryConfigColumnBean)filter, "CONFIGID");
        } else {
            List insertColumnIdList;
            CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
            filter.setConfigId(config.getId());
            List<CoreQueryConfigColumnBean> columnList = this.columnService.selectList(filter, new Order[0]);
            List<String> columnIdList = SqlHelper.getSqlColumnList(config.getSql());
            columnIdList.removeIf(c -> CoreQueryConfigHelper.IGNORE_COLUMN_LIST.contains(c));
            List deleteColumnList = columnList.stream().filter(c -> columnIdList.stream().noneMatch(i -> i.equalsIgnoreCase(c.getColumnId()))).collect(Collectors.toList());
            if (!deleteColumnList.isEmpty()) {
                this.columnService.getDao().deleteBy(deleteColumnList, new String[0]);
            }
            if (!(insertColumnIdList = columnIdList.stream().filter(i -> columnList.stream().noneMatch(c -> i.equalsIgnoreCase(c.getColumnId()))).collect(Collectors.toList())).isEmpty()) {
                int qty = insertColumnIdList.size();
                ArrayList<CoreQueryConfigColumnBean> insertColumnList = new ArrayList<CoreQueryConfigColumnBean>();
                List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
                long maxOrderNo = columnList.stream().mapToLong(CoreQueryConfigColumnBean::getOrderNo).max().orElse(1L);
                for (int i2 = 0; i2 < qty; ++i2) {
                    CoreQueryConfigColumnBean column = new CoreQueryConfigColumnBean();
                    column.setId(idList.get(i2));
                    column.setConfigId(id);
                    column.setColumnId((String)insertColumnIdList.get(i2));
                    column.setOrderNo(maxOrderNo + (long)i2 + 1L);
                    column.setLastSuspendedFlag("0");
                    insertColumnList.add(column);
                }
                this.columnService.getDao().insert(insertColumnList);
            }
        }
    }

    @Override
    public Page<CoreQueryConfigColumnBean> selectQueryConfigColumnPagination(String id, RestJsonWrapperBean wrapper) {
        SearchFilter filter = SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ);
        return this.columnService.selectPaginationByFilter(filter, wrapper);
    }

    @Override
    public List<CoreQueryConfigColumnBean> selectQueryConfigColumnList(String id) {
        CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
        filter.setConfigId(id);
        return this.columnService.selectList(filter, Order.asc("ORDERNO"));
    }

    @Override
    @Transactional
    public void suspendConfigColumn(String id, RestJsonWrapperBean wrapper) {
        this.columnService.suspend(wrapper);
    }

    @Override
    @Transactional
    public void unsuspendConfigColumn(String id, RestJsonWrapperBean wrapper) {
        this.columnService.unsuspend(wrapper);
    }

    @Override
    public Page<Map<String, Object>> selectRelatedPagination(String id, RestJsonWrapperBean wrapper) {
        CoreQueryConfigBean config = (CoreQueryConfigBean)this.getDao().selectByIdIfPresent(id);
        if (config == null) {
            return new Pagination<Map<String, Object>>();
        }
        CoreQueryConfigColumnBean filter = new CoreQueryConfigColumnBean();
        filter.setConfigId(id);
        filter.setLastSuspendedFlag("0");
        List<CoreQueryConfigColumnBean> columnList = this.columnService.getDao().selectList((CoreQueryConfigColumnBean)filter, Order.asc("ORDERNO"));
        MapperParameter parameter = wrapper.extractMapFilter();
        String sql = this.getReplacedSql(config.getSql());
        if (StringUtils.isBlank(sql)) {
            return new Pagination<Map<String, Object>>();
        }
        if (SqlHelper.hasOrderSegment(sql)) {
            String[] orders;
            parameter.put("sql", SqlHelper.removeEndingOrderIfPresent(sql));
            for (String order : orders = StringUtils.split(SqlHelper.getOrderSegment(sql), ",")) {
                String trimedOrder = StringUtils.trim(order);
                if (StringUtils.endsWithIgnoreCase(trimedOrder, "ASC")) {
                    parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "ASC")), "ASC");
                    continue;
                }
                if (StringUtils.endsWithIgnoreCase(order.trim(), "DESC")) {
                    parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "DESC")), "DESC");
                    continue;
                }
                parameter.setOrderParam(trimedOrder, "ASC");
            }
        } else {
            parameter.put("sql", sql);
            parameter.setOrderParam(columnList.get(0).getColumnId(), "ASC");
        }
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.queryMapper.selectByCondition(parameter));
        for (Map map : page.getRows()) {
            CollectionUtils.lowerCaseKey(map);
            columnList.stream().filter(c -> "DATE".equals(c.getType()) || "DATETIME".equals(c.getType())).forEach(c -> {
                String key = c.getColumnId().toLowerCase();
                Object value = map.get(key);
                if (value != null) {
                    if ("DATE".equals(c.getType())) {
                        map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                    } else if ("DATETIME".equals(c.getType())) {
                        map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                    }
                }
            });
        }
        return page;
    }

    private String getReplacedSql(String sql) {
        if (StringUtils.contains(sql, "${loginUserId}")) {
            sql = StringUtils.replace(sql, "${loginUserId}", "'" + LocalContextHelper.getLoginUserId() + "'");
        }
        if (StringUtils.contains(sql, "${loginOrgId}")) {
            sql = StringUtils.replace(sql, "${loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
        }
        if (StringUtils.contains(sql, "${loginRoleId}")) {
            sql = StringUtils.replace(sql, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
        }
        return sql;
    }
}
