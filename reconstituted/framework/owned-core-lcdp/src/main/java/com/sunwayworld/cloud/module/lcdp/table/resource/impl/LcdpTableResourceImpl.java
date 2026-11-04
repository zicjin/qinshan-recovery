/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.resource.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.cloud.module.lcdp.table.resource.LcdpTableResource;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpTableExecuteSqlValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8868")
@RestController
@GikamBean
public class LcdpTableResourceImpl
implements LcdpTableResource,
AbstractGenericResource<LcdpTableService, LcdpTableBean, Long>,
AbstractGenericChoosableResource<LcdpTableService, LcdpTableBean, Long> {
    @Autowired
    private LcdpTableService lcdpTableService;

    @Override
    public LcdpTableService getService() {
        return this.lcdpTableService;
    }

    @Override
    @Log(value="\u7269\u7406\u8868\u6811\u67e5\u8be2", type=LogType.SELECT)
    public List<LcdpTableTreeNodeDTO> selectTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectTableTree(wrapper);
    }

    @Override
    @Log(value="\u7269\u7406\u8868\u540d\u67e5\u8be2\u4e0b\u62c9\u6846", type=LogType.SELECT)
    public List<LcdpTableBean> selectPhysicalTableNameList(RestJsonWrapperBean wrapper) {
        return this.getService().selectPhysicalTableNameList(wrapper);
    }

    @Override
    @Log(value="\u865a\u62df\u8868\u540d\u67e5\u8be2\u4e0b\u62c9\u6846", type=LogType.SELECT)
    public List<LcdpTableBean> selectVirtualTableNameList(RestJsonWrapperBean wrapper) {
        return this.getService().selectVirtualTableNameList(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8868\u540d\u79f0\u67e5\u8be2\u7269\u7406\u8868\u4fe1\u606f", type=LogType.SELECT)
    public LcdpTableDTO selectPhysicalTableInfo(@PathVariable String tableName) {
        return this.getService().selectPhysicalTableInfo(tableName);
    }

    @Override
    @Log(value="\u6839\u636e\u8868\u540d\u79f0\u67e5\u8be2\u7248\u672c\u8868\u4fe1\u606f", type=LogType.SELECT)
    public LcdpTableDTO selectTableInfo(@PathVariable Long id) {
        return this.getService().selectTableInfo(id);
    }

    @Override
    @Log(value="\u67e5\u8be2\u7269\u7406\u8868\u5b57\u6bb5\u4fe1\u606f", type=LogType.SELECT)
    public List<LcdpTableFieldBean> selectPhysicalFieldList(RestJsonWrapperBean wrapper) {
        return this.getService().selectPhysicalFieldList(wrapper);
    }

    @Override
    @Log(value="\u7269\u7406\u8868\u5b57\u6bb5\u4e0b\u62c9\u6846\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public List<LcdpTableFieldBean> selectPhysicalFieldSelectableList() {
        return this.getService().selectPhysicalFieldSelectableList();
    }

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8bbe\u8ba1\u8868", type=LogType.INSERT)
    public Long design(@PathVariable String tableName) {
        return this.getService().design(tableName);
    }

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u68c0\u51fa\u8868", type=LogType.INSERT)
    public void checkout(@PathVariable String tableName) {
        this.getService().checkout(tableName);
    }

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u8868\u5bf9\u6bd4", type=LogType.SELECT)
    public LcdpTableCompareDTO<LcdpTableBean> compare(RestJsonWrapperBean wrapper) {
        return this.getService().compare(wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027,\u8868\u540d\u662f\u5426\u53ef\u4ee5\u4f7f\u7528", type=LogType.VALIDATE)
    public RestValidationResultBean validateTableNameUnique(@PathVariable(required=false) String tableName) {
        return this.getService().validateTableName(tableName);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027,\u7d22\u5f15\u540d\u662f\u5426\u53ef\u4ee5\u4f7f\u7528", type=LogType.VALIDATE)
    public RestValidationResultBean validateIndexNameUnique(@PathVariable(required=false) String indexName) {
        return this.getService().validateIndexName(indexName);
    }

    @Override
    @Log(value="\u67e5\u8be2\u8868\u6216\u89c6\u56fe", type=LogType.SELECT)
    public List<LcdpTableViewDTO> selectTableViewList(RestJsonWrapperBean wrapper) {
        return this.getService().selectPhysicalTableViewNameList(wrapper);
    }

    @Override
    @Log(value="SQL\u811a\u672c\u6267\u884c", type=LogType.INSERT)
    @ValidateDataWith(value=LcdpTableExecuteSqlValidator.class)
    public LcdpTableDTO executeSql(RestJsonWrapperBean wrapper) {
        return this.getService().executeSql(wrapper);
    }

    @Override
    @Log(value="\u52a8\u6001SQL\u67e5\u8be2", type=LogType.SELECT)
    public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean wrapper) {
        return this.getService().dynamicQuery(wrapper);
    }
}
