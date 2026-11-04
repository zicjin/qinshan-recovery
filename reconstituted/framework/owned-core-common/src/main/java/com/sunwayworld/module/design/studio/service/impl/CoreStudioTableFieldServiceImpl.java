/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioTableFieldDao;
import com.sunwayworld.module.design.studio.service.CoreStudioTableFieldService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreStudioTableFieldServiceImpl
implements CoreStudioTableFieldService {
    @Autowired
    private CoreStudioTableFieldDao tableFieldDao;
    @Lazy
    @Autowired
    private CoreStudioTableFieldService proxyInstance;

    @Override
    public CoreStudioTableFieldDao getDao() {
        return this.tableFieldDao;
    }

    @Override
    @Cacheable(value={"T_CORE_STUDIO_TABLE_FIELD$TABLE"}, key="#table", unless="#result == null")
    public List<CoreStudioTableFieldBean> selectListByTable(String table) {
        return this.getDao().selectListByOneColumnValue(table, "TABLENAME", new Order[0]);
    }

    @Override
    @Transactional
    public void reset(List<CoreStudioTableFieldBean> tableFieldList) {
        Map<String, List<CoreStudioTableFieldBean>> map = tableFieldList.stream().collect(Collectors.groupingBy(f -> f.getTableName()));
        ArrayList<CoreStudioTableFieldBean> insertList = new ArrayList<CoreStudioTableFieldBean>();
        ArrayList<CoreStudioTableFieldBean> updateList = new ArrayList<CoreStudioTableFieldBean>();
        ArrayList<CoreStudioTableFieldBean> deleteList = new ArrayList<CoreStudioTableFieldBean>();
        for (Map.Entry<String, List<CoreStudioTableFieldBean>> entry : map.entrySet()) {
            List<CoreStudioTableFieldBean> selectedFieldList = this.proxyInstance.selectListByTable(entry.getKey());
            for (CoreStudioTableFieldBean tableField : entry.getValue()) {
                CoreStudioTableFieldBean selectedField = selectedFieldList.stream().filter(f -> f.getField().equalsIgnoreCase(tableField.getField())).findAny().orElse(null);
                if (selectedField == null) {
                    tableField.setId(ApplicationContextHelper.getNextIdentity());
                    insertList.add(tableField);
                    continue;
                }
                if (!selectedField.getConfig().equalsIgnoreCase(tableField.getConfig())) {
                    tableField.setId(selectedField.getId());
                    updateList.add(tableField);
                }
                selectedFieldList.removeIf(f -> f.getField().equalsIgnoreCase(tableField.getField()));
            }
            if (selectedFieldList.isEmpty()) continue;
            deleteList.addAll(selectedFieldList);
        }
        if (!deleteList.isEmpty()) {
            this.getDao().deleteBy(deleteList, new String[0]);
        }
        if (!insertList.isEmpty()) {
            this.getDao().insert(insertList);
        }
        if (!updateList.isEmpty()) {
            this.getDao().update(updateList, new String[]{"CONFIG"});
        }
    }
}
