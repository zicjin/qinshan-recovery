/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.validator;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.permission.PermissionValidator;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTablePermissionBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreOrgDeleteValidator
implements PermissionValidator {
    @Autowired
    private CoreTableService tableService;

    @Override
    public boolean doValid(String table, String category, Object id) {
        String loopTable = table;
        while (loopTable != null) {
            CoreTablePermissionBean tablePermission = this.tableService.selectTablePermission(loopTable, category);
            if (tablePermission != null && !this.checkPermission(tablePermission, id)) {
                return false;
            }
            CoreTableHierarchyBean tableHierarchy = this.tableService.selectTableHierarchy(loopTable, id);
            if (tableHierarchy == null) {
                loopTable = null;
                continue;
            }
            loopTable = tableHierarchy.getMasterTableName();
            id = this.tableService.selectMasterId(tableHierarchy, (Serializable)id);
        }
        CoreOrgService coreOrgService = ApplicationContextHelper.getBean(CoreOrgService.class);
        List orgList = coreOrgService.selectListByFilter(SearchFilter.instance().match("PARENTID", id).filter(MatchPattern.EQ), new Order[0]);
        if (!orgList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.MDM.ORGS.TIP.FORBID-DELETE");
            return false;
        }
        return true;
    }

    private boolean checkPermission(CoreTablePermissionBean tablePermission, Object id) {
        if ("1".equals(tablePermission.getCheckUpdatable())) {
            Object service = ApplicationContextHelper.getBean(ClassUtils.getClass(tablePermission.getServiceClass()));
            if (!StringUtils.isEmpty(tablePermission.getCheckUpdatableMethod())) {
                Method checkUpdatableMethod = ReflectionUtils.findMethodWithReturnType(service.getClass(), tablePermission.getCheckUpdatableMethod(), Boolean.TYPE);
                if (checkUpdatableMethod == null) {
                    logger.warn("Not specified check updatable method in table permission with table:" + tablePermission.getTableName() + " and category: " + tablePermission.getCategory() + ".");
                } else if (!((Boolean)ReflectionUtils.invokeMethod(checkUpdatableMethod, service, id)).booleanValue()) {
                    this.addConstraintViolation("core.validator.unauthorized.noupdatable");
                    return false;
                }
            }
        }
        return true;
    }
}
