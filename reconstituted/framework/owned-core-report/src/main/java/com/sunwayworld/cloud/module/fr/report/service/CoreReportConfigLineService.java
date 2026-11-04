/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.service;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineTreeDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.util.List;

public interface CoreReportConfigLineService
extends GenericService<CoreReportConfigLineBean, Long>,
GenericActivatableService<CoreReportConfigLineBean, Long>,
GenericSelectableService<CoreReportConfigLineBean, Long>,
GenericChoosableService<CoreReportConfigLineBean, Long> {
    public Long insert(String var1, RestJsonWrapperBean var2);

    public List<CoreReportConfigLineTreeDTO> selectLineTree(RestJsonWrapperBean var1);
}
