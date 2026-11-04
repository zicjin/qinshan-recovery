/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.bean;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.Serializable;
import java.util.Map;

public class CoreBpmnParameterDTO
implements Serializable {
    private static final long serialVersionUID = -4757483195429791323L;
    private String[] nextCandidators;
    private String[] nextCandidateRoles;
    private String comment;
    private String currentStatusCode;
    private String targetStatusCode;
    private String attachment;
    private String transferCandidator;
    private Map<String, String> params;
    private String bpmnTaskuserStrategy;

    public CoreBpmnParameterDTO() {
        this.setNextCandidators(new String[0]);
        this.setNextCandidateRoles(new String[0]);
    }

    public static CoreBpmnParameterDTO of(RestJsonWrapperBean wrapper) {
        CoreBpmnParameterDTO instance = new CoreBpmnParameterDTO();
        instance.setNextCandidators(StringUtils.split(wrapper.getParamValue("bpmn_nextCandidator"), ","));
        instance.setNextCandidateRoles(StringUtils.split(wrapper.getParamValue("bpmn_nextCandidateRole"), ","));
        instance.setComment(wrapper.getParamValue("bpmn_comment"));
        instance.setCurrentStatusCode(wrapper.getParamValue("bpmn_currentStatusCode"));
        instance.setTargetStatusCode(wrapper.getParamValue("bpmn_targetStatusCode"));
        instance.setAttachment(wrapper.getParamValue("bpmn_attachment"));
        instance.setTransferCandidator(wrapper.getParamValue("bpmn_transferCandidator"));
        instance.setParams(wrapper.getParamMap());
        instance.setBpmnTaskuserStrategy(wrapper.getParamValue("bpmn_taskuser_strategy"));
        return instance;
    }

    public String[] getNextCandidators() {
        return this.nextCandidators;
    }

    public void setNextCandidators(String[] nextCandidators) {
        this.nextCandidators = nextCandidators;
    }

    public String[] getNextCandidateRoles() {
        return this.nextCandidateRoles;
    }

    public void setNextCandidateRoles(String[] nextCandidateRoles) {
        this.nextCandidateRoles = nextCandidateRoles;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getAttachment() {
        return this.attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getTransferCandidator() {
        return this.transferCandidator;
    }

    public void setTransferCandidator(String transferCandidator) {
        this.transferCandidator = transferCandidator;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getBpmnTaskuserStrategy() {
        return this.bpmnTaskuserStrategy;
    }

    public void setBpmnTaskuserStrategy(String bpmnTaskuserStrategy) {
        this.bpmnTaskuserStrategy = bpmnTaskuserStrategy;
    }
}
