/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcelock.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpResourceLockService
extends GenericService<LcdpResourceLockBean, Long> {
    public RestValidationResultBean validateResourceEditable(String var1);

    public RestValidationResultBean validateResourceEditable(List<String> var1);

    public void lock(String var1, String var2);

    public void unLock(List<String> var1);

    public void lockResource(List<LcdpResourceBean> var1);
}
