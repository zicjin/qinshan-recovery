/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpTableDeleteDataValidator
implements DataValidator {
    @Autowired
    LcdpDatabaseService lcdpDatabaseService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        LcdpTableBean toDeleteTable = wrapper.parseUnique(LcdpTableBean.class);
        if (!LcdpTableUtils.validateDatabaseConsistency(toDeleteTable.getTableName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.DATABASE_NON_CONSISTENCY", toDeleteTable.getTableName()));
            return false;
        }
        if (this.lcdpDatabaseService.isExistPhysicalTable(toDeleteTable.getTableName()) && this.lcdpDatabaseService.isExistData(toDeleteTable.getTableName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.UNABLE_DELETE", new String[0]));
            return false;
        }
        return true;
    }
}
