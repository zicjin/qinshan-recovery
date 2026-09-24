/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_EMC_EQUIPT_ACQ_RESULT")
public class EmcEquiptAcqResultBean
extends AbstractPersistable<Long> {
    @Transient
    private static final long serialVersionUID = -4741295854029133301L;
    @Id
    private Long id;
    private Long equiptAcqFileId;
    private String fileName;
    private String bizNo;
    private String testName;
    private Long rowNo;
    private String fieldName;
    private String itemName;
    private String result;
    private Long resultId;
    private String sceneEquiptNo;
    private String sceneFileNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptAcqFileId() {
        return this.equiptAcqFileId;
    }

    public void setEquiptAcqFileId(Long equiptAcqFileId) {
        this.equiptAcqFileId = equiptAcqFileId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBizNo() {
        return this.bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getRowNo() {
        return this.rowNo;
    }

    public void setRowNo(Long rowNo) {
        this.rowNo = rowNo;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getResultId() {
        return this.resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

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

