/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.List;

public class LcdpTableDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -8283270919270879941L;
    private String tableName;
    private String tableDesc;
    private String masterTableName;
    private String referColumn;
    private List<LcdpTableFieldDTO> fieldList = CollectionUtils.emptyList();
    private List<LcdpTableIndexDTO> indexList = CollectionUtils.emptyList();

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDesc() {
        return this.tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public String getMasterTableName() {
        return this.masterTableName;
    }

    public void setMasterTableName(String masterTableName) {
        this.masterTableName = masterTableName;
    }

    public String getReferColumn() {
        return this.referColumn;
    }

    public void setReferColumn(String referColumn) {
        this.referColumn = referColumn;
    }

    public List<LcdpTableFieldDTO> getFieldList() {
        return this.fieldList;
    }

    public void setFieldList(List<LcdpTableFieldDTO> fieldList) {
        this.fieldList = fieldList;
    }

    public List<LcdpTableIndexDTO> getIndexList() {
        return this.indexList;
    }

    public void setIndexList(List<LcdpTableIndexDTO> indexList) {
        this.indexList = indexList;
    }
}
