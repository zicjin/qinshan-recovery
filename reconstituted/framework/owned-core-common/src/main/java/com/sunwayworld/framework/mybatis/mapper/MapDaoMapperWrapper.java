/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapDaoMapper;
import com.sunwayworld.framework.mybatis.service.MapDaoSupportService;
import com.sunwayworld.framework.utils.TransactionUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapDaoMapperWrapper
implements MapDaoMapper {
    @Autowired
    private MapDaoMapper mapDaoMapper;
    @Autowired
    private MapDaoSupportService mapDaoSupportService;
    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public void insert(TableContextInstance instance) {
        this.mapDaoSupportService.preInsert(instance);
        this.mapDaoMapper.insert(instance);
    }

    @Override
    public void insertList(TableContext context, List<TableContextInstance> instanceList) {
        this.mapDaoSupportService.preInsertList(context, instanceList);
        this.mapDaoMapper.insertList(context, instanceList);
    }

    @Override
    public void updateById(TableContextInstance instance) {
        this.mapDaoSupportService.preUpdateById(instance);
        this.mapDaoMapper.updateById(instance);
    }

    @Override
    public <ID> void updateByIdList(TableContextInstance instance, List<ID> idList) {
        this.mapDaoSupportService.preUpdateByIdList(instance, idList);
        this.mapDaoMapper.updateByIdList(instance, idList);
    }

    @Override
    public void updateItemList(TableContext context, List<TableContextInstance> instanceList, List<String> updateColumnList) {
        this.mapDaoSupportService.preUpdateByInstanceList(context, instanceList, updateColumnList);
        this.mapDaoMapper.updateItemList(context, instanceList, updateColumnList);
    }

    @Override
    public <ID> void deleteById(TableContext context, ID id) {
        this.mapDaoMapper.deleteById(context, id);
    }

    @Override
    public <ID> void deleteByIdList(TableContext context, List<ID> idList) {
        this.mapDaoMapper.deleteByIdList(context, idList);
    }

    @Override
    public <ID> List<Map<String, Object>> selectByIdList(TableContext context, List<ID> idList, List<String> selectColumnList, List<Order> orderList) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByIdList(context, idList, selectColumnList, orderList);
        if (!mapList.isEmpty()) {
            mapList.forEach(m -> MybatisHelper.correctValue(context.getColumnContextList(), (Map<String, Object>)m));
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> selectByInstance(TableContextInstance instance, List<String> selectColumnList, List<Order> orderList) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByInstance(instance, selectColumnList, orderList);
        if (!mapList.isEmpty()) {
            mapList.forEach(m -> MybatisHelper.correctValue(instance.getContext().getColumnContextList(), (Map<String, Object>)m));
        }
        return mapList;
    }

    @Override
    public Map<String, Object> selectFirstByInstance(TableContextInstance instance, List<String> selectColumnList, List<Order> orderList) {
        Map<String, Object> map = this.mapDaoMapper.selectFirstByInstance(instance, selectColumnList, orderList);
        if (map != null) {
            MybatisHelper.correctValue(instance.getContext().getColumnContextList(), map);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> selectByInstanceList(TableContext context, List<TableContextInstance> instanceList, List<String> selectColumnList, List<Order> orderList) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectByInstanceList(context, instanceList, selectColumnList, orderList);
        if (!mapList.isEmpty()) {
            mapList.forEach(m -> MybatisHelper.correctValue(context.getColumnContextList(), (Map<String, Object>)m));
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> selectUnionByInstance(TableContextInstance instance, List<String> selectColumnList, List<Order> orderList) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectUnionByInstance(instance, selectColumnList, orderList);
        if (!mapList.isEmpty()) {
            mapList.forEach(m -> MybatisHelper.correctValue(instance.getContext().getColumnContextList(), (Map<String, Object>)m));
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> selectAll(TableContext context, List<String> selectColumnList, List<Order> orderList) {
        List<Map<String, Object>> mapList = this.mapDaoMapper.selectAll(context, selectColumnList, orderList);
        if (!mapList.isEmpty()) {
            mapList.forEach(m -> MybatisHelper.correctValue(context.getColumnContextList(), (Map<String, Object>)m));
        }
        return mapList;
    }

    @Override
    public Cursor<Map<String, Object>> selectAllWithCursor(TableContext context, List<String> selectColumnList, List<Order> orderList) {
        TransactionUtils.transactionRequired();
        return new CursorWrapper(context, this.mapDaoMapper.selectAllWithCursor(context, selectColumnList, orderList));
    }

    @Override
    public int countByInstance(TableContextInstance instance) {
        return this.mapDaoMapper.countByInstance(instance);
    }

    @Override
    public int countUnionByInstanceList(TableContext context, List<TableContextInstance> instanceList) {
        return this.mapDaoMapper.countUnionByInstanceList(context, instanceList);
    }

    private static class CursorWrapper
    implements Cursor<Map<String, Object>> {
        private final TableContext context;
        private final Cursor<Map<String, Object>> cursor;

        public CursorWrapper(TableContext context, Cursor<Map<String, Object>> cursor) {
            this.context = context;
            this.cursor = cursor;
        }

        @Override
        public void close() throws IOException {
            this.cursor.close();
        }

        @Override
        public Iterator<Map<String, Object>> iterator() {
            return new IteratorWrapper(this.context, this.cursor.iterator());
        }

        @Override
        public boolean isOpen() {
            return this.cursor.isOpen();
        }

        @Override
        public boolean isConsumed() {
            return this.cursor.isConsumed();
        }

        @Override
        public int getCurrentIndex() {
            return this.cursor.getCurrentIndex();
        }

        private static class IteratorWrapper
        implements Iterator<Map<String, Object>> {
            private final TableContext context;
            private final Iterator<Map<String, Object>> iterator;

            public IteratorWrapper(TableContext context, Iterator<Map<String, Object>> iterator) {
                this.context = context;
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                Map<String, Object> map = this.iterator.next();
                if (map != null) {
                    MybatisHelper.correctValue(this.context.getColumnContextList(), map);
                }
                return map;
            }
        }
    }
}
