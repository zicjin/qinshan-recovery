/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.resource;

import com.sunwayworld.cloud.module.equipt.acqscript.bean.CoreEquiptAcqScriptBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/acq-scripts"})
public interface CoreEquiptAcqScriptResource
extends GenericCloudResource<CoreEquiptAcqScriptBean, String>,
GenericChoosableCloudResource<CoreEquiptAcqScriptBean, String> {
}
