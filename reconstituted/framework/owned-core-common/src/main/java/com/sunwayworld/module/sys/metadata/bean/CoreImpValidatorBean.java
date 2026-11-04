/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_METADATA_IMPVALIDATOR")
public class CoreImpValidatorBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = 8534291254515703098L;
    @Id
    private Long id;
    private Long targetId;
    private String serviceClass;
    private String spelContextName;
    private String validateSpel;
    private String validatePassedSpel;
    private String message;
    private String enable;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return this.targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getServiceClass() {
        return this.serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getSpelContextName() {
        return this.spelContextName;
    }

    public void setSpelContextName(String spelContextName) {
        this.spelContextName = spelContextName;
    }

    public String getValidateSpel() {
        return this.validateSpel;
    }

    public void setValidateSpel(String validateSpel) {
        this.validateSpel = validateSpel;
    }

    public String getValidatePassedSpel() {
        return this.validatePassedSpel;
    }

    public void setValidatePassedSpel(String validatePassedSpel) {
        this.validatePassedSpel = validatePassedSpel;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEnable() {
        return this.enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
