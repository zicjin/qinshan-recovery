/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine;

import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CoreBpmnRuntimeSource<T extends Auditable<ID>, ID extends Serializable> {
    private String tableName;
    private T oldItem;
    private String comment;
    private Map<String, Object> vars;
    private Long procId;
    private Long attachmentId;
    private Supplier<String> descSupplier;
    private String currentStatusCode;
    private String targetStatusCode;
    private String nextStatusCode;
    private List<String> nextCandidatorList = new ArrayList<String>();
    private List<Long> nextCandidateRoleList = new ArrayList<Long>();
    private String transferCandidator;
    private boolean checkPermission = true;
    private boolean autoPass = false;
    private String bpmnTaskuserStrategy;

    private CoreBpmnRuntimeSource() {
    }

    public static <T extends Auditable<ID>, ID extends Serializable> CoreBpmnRuntimeSource<T, ID> of(String tableName, T oldItem, String comment, Map<String, Object> vars) {
        CoreBpmnRuntimeSource<T, ID> instance = new CoreBpmnRuntimeSource<T, ID>();
        instance.tableName = tableName;
        instance.oldItem = oldItem;
        instance.comment = comment;
        instance.vars = vars;
        return instance;
    }

    public void setProcId(Long procId) {
        this.procId = procId;
    }

    public Long getProcId() {
        return this.procId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public ID getId() {
        return (ID)((Serializable)this.getOldItem().getId());
    }

    public T getOldItem() {
        return this.oldItem;
    }

    public void setDescSupplier(Supplier<String> descSupplier) {
        this.descSupplier = descSupplier;
    }

    public String getDesc() {
        if (this.descSupplier == null) {
            return ObjectUtils.toString(this.getId());
        }
        return this.descSupplier.get();
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getVars() {
        return this.vars;
    }

    public String getCurrentStatusCode() {
        return this.currentStatusCode;
    }

    public void setCurrentStatusCode(String currentStatusCode) {
        this.currentStatusCode = currentStatusCode;
    }

    public String getTargetStatusCode() {
        return this.targetStatusCode;
    }

    public void setTargetStatusCode(String targetStatusCode) {
        this.targetStatusCode = targetStatusCode;
    }

    public String getNextStatusCode() {
        return this.nextStatusCode;
    }

    public void setNextStatusCode(String nextStatusCode) {
        this.nextStatusCode = nextStatusCode;
    }

    public List<String> getNextCandidatorList() {
        return this.nextCandidatorList;
    }

    public List<Long> getNextCandidateRoleList() {
        return this.nextCandidateRoleList;
    }

    public void addNextCandidator(String candidatorId) {
        this.nextCandidatorList.add(candidatorId);
    }

    public void addNextCandidatorRole(Long candidatorRole) {
        this.nextCandidateRoleList.add(candidatorRole);
    }

    public Long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getTransferCandidator() {
        return this.transferCandidator;
    }

    public void setTransferCandidator(String transferCandidator) {
        this.transferCandidator = transferCandidator;
    }

    public String getTargetId() {
        return CoreBpmnHelper.getTargetId(this.tableName, this.getId());
    }

    public boolean isCheckPermission() {
        return this.checkPermission;
    }

    public void setCheckPermission(boolean checkPermission) {
        this.checkPermission = checkPermission;
    }

    public boolean isAutoPass() {
        return this.autoPass;
    }

    public void setAutoPass(boolean autoPass) {
        this.autoPass = autoPass;
    }

    public String getBpmnTaskuserStrategy() {
        return this.bpmnTaskuserStrategy;
    }

    public void setBpmnTaskuserStrategy(String bpmnTaskuserStrategy) {
        this.bpmnTaskuserStrategy = bpmnTaskuserStrategy;
    }
}
