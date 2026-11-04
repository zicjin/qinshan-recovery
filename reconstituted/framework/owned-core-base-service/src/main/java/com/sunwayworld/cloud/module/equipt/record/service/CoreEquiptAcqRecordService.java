/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.record.service;

import com.sunwayworld.cloud.module.equipt.record.bean.CoreEquiptAcqRecordBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreEquiptAcqRecordService
extends GenericService<CoreEquiptAcqRecordBean, Long> {
    public void insertInNewTransaction(List<CoreEquiptAcqRecordBean> var1);

    public List<CoreEquiptAcqRecordBean> selectNoneAcqRecordList();

    public void updateIfChangedInNewTransaction(CoreEquiptAcqRecordBean var1);
}
