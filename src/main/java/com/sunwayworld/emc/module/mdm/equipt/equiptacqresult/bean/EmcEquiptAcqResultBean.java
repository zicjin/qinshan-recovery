package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

/**
 * 仪器采集结果(T_EMC_EQUIPT_ACQ_RESULT)
 *
 */
@Table("T_EMC_EQUIPT_ACQ_RESULT")
    public class EmcEquiptAcqResultBean extends AbstractPersistable<Long> {

    @Transient
    private static final long serialVersionUID = -4741295854029133301L;

    @Id
    private Long id; // 主键

    private Long equiptAcqFileId; // 仪器采集文件ID

    private String fileName; // 采集文件名

    private String bizNo; // 业务编号(样品编号/系统编号)

    private String testName;// 测试名称

    private Long rowNo; // 行号

    private String fieldName; // 字段名称

    private String itemName; // 分析项名称

    private String result; // 结果

    private Long resultId; // 结果录入结果表的ID，用来表示哪个结果用了这条数据值

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptAcqFileId() {
        return equiptAcqFileId;
    }

    public void setEquiptAcqFileId(Long equiptAcqFileId) {
        this.equiptAcqFileId = equiptAcqFileId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getRowNo() {
        return rowNo;
    }

    public void setRowNo(Long rowNo) {
        this.rowNo = rowNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }


    private String sceneEquiptNo;
    private String sceneFileNo;

    public String getSceneEquiptNo() {
        return this.sceneEquiptNo;
    }
    public void setSceneEquiptNo(String sceneEquiptNo) {
        this.sceneEquiptNo = sceneEquiptNo;
    }
    public String getSceneFileNo() {
        return this.sceneFileNo;
    }
    public void setSceneFileNo(String sceneFileNo) {
        this.sceneFileNo = sceneFileNo;
    }
}
