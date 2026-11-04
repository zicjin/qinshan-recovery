/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpViewSaveDataValidator
implements DataValidator {
    @Autowired
    LcdpDatabaseService lcdpDatabaseService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[args.length - 1];
        LcdpViewBean view = wrapper.parseUnique(LcdpViewBean.class);
        if (!LcdpTableUtils.validateViewConsistency(view.getViewName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.DATABASE_NON_CONSISTENCY", view.getViewName()));
            return false;
        }
        try {
            this.lcdpDatabaseService.testSelectStatement(view.getSelectStatement());
        }
        catch (CheckedException ex) {
            this.addConstraintViolation(I18nHelper.getMessage(ex.getMessage(), new String[0]));
            return false;
        }
        return true;
    }
}
