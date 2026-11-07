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
    @Autowired(required = false)
    private com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService.ReadScope.Provider auditReadScope;

    private String readOrganization() {
        return com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService.ReadScope.organization(auditReadScope);
    }

    private com.sunwayworld.framework.mybatis.mapper.MapperParameter scoped(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter) {
        String organization=readOrganization();
        if (organization != null) parameter.put("ownedAuditOrgId",organization);
        return parameter;
    }

    @Override
    @Transactional(readOnly = true)
    public com.sunwayworld.framework.data.page.Page<CoreAuditTrailRecordLineBean> selectPagination(
            com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, com.sunwayworld.framework.mybatis.page.PageRowBounds bounds) {
        return CoreAuditTrailRecordLineService.super.selectPagination(scoped(parameter),bounds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoreAuditTrailRecordLineBean> selectList(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter) {
        return CoreAuditTrailRecordLineService.super.selectList(scoped(parameter));
    }

    @Override
    @Transactional(readOnly = true)
    public CoreAuditTrailRecordLineBean selectByIdIfPresent(Long id) {
        if (readOrganization() == null) return CoreAuditTrailRecordLineService.super.selectByIdIfPresent(id);
        List<CoreAuditTrailRecordLineBean> rows=selectListByFilter(com.sunwayworld.framework.mybatis.mapper.SearchFilter.instance()
                .match("ID",id).filter(com.sunwayworld.framework.mybatis.mapper.MatchPattern.EQ),new Order[0]);
        return rows.isEmpty() ? null : rows.get(0);
    }

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
    @Transactional(readOnly = true)
    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(Long recordId) {
        List<String> tableNameList;
        if (readOrganization() == null) tableNameList=this.getDao().selectTableNameByRecordId(recordId + "");
        else tableNameList=selectListByFilter(com.sunwayworld.framework.mybatis.mapper.SearchFilter.instance()
                .match("RECORDID",recordId).filter(com.sunwayworld.framework.mybatis.mapper.MatchPattern.EQ),new Order[0])
                .stream().map(CoreAuditTrailRecordLineBean::getTableName).distinct().collect(java.util.stream.Collectors.toList());
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
    @Transactional(readOnly = true)
    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(Long recordId, String tableName) {
        CoreAuditTrailRecordLineBean recordLine = new CoreAuditTrailRecordLineBean();
        recordLine.setRecordId(recordId);
        recordLine.setTableName(tableName);
        List<CoreAuditTrailRecordLineBean> lineList;
        if (readOrganization() == null) lineList=this.getDao().selectList(recordLine, Order.asc("TARGETID"));
        else lineList=selectListByFilter(com.sunwayworld.framework.mybatis.mapper.SearchFilter.instance()
                .match("RECORDID",recordId).filter(com.sunwayworld.framework.mybatis.mapper.MatchPattern.EQ)
                .match("TABLENAME",tableName).filter(com.sunwayworld.framework.mybatis.mapper.MatchPattern.EQ),Order.asc("TARGETID"));
        if (lineList.isEmpty()) {
            return null;
        }
        return lineList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> targetIds, String nameField) {
        if (StringUtils.isBlank(nameField) || CollectionUtils.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        return this.getDao().selectDeletedNames(targetIds, nameField, readOrganization());
    }
}
