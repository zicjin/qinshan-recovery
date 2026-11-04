/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqaffair.service;

import com.sunwayworld.cloud.module.equipt.acqaffair.bean.CoreEquiptAcqAffairBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.io.File;

public interface CoreEquiptAcqAffairService
extends GenericService<CoreEquiptAcqAffairBean, String> {
    public void insertByFile(File var1);
}
