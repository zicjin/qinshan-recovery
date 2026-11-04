/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnImportDTO;
import java.util.List;

public interface CoreBpmnDraftService
extends GenericService<CoreBpmnDraftBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public Long insertByProcId(Long var1);

    public void submit(Long var1);

    public void importBpmn(List<CoreBpmnImportDTO> var1);

    public Page<CoreBpmnDraftOrgBean> selectDraftOrgPagination(Long var1, RestJsonWrapperBean var2);

    public void insertDraftOrg(Long var1, RestJsonWrapperBean var2);

    public void deleteDraftOrg(Long var1, RestJsonWrapperBean var2);
}
