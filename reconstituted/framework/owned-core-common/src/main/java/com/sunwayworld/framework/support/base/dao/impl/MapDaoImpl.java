/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.dao.impl;

import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapDaoMapperWrapper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JdbcUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
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
public class MapDaoImpl
implements MapDao {
    @Autowired
    private Dialect dialect;
    @Autowired
    private MapDaoMapperWrapper mapDaoMapper;
    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public void insert(TableContext context, Map<String, Object> item) {
        this.preInsert(context, Arrays.asList(item));
        this.mapDaoMapper.insert(TableContextInstance.of(context, item));
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.INSERT)) {
            Object id = CollectionUtils.getValueIgnorecase(item, "id");
            Map<String, Object> selectedItem = this.selectById(context, id);
            AuditLogHelper.insertAuditLog(context.getTableName(), null, selectedItem, new String[0]);
        }
    }

    @Override
    public void insert(TableContext context, List<Map<String, Object>> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        this.preInsert(context, itemList);
        int columnQty = context.getColumnContextList().size();
        int loopItemQty = this.dialect.getMaxParamQty() / columnQty;
        ListChunkIterator<Map<String, Object>> chunkIterator = ListChunkIterator.of(itemList, loopItemQty);
        while (chunkIterator.hasNext()) {
            this.mapDaoMapper.insertList(context, chunkIterator.nextChunk().stream().map(t -> TableContextInstance.of(context, t)).collect(Collectors.toList()));
        }
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.INSERT)) {
            List idList = itemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "id")).collect(Collectors.toList());
            int searchLoopItemQty = this.dialect.getInClauseIdMaxQty();
            ListChunkIterator searchChunkIterator = ListChunkIterator.of(idList, searchLoopItemQty);
            ArrayList<Map<String, Object>> selectedItemList = new ArrayList<Map<String, Object>>();
            while (searchChunkIterator.hasNext()) {
                selectedItemList.addAll(this.selectListByIdList(context, new ArrayList(searchChunkIterator.nextChunk()), new Order[0]));
            }
            for (Map map : selectedItemList) {
                AuditLogHelper.insertAuditLog(context.getTableName(), null, map, new String[0]);
            }
        }
    }

    @Override
    public void fastInsert(TableContext context, List<Map<String, Object>> itemList) {
        boolean auditable;
        if (null == itemList || itemList.isEmpty()) {
            return;
        }
        this.preInsert(context, itemList);
        List<TableContextInstance> contextInstanceList = itemList.stream().map(t -> TableContextInstance.of(context, t)).collect(Collectors.toList());
        GlobalBoundSql insertBoundSql = this.dialect.getBatchInsertSql(context, contextInstanceList);
        this.globalMapper.updateByBoundSql(insertBoundSql);
        String table = context.getTableName();
        if (!ApplicationContextHelper.stressTesting() && (auditable = AuditLogHelper.auditable(AuditDatabaseOperation.INSERT))) {
            List idList = itemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "ID")).collect(Collectors.toList());
            List<Map<String, Object>> selectedItemList = this.selectListByIdList(context, idList, new Order[0]);
            if (AuditLogHelper.auditable(AuditDatabaseOperation.INSERT)) {
                for (Map<String, Object> selectedItem : selectedItemList) {
                    AuditLogHelper.insertAuditLog(table, null, selectedItem, new String[0]);
                }
            }
        }
    }

    @Override
    public <ID> void delete(TableContext context, ID id) {
        if (id == null) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.DELETE_REQUIRE_ID", new String[0]);
        }
        Map<String, Object> selectedItem = this.selectById(context, id);
        if (selectedItem == null) {
            return;
        }
        this.mapDaoMapper.deleteById(context, id);
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.DELETE)) {
            AuditLogHelper.insertAuditLog(context.getTableName(), selectedItem, null, new String[0]);
        }
    }

    @Override
    public <ID> void deleteByIdList(TableContext context, List<ID> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        if (idList.stream().anyMatch(i -> i == null)) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.DELETE_REQUIRE_ID", new String[0]);
        }
        List<Map<String, Object>> selectedItemList = ApplicationContextHelper.stressTesting() ? null : this.selectListByIdList(context, idList, new Order[0]);
        this.mapDaoMapper.deleteByIdList(context, idList);
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.DELETE)) {
            for (Map<String, Object> selectedItem : selectedItemList) {
                AuditLogHelper.insertAuditLog(context.getTableName(), selectedItem, null, new String[0]);
            }
        }
    }

    @Override
    public <ID> void deleteBy(TableContext context, List<Map<String, Object>> itemList, String ... searchColumns) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        if (ArrayUtils.isEmpty(searchColumns) && itemList.stream().anyMatch(m -> m.isEmpty())) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.DELETE_REQUIRE_NONE_EMPTY_BEAN", new String[0]);
        }
        ArrayList idList = new ArrayList();
        if (ArrayUtils.isEmpty(searchColumns)) {
            itemList.stream().forEach(m -> {
                Object id = context.getIdColumnContext().parse(CollectionUtils.getValueIgnorecase(m, "id"));
                if (id != null) {
                    idList.add(id);
                }
            });
        } else {
            idList.addAll(this.selectIdList(context, itemList, ArrayUtils.asList(searchColumns), new Order[0]));
        }
        if (!idList.isEmpty()) {
            this.deleteByIdList(context, idList);
        }
    }

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
        if (ArrayUtils.isEmpty(searchColumns)) {
            oldItemList.add(this.selectById(context, CollectionUtils.getValueIgnorecase(item, "ID")));
            this.mapDaoMapper.updateById(instance);
        } else {
            oldItemList.addAll(this.selectList(context, item, ArrayUtils.asList(searchColumns), new Order[0]));
            idList = this.selectIdList(context, item, ArrayUtils.asList(searchColumns), new Order[0]);
            if (!idList.isEmpty()) {
                this.mapDaoMapper.updateByIdList(instance, idList);
            }
        }
        if (!ApplicationContextHelper.stressTesting() && AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
            idList = oldItemList.stream().map(i -> CollectionUtils.getValueIgnorecase(i, "ID")).collect(Collectors.toList());
            List<Map<String, Object>> newItemList = this.selectListByIdList(context, idList, new Order[0]);
            this.auditUpdate(context.getTableName(), oldItemList, newItemList);
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
    }

    @Override
    public void update(TableContext context, List<Map<String, Object>> itemList, String ... updateColumns) {
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
    }

    @Override
    public void fastUpdate(TableContext context, List<Map<String, Object>> itemList, String ... updateColumns) {
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
        GlobalBoundSql updateBoundSql = this.dialect.getBatchUpdateByIdsSql(context, instanceList, updateColumnList);
        this.globalMapper.updateByBoundSql(updateBoundSql);
        if (!ApplicationContextHelper.stressTesting() && auditable) {
            List<Map<String, Object>> newItemList = this.selectListByIdList(context, idList, new Order[0]);
            this.auditUpdate(context.getTableName(), oldItemList, newItemList);
        }
    }

    @Override
    public void updateIfChanged(TableContext context, List<Map<String, Object>> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Map<String, Object>> selectedItemList = this.selectListByIdList(context, itemList.stream().map(m -> CollectionUtils.getValueIgnorecase(m, "ID")).collect(Collectors.toList()), new Order[0]);
        for (Map<String, Object> item : itemList) {
            Map selectedItem = null;
            Object itemId = CollectionUtils.getValueIgnorecase(item, "ID");
            selectedItem = NumberUtils.isNumber(itemId.toString()) && !itemId.toString().startsWith("0") ? selectedItemList.stream().filter(i -> ConvertUtils.convert(CollectionUtils.getValueIgnorecase(i, "ID"), Long.class).compareTo(ConvertUtils.convert(itemId, Long.class)) == 0).findFirst().orElseThrow(() -> new ApplicationRuntimeException("MYBATIS.EXCEPTION.NO_VALUE_EXISTS", new String[0])) : selectedItemList.stream().filter(i -> CollectionUtils.getValueIgnorecase(i, "ID").equals(itemId)).findFirst().orElseThrow(() -> new ApplicationRuntimeException("MYBATIS.EXCEPTION.NO_VALUE_EXISTS", new String[0]));
            ArrayList updatedColumnList = new ArrayList();
            Map finalSelectedItem = selectedItem;
            context.getColumnContextList().forEach(c -> {
                if (CollectionUtils.containsIgnoreCase(item, "ext$")) {
                    ((Map)item.get("ext$")).forEach((k, v) -> {
                        if (!item.keySet().stream().anyMatch(column -> column.equalsIgnoreCase((String)k))) {
                            item.put((String)k, v);
                        }
                    });
                }
                if (CollectionUtils.containsIgnoreCase(item, c.getColumnName())) {
                    if (!CollectionUtils.containsIgnoreCase(finalSelectedItem, c.getColumnName())) {
                        updatedColumnList.add(c.getColumnName());
                    } else if (!Objects.equals(c.getValue(item), c.getValue(finalSelectedItem))) {
                        updatedColumnList.add(c.getColumnName());
                    }
                }
            });
            if (updatedColumnList.isEmpty()) continue;
            TableContextInstance instance = TableContextInstance.of(context, item);
            instance.getColumnInstanceList().stream().forEach(i -> i.setActive(CollectionUtils.containsIgnoreCase(updatedColumnList, i.getColumnContext().getColumnName())));
            this.mapDaoMapper.updateById(instance);
            if (ApplicationContextHelper.stressTesting() || !AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) continue;
            Map<String, Object> newItem = this.selectById(context, CollectionUtils.getValueIgnorecase(selectedItem, "ID"));
            this.auditUpdate(context.getTableName(), Arrays.asList(selectedItem), Arrays.asList(newItem));
        }
    }

    @Override
    public List<Map<String, Object>> selectList(TableContext context, Map<String, Object> item, List<String> selectColumnList, Order ... orders) {
        if (item == null || item.isEmpty()) {
            return this.mapDaoMapper.selectAll(context, selectColumnList, ArrayUtils.asList(orders));
        }
        TableContextInstance instance = TableContextInstance.of(context, item);
        return this.mapDaoMapper.selectByInstance(instance, selectColumnList, ArrayUtils.asList(orders));
    }

    @Override
    public <ID> Map<String, Object> selectByIdIfPresent(TableContext context, ID id) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByIdList(context, Arrays.asList(id), Collections.emptyList(), Collections.emptyList());
        if (mapList.isEmpty()) {
            return null;
        }
        return mapList.get(0);
    }

    @Override
    public Map<String, Object> selectFirstIfPresent(TableContext context, Map<String, Object> item, Order ... orders) {
        Map<String, Object> map = this.mapDaoMapper.selectFirstByInstance(TableContextInstance.of(context, item), CollectionUtils.emptyList(), ArrayUtils.asList(orders));
        return map;
    }

    @Override
    public Map<String, Object> selectOneIfPresent(TableContext context, Map<String, Object> item, String ... selectColNames) {
        Map<String, Object> map = this.mapDaoMapper.selectFirstByInstance(TableContextInstance.of(context, item), ArrayUtils.asList(selectColNames), CollectionUtils.emptyList());
        return map;
    }

    @Override
    public List<Map<String, Object>> selectList(TableContext context, List<Map<String, Object>> itemList, List<String> searchColumnList, List<String> selectColumnList, Order ... orders) {
        if (CollectionUtils.isEmpty(itemList)) {
            return Collections.emptyList();
        }
        List<TableContextInstance> instanceList = itemList.stream().map(m -> TableContextInstance.of(context, m)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(searchColumnList)) {
            instanceList.forEach(i -> i.getColumnInstanceList().forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColumnList, c.getColumnContext().getColumnName()))));
        }
        return this.mapDaoMapper.selectByInstanceList(context, instanceList, selectColumnList, ArrayUtils.asList(orders));
    }

    @Override
    public <V> List<V> selectColumnList(TableContext context, Map<String, Object> item, List<String> searchColumnList, String colName, Class<V> colType, Order ... orders) {
        TableContextInstance instance = TableContextInstance.of(context, item);
        if (!CollectionUtils.isEmpty(searchColumnList)) {
            instance.getColumnInstanceList().forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColumnList, c.getColumnContext().getColumnName())));
        }
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByInstance(instance, ArrayUtils.asList(colName), ArrayUtils.asList(orders));
        return mapList.parallelStream().map(m -> ConvertUtils.convert(CollectionUtils.getFirstValue(m), colType)).collect(Collectors.toList());
    }

    @Override
    public <ID> List<Map<String, Object>> selectListByIdList(TableContext context, List<ID> idList, List<String> searchColumnList, Order ... orders) {
        if (idList == null || idList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        ArrayList<String> cloneSelectColNames = searchColumnList == null ? new ArrayList<String>() : new ArrayList<String>(searchColumnList);
        boolean loop = idList.size() > this.dialect.getInClauseIdMaxQty();
        int columnQty = context.getColumnContextList().size();
        int loopItemQty = Math.min(this.dialect.getInClauseIdMaxQty(), this.dialect.getMaxParamQty() / columnQty);
        ListChunkIterator<ID> chunkIterator = ListChunkIterator.of(idList, loopItemQty);
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        while (chunkIterator.hasNext()) {
            if (loop) {
                mapList.addAll(this.mapDaoMapper.selectByIdList(context, chunkIterator.nextChunk().stream().map(i -> i).collect(Collectors.toList()), cloneSelectColNames, CollectionUtils.emptyList()));
                continue;
            }
            mapList.addAll(this.mapDaoMapper.selectByIdList(context, chunkIterator.nextChunk().stream().map(i -> i).collect(Collectors.toList()), cloneSelectColNames, ArrayUtils.asList(orders)));
        }
        if (!mapList.isEmpty() && loop && ArrayUtils.hasElement(orders)) {
            this.sort(mapList, orders);
        }
        return mapList;
    }

    @Override
    public <ID, V> V selectColumnById(TableContext context, ID id, String column, Class<V> columnType) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByIdList(context, Arrays.asList(id), Arrays.asList(column), CollectionUtils.emptyList());
        if (mapList.isEmpty()) {
            return null;
        }
        return ConvertUtils.convert(mapList.get(0).values().iterator().next(), columnType);
    }

    private <ID> List<Map<String, Object>> selectMapListByIdList(TableContext context, List<ID> idList, List<String> selectColumnList, Order ... orders) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return this.mapDaoMapper.selectByIdList(context, idList, selectColumnList, ArrayUtils.asList(orders));
    }

    private void auditUpdate(String tableName, List<Map<String, Object>> selectedOldItemList, List<Map<String, Object>> selectedNewItemList) {
        for (Map<String, Object> selectedOldItem : selectedOldItemList) {
            Map selectedNewItem = selectedNewItemList.stream().filter(i -> CollectionUtils.getValueIgnorecase(selectedOldItem, "ID").equals(CollectionUtils.getValueIgnorecase(i, "ID"))).findAny().orElse(null);
            AuditLogHelper.insertAuditLog(tableName, selectedOldItem, selectedNewItem, new String[0]);
        }
    }

    private void preInsert(TableContext context, List<Map<String, Object>> itemList) {
        if (!LocalContextHelper.isUserLogin()) {
            return;
        }
        String createdById = LocalContextHelper.getLoginUserId();
        String createdByName = LocalContextHelper.getLoginUserName();
        String createdByOrgId = LocalContextHelper.getLoginOrgId();
        String createdByOrgName = LocalContextHelper.getLoginOrgName();
        LocalDateTime nowTime = LocalDateTime.now();
        int j = itemList.size();
        for (int i = 0; i < j; ++i) {
            Map<String, Object> item = itemList.get(i);
            if (CollectionUtils.getValueIgnorecase(item, "createdbyid") == null) {
                item.put("createdbyid", createdById);
            }
            if (CollectionUtils.getValueIgnorecase(item, "createdbyname") == null) {
                item.put("createdbyname", createdByName);
            }
            if (CollectionUtils.getValueIgnorecase(item, "createdbyorgid") == null) {
                item.put("createdbyorgid", createdByOrgId);
            }
            if (CollectionUtils.getValueIgnorecase(item, "createdbyorgname") == null) {
                item.put("createdbyorgname", createdByOrgName);
            }
            if (CollectionUtils.getValueIgnorecase(item, "createdtime") != null) continue;
            item.put("createdtime", nowTime);
        }
        if (context.hasOrderColumn()) {
            itemList.forEach(e -> {
                if (CollectionUtils.getValueIgnorecase(e, "orderno") == null) {
                    e.put("orderno", ApplicationContextHelper.getNextOrderNo());
                }
            });
        }
    }

    private void sort(List<Map<String, Object>> mapList, Order ... orders) {
        Collections.sort(mapList, (m1, m2) -> {
            for (Order order : orders) {
                Comparable v1 = (Comparable)CollectionUtils.getValueIgnorecase(m1, order.getColumn());
                Comparable v2 = (Comparable)CollectionUtils.getValueIgnorecase(m2, order.getColumn());
                if (v1 == null) {
                    if (v2 == null) continue;
                    if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                        return -1;
                    }
                    return 1;
                }
                if (v2 == null) {
                    if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                        return 1;
                    }
                    return -1;
                }
                int cv = v1.compareTo(v2);
                if (cv == 0) continue;
                if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                    return cv;
                }
                return -1 * cv;
            }
            return 0;
        });
    }

    @Override
    public int countBy(TableContext context, Map<String, Object> item, String ... searchColNames) {
        TableContextInstance instance = TableContextInstance.of(context, item);
        if (!ArrayUtils.isEmpty(searchColNames)) {
            instance.getColumnInstanceList().forEach(c -> c.setActive(ArrayUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())));
        }
        return this.mapDaoMapper.countByInstance(instance);
    }

    @Override
    public int countBy(TableContext context, List<Map<String, Object>> itemList, String ... searchColNames) {
        List<TableContextInstance> contextInstanceList = itemList.stream().map(i -> TableContextInstance.of(context, i)).collect(Collectors.toList());
        if (!ArrayUtils.isEmpty(searchColNames)) {
            contextInstanceList.forEach(i -> i.getColumnInstanceList().forEach(c -> c.setActive(ArrayUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName()))));
        }
        return this.mapDaoMapper.countUnionByInstanceList(context, contextInstanceList);
    }

    @Override
    public void updateCreatedBy(TableContext context, List<Map<String, Object>> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<String> updateColumnList = Arrays.asList("createdbyid", "createdbyname", "createdbyorgid", "createdbyorgname", "createdtime");
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
    }
}
