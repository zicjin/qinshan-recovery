/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.validator;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplChildTableDTO;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplDTO;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcpdResourceCreateableByTmplValidator
implements DataValidator {
    @Override
    public boolean doValid(Object ... args) {
        LcdpModuleTmplDTO moduleTmplDTO = ((RestJsonWrapperBean)args[0]).parseUnique(LcdpModuleTmplDTO.class);
        String masterTableName = moduleTmplDTO.getMasterTableName();
        List<String> tableNameList = moduleTmplDTO.getTableNameList();
        List<LcdpModuleTmplChildTableDTO> childTableList = moduleTmplDTO.getChildTableList();
        if (!StringUtils.isEmpty(masterTableName) && this.validateTableName(masterTableName)) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.TABLENAME_NOT_ALLOWED_UNDERLINE", new String[0]));
            return false;
        }
        if (!ObjectUtils.isEmpty(tableNameList) && tableNameList.stream().filter(tableName -> this.validateTableName((String)tableName)).collect(Collectors.toList()).size() > 0) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.TABLENAME_NOT_ALLOWED_UNDERLINE", new String[0]));
            return false;
        }
        if (!ObjectUtils.isEmpty(childTableList) && childTableList.stream().filter(table -> this.validateTableName(table.getTableName())).collect(Collectors.toList()).size() > 0) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.TABLENAME_NOT_ALLOWED_UNDERLINE", new String[0]));
            return false;
        }
        return true;
    }

    private boolean validateTableName(String tableName) {
        return tableName.endsWith("_") || tableName.endsWith("-");
    }
}
