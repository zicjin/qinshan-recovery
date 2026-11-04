/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.bean;

import com.sunwayworld.module.sys.wechat.bean.CoreWechatNoticeDataDTO;
import java.io.Serializable;
import java.util.Map;

public class CoreWechatNoticeDTO
implements Serializable {
    private static final long serialVersionUID = 5575172073217346885L;
    private Map<String, CoreWechatNoticeDataDTO> data;
    private String touser;
    private String template_id;

    public Map<String, CoreWechatNoticeDataDTO> getData() {
        return this.data;
    }

    public void setData(Map<String, CoreWechatNoticeDataDTO> data) {
        this.data = data;
    }

    public String getTouser() {
        return this.touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }
}
