/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.bean;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName="corefiledocument")
public class CoreFileDocumentBean
implements Serializable {
    private static final long serialVersionUID = 5237892296886269181L;
    @Id
    private Long id;
    private String serviceName;
    private String serviceTable;
    private String serviceId;
    private String category;
    private String url;
    private String originalDocumentName;
    private String documentName;
    private String documentContent;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    @Field(type=FieldType.Date, format=DateFormat.custom, pattern="uuuu-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceTable() {
        return this.serviceTable;
    }

    public void setServiceTable(String serviceTable) {
        this.serviceTable = serviceTable;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOriginalDocumentName() {
        return this.originalDocumentName;
    }

    public void setOriginalDocumentName(String originalDocumentName) {
        this.originalDocumentName = originalDocumentName;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentContent() {
        return this.documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
