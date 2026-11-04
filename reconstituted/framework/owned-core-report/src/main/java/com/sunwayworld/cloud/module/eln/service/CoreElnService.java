/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreElnService
extends GenericService<CoreElnBean, Long> {
    public CoreElnDTO selectElnDTO(Long var1, Long var2, RestJsonWrapperBean var3);

    public void updateEln(Long var1, RestJsonWrapperBean var2);

    public void insertElnFieldOrWriteBack(CoreElnDTO var1);
}
