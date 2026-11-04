/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlogtmpl.resource;

import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.bean.LcdpSubmitLogTmplBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/submit-log-tmpls"})
public interface LcdpSubmitLogTmplResource
extends GenericCloudResource<LcdpSubmitLogTmplBean, Long>,
GenericActivatableCloudResource<LcdpSubmitLogTmplBean, Long> {
}
