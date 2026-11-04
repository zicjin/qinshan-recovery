/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigTreeDTO;
import java.util.List;

public interface CoreAuditTrailConfigService
extends GenericService<CoreAuditTrailConfigBean, String> {
    public void insert(String var1);

    public Page<CoreAuditTrailConfigTreeDTO> selectAuditTrailableTreePagination(RestJsonWrapperBean var1);

    public Page<CoreAuditTrailConfigLineBean> selectAuditTrailConfigLineListByConfig(String var1, RestJsonWrapperBean var2);

    public void auditConfigLines(String var1, RestJsonWrapperBean var2);

    public void cancelConfigLines(String var1, RestJsonWrapperBean var2);

    public List<CoreAuditTrailConfigLineBean> selectAuditTrailableLineList(String var1);

    public void revisingAuditTrailConfigLine(String var1);

    public boolean auditTrailable(String var1);
}
