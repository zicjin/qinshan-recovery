/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.sunwayworld.framework.data.Pair;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CoreNotificationDTO
implements Serializable {
    private static final long serialVersionUID = 8685127563066337649L;
    private String templateId;
    private List<String> notifiedIdList;
    private List<Pair<String, String>> filePairList;
    private Map<String, Object> paramMap;

    public static CoreNotificationDTO of(String templateId, List<String> notifiedIdList, Map<String, Object> paramMap) {
        return CoreNotificationDTO.of(templateId, notifiedIdList, paramMap, null);
    }

    public static CoreNotificationDTO of(String templateId, List<String> notifiedIdList, Map<String, Object> paramMap, List<Pair<String, String>> filePairList) {
        CoreNotificationDTO instance = new CoreNotificationDTO();
        instance.setTemplateId(templateId);
        instance.setNotifiedIdList(notifiedIdList);
        instance.setParamMap(paramMap);
        instance.setFilePairList(filePairList);
        return instance;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getNotifiedIdList() {
        return this.notifiedIdList;
    }

    public void setNotifiedIdList(List<String> notifiedIdList) {
        this.notifiedIdList = notifiedIdList;
    }

    public Map<String, Object> getParamMap() {
        return this.paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public List<Pair<String, String>> getFilePairList() {
        return this.filePairList;
    }

    public void setFilePairList(List<Pair<String, String>> filePairList) {
        this.filePairList = filePairList;
    }
}
