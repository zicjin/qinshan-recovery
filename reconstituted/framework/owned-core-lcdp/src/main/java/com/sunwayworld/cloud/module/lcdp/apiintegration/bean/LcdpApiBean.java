/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_API")
public class LcdpApiBean
extends AbstractInsertable<Long>
implements Activatable<Long> {
    @Transient
    private static final long serialVersionUID = 2900313668485136841L;
    @Id
    private Long id;
    private String apiCode;
    private String apiName;
    private String apiUrl;
    private String apiType;
    private String callType;
    private String apiDesc;
    private String restfulMethod;
    private String restfulHeader;
    private String restfulReqBody;
    private String restfulQueryParam;
    private String scriptMethodPath;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    private Long effectVersion;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String testParams;
    private String testOperation;
    private String testReqBody;
    private String soapConfig;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getApiCode() {
        return this.apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getApiName() {
        return this.apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiType() {
        return this.apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getCallType() {
        return this.callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getApiDesc() {
        return this.apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public String getRestfulMethod() {
        return this.restfulMethod;
    }

    public void setRestfulMethod(String restfulMethod) {
        this.restfulMethod = restfulMethod;
    }

    public String getRestfulHeader() {
        return this.restfulHeader;
    }

    public void setRestfulHeader(String restfulHeader) {
        this.restfulHeader = restfulHeader;
    }

    public String getRestfulReqBody() {
        return this.restfulReqBody;
    }

    public void setRestfulReqBody(String restfulReqBody) {
        this.restfulReqBody = restfulReqBody;
    }

    public String getRestfulQueryParam() {
        return this.restfulQueryParam;
    }

    public void setRestfulQueryParam(String restfulQueryParam) {
        this.restfulQueryParam = restfulQueryParam;
    }

    public String getScriptMethodPath() {
        return this.scriptMethodPath;
    }

    public void setScriptMethodPath(String scriptMethodPath) {
        this.scriptMethodPath = scriptMethodPath;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    public Long getEffectVersion() {
        return this.effectVersion;
    }

    public void setEffectVersion(Long effectVersion) {
        this.effectVersion = effectVersion;
    }

    public String getTestParams() {
        return this.testParams;
    }

    public void setTestParams(String testParams) {
        this.testParams = testParams;
    }

    public String getTestOperation() {
        return this.testOperation;
    }

    public void setTestOperation(String testOperation) {
        this.testOperation = testOperation;
    }

    public String getTestReqBody() {
        return this.testReqBody;
    }

    public void setTestReqBody(String testReqBody) {
        this.testReqBody = testReqBody;
    }

    public String getSoapConfig() {
        return this.soapConfig;
    }

    public void setSoapConfig(String soapConfig) {
        this.soapConfig = soapConfig;
    }
}
