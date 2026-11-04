/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.AttachmentStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.NextCandidatorOptStrategy;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.io.Serializable;
import java.util.List;

public class CoreBpmnTaskStatusDTO
extends AbstractBaseData
implements Serializable {
    private static final long serialVersionUID = 5248921702455710239L;
    private boolean authRequired = false;
    private boolean commentRequired = false;
    private boolean transfer = false;
    private AttachmentStrategy attachmentStrategy;
    private NextCandidatorOptStrategy nextCandidatorOptStrategy;
    private List<CoreUserBean> candidatorList;
    private List<CoreRoleBean> candidateRoleList;

    public boolean getAuthRequired() {
        return this.authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    public boolean isCommentRequired() {
        return this.commentRequired;
    }

    public void setCommentRequired(boolean commentRequired) {
        this.commentRequired = commentRequired;
    }

    public AttachmentStrategy getAttachmentStrategy() {
        return this.attachmentStrategy;
    }

    public void setAttachmentStrategy(AttachmentStrategy attachmentStrategy) {
        this.attachmentStrategy = attachmentStrategy;
    }

    public NextCandidatorOptStrategy getNextCandidatorOptStrategy() {
        return this.nextCandidatorOptStrategy;
    }

    public void setNextCandidatorOptStrategy(NextCandidatorOptStrategy nextCandidatorOptStrategy) {
        this.nextCandidatorOptStrategy = nextCandidatorOptStrategy;
    }

    public List<CoreUserBean> getCandidatorList() {
        return this.candidatorList;
    }

    public void setCandidatorList(List<CoreUserBean> candidatorList) {
        this.candidatorList = candidatorList;
    }

    public List<CoreRoleBean> getCandidateRoleList() {
        return this.candidateRoleList;
    }

    public void setCandidateRoleList(List<CoreRoleBean> candidateRoleList) {
        this.candidateRoleList = candidateRoleList;
    }

    public boolean isTransfer() {
        return this.transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }
}
