/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpViewDeleteDataValidator
implements DataValidator {
    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[args.length - 1];
        LcdpViewBean view = wrapper.parseUnique(LcdpViewBean.class);
        if (!LcdpTableUtils.validateViewConsistency(view.getViewName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.DATABASE_NON_CONSISTENCY", view.getViewName()));
            return false;
        }
        return true;
    }
}
