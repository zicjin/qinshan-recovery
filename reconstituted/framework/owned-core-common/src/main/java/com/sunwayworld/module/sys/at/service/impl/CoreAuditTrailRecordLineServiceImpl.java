/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailRecordLineDao;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAuditTrailRecordLineServiceImpl
implements CoreAuditTrailRecordLineService {
    @Autowired
    private CoreAuditTrailRecordLineDao recordLineDao;

    @Override
    public CoreAuditTrailRecordLineDao getDao() {
        return this.recordLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insert(List<CoreAuditTrailRecordLineBean> lineList) {
        int qty = lineList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            lineList.get(i).setId(idList.get(i));
        }
        this.getDao().insert(lineList);
    }

    @Override
    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(Long recordId) {
        List<String> tableNameList = this.getDao().selectTableNameByRecordId(recordId + "");
        if (tableNameList.isEmpty()) {
            return null;
        }
        ArrayList<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<CoreAuditTrailRecordLineBean>();
        tableNameList.forEach(e -> {
            CoreAuditTrailRecordLineBean recordLine = new CoreAuditTrailRecordLineBean();
            recordLine.setTableName((String)e);
            recordLine.setRecordId(NumberUtils.parseLong(recordId));
            recordLineList.add(recordLine);
        });
        return recordLineList;
    }

    @Override
    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(Long recordId, String tableName) {
        CoreAuditTrailRecordLineBean recordLine = new CoreAuditTrailRecordLineBean();
        recordLine.setRecordId(recordId);
        recordLine.setTableName(tableName);
        List<CoreAuditTrailRecordLineBean> lineList = this.getDao().selectList(recordLine, Order.asc("TARGETID"));
        if (lineList.isEmpty()) {
            return null;
        }
        return lineList;
    }

    @Override
    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> targetIds, String nameField) {
        if (StringUtils.isBlank(nameField) || CollectionUtils.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        return this.getDao().selectDeletedNames(targetIds, nameField);
    }
}
