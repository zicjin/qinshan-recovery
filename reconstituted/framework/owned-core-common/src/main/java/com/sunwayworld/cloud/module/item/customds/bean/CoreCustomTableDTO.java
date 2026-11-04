/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomColumnDTO;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.io.Serializable;
import java.util.List;

public class CoreCustomTableDTO
extends AbstractInsertable<String>
implements Serializable {
    private static final long serialVersionUID = 3443641426594527441L;
    private String id;
    private String tableType;
    private String tableName;
    private List<CoreCustomColumnDTO> columnList;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTableType() {
        return this.tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<CoreCustomColumnDTO> getColumnList() {
        return this.columnList;
    }

    public void setColumnList(List<CoreCustomColumnDTO> columnList) {
        this.columnList = columnList;
    }
}
