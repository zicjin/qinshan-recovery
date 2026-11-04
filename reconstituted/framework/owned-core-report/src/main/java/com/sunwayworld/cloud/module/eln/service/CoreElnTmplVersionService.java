/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreElnTmplVersionService
extends GenericService<CoreElnTmplVersionBean, Long> {
    public void updateVersion(RestJsonWrapperBean var1);

    public Long uploadElnTempVersion(String var1, CoreElnTmplVersionBean var2);
}
