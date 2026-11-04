/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpTableExecuteSqlValidator
implements DataValidator {
    @Override
    public boolean doValid(Object ... args) {
        String[] sqls;
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        String sqlText = wrapper.getParamValue("sql");
        for (String sql : sqls = sqlText.split(";")) {
            Pattern pattern;
            Matcher matcher;
            if (StringUtils.isEmpty(sql) || !(matcher = (pattern = Pattern.compile("\\b(insert|select|drop|alter|delete|update|truncate)\\b")).matcher(sql.toLowerCase())).find()) continue;
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLE.EXCEPTION.SQL_NOT_SUPPORT", new String[0]) + ":" + sql);
            return false;
        }
        return true;
    }
}
