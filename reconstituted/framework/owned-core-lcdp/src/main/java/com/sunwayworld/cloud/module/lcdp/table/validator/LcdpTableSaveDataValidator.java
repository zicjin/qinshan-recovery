/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpTableSaveDataValidator
implements DataValidator {
    @Autowired
    LcdpDatabaseService lcdpDatabaseService;

    @Override
    public boolean doValid(Object ... args) {
        HttpServletRequest request;
        String constraintViolation;
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[args.length - 1];
        LcdpTableBean lcdpTable = wrapper.parseUnique(LcdpTableBean.class);
        List<LcdpTableFieldBean> fieldList = wrapper.parse(LcdpTableFieldBean.class);
        List<LcdpTableIndexBean> indexList = wrapper.parse(LcdpTableIndexBean.class);
        if (!LcdpTableUtils.validateDatabaseConsistency(lcdpTable.getTableName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.DATABASE_NON_CONSISTENCY", lcdpTable.getTableName()));
            return false;
        }
        boolean isExistPhysicalTable = this.lcdpDatabaseService.isExistPhysicalTable(lcdpTable.getTableName());
        LcdpTableDTO physicalTable = null;
        if (isExistPhysicalTable) {
            physicalTable = this.lcdpDatabaseService.selectPhysicalTableInfo(lcdpTable.getTableName());
        }
        if (!isExistPhysicalTable && LcdpTableUtils.isReservedWord(lcdpTable.getTableName())) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.TABLES.NAME.TIP.INVALID", new String[0]));
        }
        fieldList.forEach(fieldBean -> fieldBean.setTableName(lcdpTable.getTableName()));
        indexList.forEach(indexBean -> indexBean.setTableName(lcdpTable.getTableName()));
        LcdpTableDTO lcdpTableDTO = LcdpTableUtils.parseTableBeanToDTO(ArrayUtils.asList(lcdpTable), fieldList, indexList).get(0);
        List<LcdpTableFieldBean> fieldOpsList = LcdpTableUtils.analyzeFieldOps(lcdpTableDTO.getFieldList(), isExistPhysicalTable ? physicalTable.getFieldList() : null);
        List<LcdpTableIndexBean> indexOpsList = LcdpTableUtils.analyzeIndexOps(lcdpTableDTO.getIndexList(), isExistPhysicalTable ? physicalTable.getIndexList() : null);
        LcdpAnalysisResultDTO validationReuslt = LcdpTableUtils.validateTable(physicalTable, lcdpTableDTO, fieldOpsList, indexOpsList);
        if (!validationReuslt.getEnable().booleanValue()) {
            validationReuslt.getAnalysisResultList().forEach(result -> this.addConstraintViolation(result + ";"));
        }
        return (constraintViolation = (String)(request = ServletUtils.getCurrentRequest()).getAttribute("validator.constraintViolation")) == null;
    }
}
