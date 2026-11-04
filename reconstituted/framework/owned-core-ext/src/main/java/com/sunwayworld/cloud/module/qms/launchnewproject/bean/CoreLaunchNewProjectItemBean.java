/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_LAUNCH_NEW_PROJECT_ITEM")
public class CoreLaunchNewProjectItemBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long newProjectId;
    private String folderNo;
    private String type;
    private Long sortor;
    private String testStandard;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewProjectId() {
        return this.newProjectId;
    }

    public void setNewProjectId(Long newProjectId) {
        this.newProjectId = newProjectId;
    }

    public String getFolderNo() {
        return this.folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSortor() {
        return this.sortor;
    }

    public void setSortor(Long sortor) {
        this.sortor = sortor;
    }

    public String getTestStandard() {
        return this.testStandard;
    }

    public void setTestStandard(String testStandard) {
        this.testStandard = testStandard;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
