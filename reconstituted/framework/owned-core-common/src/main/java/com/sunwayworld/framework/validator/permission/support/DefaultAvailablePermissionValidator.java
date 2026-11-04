/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.permission.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.permission.PermissionValidator;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTablePermissionBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultAvailablePermissionValidator
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
        return true;
    }

    private boolean checkPermission(CoreTablePermissionBean tablePermission, Object id) {
        if ("1".equals(tablePermission.getCheckAvailable())) {
            Object service = ApplicationContextHelper.getBean(ClassUtils.getClass(tablePermission.getServiceClass()));
            if (!StringUtils.isEmpty(tablePermission.getCheckAvailableMethod())) {
                Method checkAvailableMethod = ReflectionUtils.findMethodWithReturnType(service.getClass(), tablePermission.getCheckAvailableMethod(), Boolean.TYPE);
                if (checkAvailableMethod == null) {
                    logger.warn("Not specified check available method in table permission with table:" + tablePermission.getTableName() + " and category: " + tablePermission.getCategory() + ".");
                } else if (!((Boolean)ReflectionUtils.invokeMethod(checkAvailableMethod, service, id)).booleanValue()) {
                    this.addConstraintViolation("core.validator.unauthorized.unavailable");
                    return false;
                }
            }
        }
        return true;
    }
}
