/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.service;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;
import java.util.Map;

public interface LcdpDatabaseService {
    public List<LcdpViewBean> selectPhysicalViewInfoList(MapperParameter var1);

    public LcdpViewBean selectPhysicalViewInfo(String var1);

    public List<LcdpViewBean> selectPhysicalViewInfoList(List<String> var1);

    public LcdpTableDTO selectPhysicalTableInfo(String var1);

    public List<LcdpTableDTO> selectPhysicalTableInfoList(List<String> var1);

    public List<LcdpTableBean> selectPhysicalTableNameList(MapperParameter var1);

    public List<LcdpTableBean> selectPhysicalTableInfoList(MapperParameter var1);

    public List<LcdpTableFieldBean> selectPhysicalFieldList(MapperParameter var1);

    public List<LcdpTableFieldBean> selectPhysicalFieldList(String var1);

    public List<LcdpTableIndexBean> selectPhysicalIndexList(String var1);

    public boolean isExistData(String var1);

    public boolean isExistPhysicalTable(String var1);

    public boolean isExistPhysicalView(String var1);

    public boolean isExistNullDataInColumn(String var1, String var2);

    public boolean isExistNotNullDataInColumn(String var1, String var2);

    public String createPhysicalTable(LcdpTableBean var1, List<LcdpTableFieldBean> var2, List<LcdpTableIndexBean> var3);

    public String alterPhysicalTable(LcdpTableDTO var1, LcdpTableBean var2, List<LcdpTableFieldBean> var3, List<LcdpTableIndexBean> var4);

    public String dropPhysicalTable(LcdpTableBean var1);

    public String createPhysicalView(LcdpViewBean var1);

    public String alterPhysicalView(LcdpViewBean var1);

    public String dropPhysicalView(LcdpViewBean var1);

    public void testSelectStatement(String var1);

    public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean var1);

    public List<LcdpTableViewDTO> selectPhysicalTableOrViewNameList(MapperParameter var1);
}
