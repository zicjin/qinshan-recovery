/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.dao.impl;

import com.sunwayworld.cloud.module.lcdp.base.service.LcdpBaseService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpWrapperParseUtils;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.mybatis.mapper.MapDaoMapperWrapper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.impl.MapDaoImpl;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.JdbcUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class LcdpMapDaoImpl
extends MapDaoImpl {
    @Autowired
    private MapDaoMapperWrapper mapDaoMapper;

    @Override
    public <ID> void update(TableContext context, Map<String, Object> item, List<String> updateColumnList, String ... searchColumns) {
        List<Object> idList;
        TableContextInstance instance = TableContextInstance.of(context, item);
        if (!CollectionUtils.isEmpty(updateColumnList)) {
            instance.getColumnInstanceList().forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(updateColumnList, c.getColumnContext().getColumnName())));
        }
        if (!instance.isUpdatable()) {
            return;
        }
        ArrayList<Map<String, Object>> oldItemList = new ArrayList<Map<String, Object>>();
        ArrayList<String> updatedColumnNameList = new ArrayList<String>();
        if (ArrayUtils.isEmpty(searchColumns)) {
            oldItemList.add(this.selectById(context, CollectionUtils.getValueIgnorecase(item, "ID")));
            this.mapDaoMapper.updateById(instance);
            instance.getColumnInstanceList().stream().filter(c -> c.isActive()).forEach(c -> updatedColumnNameList.add(c.getColumnContext().getColumnName()));
        } else {
            oldItemList.addAll(this.selectList(context, item, ArrayUtils.asList(searchColumns), new Order[0]));
            idList = this.selectIdList(context, item, ArrayUtils.asList(searchColumns), new Order[0]);
            if (!idList.isEmpty()) {
                this.mapDaoMapper.updateByIdList(instance, idList);
            }
            instance.getColumnInstanceList().stream().filter(c -> c.isActive()).forEach(c -> updatedColumnNameList.add(c.getColumnContext().getColumnName()));
        }
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
            idList = oldItemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "ID")).collect(Collectors.toList());
            List<Map<String, Object>> newItemList = this.selectListByIdList(context, idList, new Order[0]);
            this.auditUpdate(context.getTableName(), oldItemList, newItemList);
        }
        idList = oldItemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "ID")).collect(Collectors.toList());
        LcdpBaseService service = this.getScriptService(context);
        if (service != null) {
            service.postUpdate(updatedColumnNameList, idList);
        }
    }

    @Override
    public <ID> void updateByIdList(TableContext context, Map<String, Object> item, List<ID> idList, String ... updateColumns) {
        if (idList == null || idList.size() == 0) {
            return;
        }
        TableContextInstance instance = TableContextInstance.of(context, item);
        if (!ArrayUtils.isEmpty(updateColumns)) {
            instance.getColumnInstanceList().forEach(c -> c.setActive(ArrayUtils.containsIgnoreCase(updateColumns, c.getColumnContext().getColumnName())));
        }
        if (!instance.isUpdatable()) {
            return;
        }
        List<Map<String, Object>> oldItemList = this.selectListByIdList(context, idList, new Order[0]);
        this.mapDaoMapper.updateByIdList(instance, idList);
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
            List<Map<String, Object>> newItemList = this.selectListByIdList(context, idList, new Order[0]);
            this.auditUpdate(context.getTableName(), oldItemList, newItemList);
        }
        List<String> updatedColumnNameList = instance.getColumnInstanceList().stream().filter(c -> c.isActive()).map(c -> c.getColumnContext().getColumnName()).collect(Collectors.toList());
        LcdpBaseService service = this.getScriptService(context);
        if (service != null) {
            service.postUpdate(updatedColumnNameList, idList);
        }
    }

    @Override
    public void update(TableContext context, List<Map<String, Object>> itemList, String ... updateColumns) {
        LcdpBaseService service;
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        if (ArrayUtils.isEmpty(updateColumns)) {
            throw new ApplicationRuntimeException("MYBATIS.EXCEPTION.UPDATE_LIST_REQUIRES_COLUMNS", new String[0]);
        }
        List<String> updateColumnList = CollectionUtils.emptyList();
        Collections.addAll(updateColumnList, updateColumns);
        if (DialectRepository.getDialect() instanceof OracleDialect) {
            if (!(itemList instanceof ArrayList)) {
                itemList = new ArrayList<Map<String, Object>>(itemList);
            }
            Iterator<Map<String, Object>> iterator = itemList.iterator();
            block0: while (iterator.hasNext()) {
                Map<String, Object> item = iterator.next();
                for (String updateColumn : updateColumnList) {
                    Object value = CollectionUtils.getValueIgnorecase(item, updateColumn);
                    if (!(value instanceof String) || !JdbcUtils.isBigData((String)value)) continue;
                    iterator.remove();
                    this.update(context, item, updateColumnList, new String[0]);
                    continue block0;
                }
            }
            if (itemList.isEmpty()) {
                return;
            }
        }
        boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
        boolean selectList = !ApplicationContextHelper.stressTesting() && auditable;
        List idList = itemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "ID")).collect(Collectors.toList());
        List<Map<String, Object>> oldItemList = selectList ? this.selectListByIdList(context, idList, new Order[0]) : null;
        List<TableContextInstance> instanceList = itemList.stream().map(m -> TableContextInstance.of(context, m)).collect(Collectors.toList());
        this.mapDaoMapper.updateItemList(context, instanceList, updateColumnList);
        if (!ApplicationContextHelper.stressTesting() && auditable) {
            List<Map<String, Object>> newItemList = this.selectListByIdList(context, idList, new Order[0]);
            this.auditUpdate(context.getTableName(), oldItemList, newItemList);
        }
        if ((service = this.getScriptService(context)) != null) {
            service.postUpdate(updateColumnList, idList);
        }
    }

    @Override
    public void updateIfChanged(TableContext context, List<Map<String, Object>> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Map<String, Object>> selectedItemList = this.selectListByIdList(context, itemList.stream().map(m -> CollectionUtils.getValueIgnorecase(m, "ID")).collect(Collectors.toList()), new Order[0]);
        selectedItemList.stream().forEach(map -> map.forEach((column, value) -> {
            if (column != null) {
                if (LocalDate.class.isInstance(value)) {
                    try {
                        value = DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(value.toString()));
                        map.put(column, value);
                    }
                    catch (Exception exception) {}
                } else if (LocalDateTime.class.isInstance(value)) {
                    try {
                        value = DateTimeUtils.formatLocalDateTime(DateTimeUtils.parseLocalDateTime(value.toString()));
                        map.put(column, value);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }));
        for (Map<String, Object> item : itemList) {
            LcdpBaseService service;
            Map selectedItem = null;
            Object itemId = CollectionUtils.getValueIgnorecase(item, "ID");
            selectedItem = !String.class.equals(context.getIdColumnContext().getType()) && NumberUtils.isNumber(itemId.toString()) && !itemId.toString().startsWith("0") ? selectedItemList.stream().filter(i -> ConvertUtils.convert(CollectionUtils.getValueIgnorecase(i, "ID"), Long.class).compareTo(ConvertUtils.convert(itemId, Long.class)) == 0).findFirst().orElseThrow(() -> new ApplicationRuntimeException("MYBATIS.EXCEPTION.NO_VALUE_EXISTS", new String[0])) : selectedItemList.stream().filter(i -> CollectionUtils.getValueIgnorecase(i, "ID").equals(itemId)).findFirst().orElseThrow(() -> new ApplicationRuntimeException("MYBATIS.EXCEPTION.NO_VALUE_EXISTS", new String[0]));
            ArrayList<String> updatedColumnList = new ArrayList<String>();
            Map finalSelectedItem = selectedItem;
            context.getColumnContextList().forEach(c -> {
                if (CollectionUtils.containsIgnoreCase(item, "ext$")) {
                    ((Map)item.get("ext$")).forEach((k, v) -> item.put((String)k, v));
                }
                if (CollectionUtils.containsIgnoreCase(item, c.getColumnName())) {
                    if (!CollectionUtils.containsIgnoreCase(finalSelectedItem, c.getColumnName())) {
                        updatedColumnList.add(c.getColumnName());
                    } else if (!Objects.equals(c.getValue(item), c.getValue(finalSelectedItem))) {
                        updatedColumnList.add(c.getColumnName());
                    }
                }
            });
            if (!updatedColumnList.isEmpty()) {
                TableContextInstance instance = TableContextInstance.of(context, item);
                instance.getColumnInstanceList().stream().forEach(i -> i.setActive(CollectionUtils.containsIgnoreCase(updatedColumnList, i.getColumnContext().getColumnName())));
                this.mapDaoMapper.updateById(instance);
                if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
                    Map<String, Object> newItem = this.selectById(context, CollectionUtils.getValueIgnorecase(selectedItem, "ID"));
                    this.auditUpdate(context.getTableName(), Arrays.asList(selectedItem), Arrays.asList(newItem));
                }
            }
            if ((service = this.getScriptService(context)) == null) continue;
            service.postUpdate(updatedColumnList, Arrays.asList(CollectionUtils.getValueIgnorecase(selectedItem, "ID")));
        }
    }

    private void auditUpdate(String tableName, List<Map<String, Object>> selectedOldItemList, List<Map<String, Object>> selectedNewItemList) {
        for (Map<String, Object> selectedOldItem : selectedOldItemList) {
            Map selectedNewItem = selectedNewItemList.stream().filter(i -> CollectionUtils.getValueIgnorecase(selectedOldItem, "ID").equals(CollectionUtils.getValueIgnorecase(i, "ID"))).findAny().orElse(null);
            AuditLogHelper.insertAuditLog(tableName, selectedOldItem, selectedNewItem, new String[0]);
        }
    }

    public LcdpBaseService getScriptService(TableContext context) {
        String tableName = context.getTableName();
        String beanName = (String)RedisHelper.get("TABLE_SCRIPT_MAPPING_CACHE", tableName);
        if (!StringUtils.isEmpty(beanName)) {
            return (LcdpBaseService)SpringUtils.getBean(beanName);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> selectList(TableContext context, Map<String, Object> item, List<String> selectColumnList, Order ... orders) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectList(context, item, selectColumnList, orders));
    }

    @Override
    public <ID> Map<String, Object> selectByIdIfPresent(TableContext context, ID id) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectByIdIfPresent(context, id));
    }

    @Override
    public Map<String, Object> selectFirstIfPresent(TableContext context, Map<String, Object> item, Order ... orders) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectFirstIfPresent(context, item, orders));
    }

    @Override
    public Map<String, Object> selectOneIfPresent(TableContext context, Map<String, Object> item, String ... selectColNames) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectOneIfPresent(context, item, selectColNames));
    }

    @Override
    public List<Map<String, Object>> selectList(TableContext context, List<Map<String, Object>> itemList, List<String> searchColumnList, List<String> selectColumnList, Order ... orders) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectList(context, itemList, searchColumnList, selectColumnList, orders));
    }

    @Override
    public <ID> List<Map<String, Object>> selectListByIdList(TableContext context, List<ID> ids, List<String> searchColumnList, Order ... orders) {
        return LcdpWrapperParseUtils.lowerCaseKey(super.selectListByIdList(context, ids, searchColumnList, orders));
    }
}
