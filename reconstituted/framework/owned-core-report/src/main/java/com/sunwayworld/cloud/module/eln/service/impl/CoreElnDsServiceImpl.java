/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDsDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsFieldService;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreElnDsServiceImpl
implements CoreElnDsService {
    @Autowired
    private CoreElnDsDao coreElnDsDao;
    @Autowired
    private CoreElnDsFieldService dsFieldService;

    @Override
    public CoreElnDsDao getDao() {
        return this.coreElnDsDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreElnDsBean elnDs = wrapper.parseUnique(CoreElnDsBean.class);
        elnDs.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(elnDs);
        return elnDs.getId();
    }

    @Override
    @Transactional
    public RestValidationResultBean updateSqlIfValid(Long id, RestJsonWrapperBean wrapper) {
        CoreElnDsBean elnDs = wrapper.parseUnique(CoreElnDsBean.class);
        String sqlContent = elnDs.getSqlContent();
        CoreElnDsFieldBean filter = new CoreElnDsFieldBean();
        filter.setElnDsId(id);
        if (StringUtils.isBlank(sqlContent)) {
            this.getDao().updateIfChanged(elnDs);
            this.dsFieldService.getDao().deleteBy((CoreElnDsFieldBean)filter, "elnDsId");
            return new RestValidationResultBean(true);
        }
        sqlContent = sqlContent.replaceAll("('?#)[^#]*(#'?)", "NULL");
        ArrayList insertFieldList = new ArrayList();
        ArrayList deleteFieldList = new ArrayList();
        try {
            if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.id}")) {
                sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.id}", "''");
            }
            if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.orgId}")) {
                sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.orgId}", "''");
            }
            if (StringUtils.containsIgnoreCase(sqlContent, "{loginOrgId}")) {
                sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginOrgId}", "''");
            }
            if (StringUtils.containsIgnoreCase(sqlContent, "{loginRoleId}")) {
                sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginRoleId}", "''");
            }
            List<ColumnContext> sqlColumnList = DatabaseManager.selectColumnContextList(sqlContent);
            List<CoreElnDsFieldBean> oldFieldList = this.dsFieldService.selectList(filter, new Order[0]);
            sqlColumnList.stream().filter(c -> !StringUtils.contains(c.getRawColumnName().toUpperCase(), "_ID") && !StringUtils.equals(c.getRawColumnName().toUpperCase(), "ID") && !StringUtils.contains(c.getRawColumnName().toUpperCase(), "_RID") && oldFieldList.stream().noneMatch(f -> {
                String columnName = c.getRawColumnName();
                if (columnName.indexOf("_") > 0) {
                    columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
                }
                return f.getElnColumnName().equalsIgnoreCase(columnName);
            })).forEach(c -> {
                CoreElnDsFieldBean elnDsField = new CoreElnDsFieldBean();
                elnDsField.setId(ApplicationContextHelper.getNextIdentity());
                elnDsField.setElnDsId(id);
                String columnName = c.getRawColumnName();
                if (columnName.indexOf("_") > 0) {
                    columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
                }
                elnDsField.setElnColumnName(columnName);
                insertFieldList.add(elnDsField);
            });
            deleteFieldList.addAll(oldFieldList.stream().filter(f -> sqlColumnList.stream().noneMatch(c -> {
                String columnName = c.getRawColumnName();
                if (columnName.indexOf("_") > 0) {
                    columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
                }
                return columnName.equalsIgnoreCase(f.getElnColumnName());
            })).collect(Collectors.toList()));
        }
        catch (JdbcException je) {
            return new RestValidationResultBean(false, je.getCause().getMessage());
        }
        if ("dropDown".equals(elnDs.getDisplayCategory())) {
            this.dsFieldService.getDao().deleteBy((CoreElnDsFieldBean)filter, "elnDsId");
        } else {
            this.dsFieldService.getDao().deleteBy(deleteFieldList, new String[0]);
            this.dsFieldService.getDao().insert(insertFieldList);
        }
        this.getDao().updateIfChanged(elnDs);
        return new RestValidationResultBean(true);
    }

    @Override
    @Cacheable(value={"T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID"}, key="#id")
    public List<CoreElnDsFieldBean> selectDsFieldListById(Long id) {
        CoreElnDsFieldBean filter = new CoreElnDsFieldBean();
        filter.setElnDsId(id);
        return this.dsFieldService.selectList(filter, Order.desc("ID"));
    }
}
