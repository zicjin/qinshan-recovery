/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreAuditTrailRecordDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -7671022420713135166L;
    private CoreAuditTrailRecordBean auditTrailRecord;
    private List<CoreAuditTrailRecordLineBean> auditTrailRecordLineList;

    public CoreAuditTrailRecordDTO(CoreAuditTrailRecordBean auditTrailRecord, List<CoreAuditTrailRecordLineBean> auditTrailRecordLineList) {
        this.auditTrailRecord = auditTrailRecord;
        this.auditTrailRecordLineList = ObjectUtils.defaultIfNull(auditTrailRecordLineList, new ArrayList());
    }

    public CoreAuditTrailRecordBean getAuditTrailRecord() {
        return this.auditTrailRecord;
    }

    public List<CoreAuditTrailRecordLineBean> getAuditTrailRecordLineList() {
        return this.auditTrailRecordLineList;
    }
}
