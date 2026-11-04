/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreOfficeDsService
extends GenericService<CoreOfficeDsBean, Long>,
GenericActivatableService<CoreOfficeDsBean, Long>,
GenericChoosableService<CoreOfficeDsBean, Long> {
    public RestValidationResultBean updateSqlIfValid(Long var1, RestJsonWrapperBean var2);

    public CoreOfficeDsDTO selectActivatableOfficeDsDTO();

    public String selectTree();

    public Long insertField(RestJsonWrapperBean var1);

    public void deleteField(RestJsonWrapperBean var1);

    public Pagination<CoreOfficeDsFieldBean> selectFieldPaginationByDsId(Long var1, RestJsonWrapperBean var2);

    public void swapField(String var1, Long var2, Long var3);
}
