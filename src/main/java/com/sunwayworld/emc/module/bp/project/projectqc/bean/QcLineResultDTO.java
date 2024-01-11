package com.sunwayworld.emc.module.bp.project.projectqc.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 行转列
 * @author zhengxy@sunwayworld.com 2020年09月21日
 */
public class QcLineResultDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 5887927267418557148L;

    /** 列表单元格ID，此处即样品测试结果ID */
    private Long id;

    /** 列ID，由测试ID + 分析项ID + 重复数组成 */
    private String colId;

    /** 列标题 */
    private String colTitle;

    /** 列值 */
    private String colVal;

    /** 行号，行转列唯一标识 */
    private String rowNo;

    public QcLineResultDTO() {
    }

    public QcLineResultDTO(Long id, String colId, String colVal, String rowNo) {
        this.id = id;
        this.colId = colId;
        this.colVal = colVal;
        this.rowNo = rowNo;
    }

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
