/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_PDF_ACQ_PREPROCESS")
public class CorePdfAcqPreprocessBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -257424860594930447L;
    @Id
    private Long id;
    private Long pdfAcqConfigId;
    private String keyword;
    private String operation;
    private String replaceStr;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPdfAcqConfigId() {
        return this.pdfAcqConfigId;
    }

    public void setPdfAcqConfigId(Long pdfAcqConfigId) {
        this.pdfAcqConfigId = pdfAcqConfigId;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getReplaceStr() {
        return this.replaceStr;
    }

    public void setReplaceStr(String replaceStr) {
        this.replaceStr = replaceStr;
    }
}
