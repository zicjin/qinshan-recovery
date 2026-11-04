/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import java.util.List;
import org.springframework.lang.Nullable;

public class CoreBpmnTaskCandidatorsDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = 8526910356224985462L;
    @Nullable
    private List<String> candidatorIdList;
    @Nullable
    private List<Long> candidateRoleIdList;

    public List<String> getCandidatorIdList() {
        return this.candidatorIdList;
    }

    public void setCandidatorIdList(List<String> candidatorIdList) {
        this.candidatorIdList = candidatorIdList;
    }

    public List<Long> getCandidateRoleIdList() {
        return this.candidateRoleIdList;
    }

    public void setCandidateRoleIdList(List<Long> candidateRoleIdList) {
        this.candidateRoleIdList = candidateRoleIdList;
    }
}
