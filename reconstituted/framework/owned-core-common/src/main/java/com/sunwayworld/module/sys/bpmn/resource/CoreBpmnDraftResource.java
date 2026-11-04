/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-drafts"})
public interface CoreBpmnDraftResource
extends GenericResource<CoreBpmnDraftService, CoreBpmnDraftBean, Long> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/source-procs/{procId}"}, method={RequestMethod.POST})
    public Long insertByProcId(Long var1);

    @RequestMapping(value={"/{id}/action/submit"}, method={RequestMethod.PUT})
    public void submit(Long var1);

    @RequestMapping(value={"/{id}/bpmn-draft-orgs/queries"}, method={RequestMethod.POST})
    public Page<CoreBpmnDraftOrgBean> selectDraftOrgPagination(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/bpmn-draft-orgs"}, method={RequestMethod.POST})
    public void insertDraftOrg(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/bpmn-draft-orgs"}, method={RequestMethod.DELETE})
    public void deleteDraftOrg(Long var1, RestJsonWrapperBean var2);
}
