/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.importrecord.resource;

import com.sunwayworld.cloud.module.lcdp.importrecord.bean.LcdpResourceImportRecordBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/cloud/module/lcdp/resource-import-records"})
public interface LcdpResourceImportRecordResource
extends GenericCloudResource<LcdpResourceImportRecordBean, Long> {
}
