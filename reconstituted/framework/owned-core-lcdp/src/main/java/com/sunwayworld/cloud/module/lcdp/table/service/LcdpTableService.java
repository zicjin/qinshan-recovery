/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;
import java.util.Map;

public interface LcdpTableService
extends GenericService<LcdpTableBean, Long>,
GenericChoosableService<LcdpTableBean, Long> {
    public List<LcdpTableTreeNodeDTO> selectTableTree(RestJsonWrapperBean var1);

    public LcdpTableDTO selectTableInfo(Long var1);

    public List<LcdpTableDTO> selectTableInfoList(List<Long> var1);

    public LcdpTableDTO selectPhysicalTableInfo(String var1);

    public List<LcdpTableDTO> selectPhysicalTableInfoList(List<String> var1);

    public List<LcdpTableBean> selectPhysicalTableNameList(RestJsonWrapperBean var1);

    public List<LcdpTableBean> selectVirtualTableNameList(RestJsonWrapperBean var1);

    public List<LcdpTableFieldBean> selectPhysicalFieldList(RestJsonWrapperBean var1);

    public Long design(String var1);

    public void checkout(String var1);

    public void submit(List<LcdpTableBean> var1);

    public List<String> revert(List<String> var1);

    public LcdpTableCompareDTO<LcdpTableBean> compare(RestJsonWrapperBean var1);

    public RestValidationResultBean validateTableName(String var1);

    public RestValidationResultBean validateIndexName(String var1);

    public Map<String, LcdpAnalysisResultDTO> analysisTableInfo(List<String> var1, Map<String, String> var2);

    public String createPhysicalTable(LcdpTableBean var1, List<LcdpTableFieldBean> var2, List<LcdpTableIndexBean> var3);

    public String alterPhysicalTable(LcdpTableDTO var1, LcdpTableBean var2, List<LcdpTableFieldBean> var3, List<LcdpTableIndexBean> var4);

    public String dropPhysicalTable(LcdpTableBean var1);

    public String generateCreateSql(LcdpTableDTO var1);

    public List<LcdpTableViewDTO> selectPhysicalTableViewNameList(RestJsonWrapperBean var1);

    public LcdpTableDTO executeSql(RestJsonWrapperBean var1);

    public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean var1);

    public List<LcdpTableFieldBean> selectPhysicalFieldSelectableList();
}
