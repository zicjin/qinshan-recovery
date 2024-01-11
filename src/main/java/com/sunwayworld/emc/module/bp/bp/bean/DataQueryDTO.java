package com.sunwayworld.emc.module.bp.bp.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 数据查询-行转列
 * 
 * @author magt@sunwayworld.com
 * @date 2020-02-26
 */
public class DataQueryDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 1760370910984079416L;

    private Long id; // ID
    private String colId;
    private String colTitle; // 列标题
    private String colVal; // 列值
    private String rowNo; // 行号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public String getColTitle() {
        return colTitle;
    }

    public void setColTitle(String colTitle) {
        this.colTitle = colTitle;
    }

    public String getColVal() {
        return colVal;
    }

    public void setColVal(String colVal) {
        this.colVal = colVal;
    }

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }
}
