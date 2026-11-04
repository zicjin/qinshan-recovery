/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigImportDataDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigLineBean;
import java.util.List;

public interface CoreDpConfigService
extends GenericService<CoreDpConfigBean, Long>,
GenericTreeService<CoreDpConfigBean, Long>,
GenericChoosableService<CoreDpConfigBean, Long> {
    public Page<CoreDpConfigLineBean> selectConfigLinePaginationByDpConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertConfigLine(Long var1, RestJsonWrapperBean var2);

    public void deleteConfigLine(Long var1, RestJsonWrapperBean var2);

    public String export(RestJsonWrapperBean var1);

    public List<CoreDpConfigImportDataDTO> importValidate(Long var1, RestJsonWrapperBean var2);

    public CoreFileImportResultDTO importData(Long var1, RestJsonWrapperBean var2);
}
