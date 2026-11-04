/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.service;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CorePageFieldService
extends GenericService<CorePageFieldBean, Long>,
GenericChoosableService<CorePageFieldBean, Long> {
    public Long insertComp(Long var1, RestJsonWrapperBean var2);

    public void deleteComp(Long var1, RestJsonWrapperBean var2);

    public Page<CorePageFieldCompBean> selectCompPagination(Long var1, RestJsonWrapperBean var2);

    public Long insertCompField(Long var1, RestJsonWrapperBean var2);

    public void deleteCompField(Long var1, RestJsonWrapperBean var2);

    public Page<CorePageFieldCompFieldBean> selectCompFieldPagination(Long var1, RestJsonWrapperBean var2);

    public List<CorePageFieldDTO> selectCompFieldList(Long var1);

    public void swapCompField(Long var1, Long var2, Long var3);
}
