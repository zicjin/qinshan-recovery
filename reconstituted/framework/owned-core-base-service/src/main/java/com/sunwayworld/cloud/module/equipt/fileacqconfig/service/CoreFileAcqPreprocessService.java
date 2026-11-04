/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.service;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreFileAcqPreprocessService
extends GenericService<CoreFileAcqPreprocessBean, Long> {
    public String preprocess(Long var1, String var2);
}
