/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreElnDsService
extends GenericService<CoreElnDsBean, Long>,
GenericActivatableService<CoreElnDsBean, Long>,
GenericChoosableService<CoreElnDsBean, Long> {
    public List<CoreElnDsFieldBean> selectDsFieldListById(Long var1);

    public RestValidationResultBean updateSqlIfValid(Long var1, RestJsonWrapperBean var2);
}
