/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateOrgBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreLesTemplateResource
extends GenericCloudResource<CoreLesTemplateBean, Long>,
GenericAuditableCloudResource<CoreLesTemplateBean, Long>,
GenericChoosableCloudResource<CoreLesTemplateBean, Long> {
    @RequestMapping(value={"{id}/new-versions"}, method={RequestMethod.POST})
    public Long newVersion(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"{copiedId}/action/copy"}, method={RequestMethod.POST})
    public Long copy(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/expire"}, method={RequestMethod.PUT})
    public void expireTemplate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/template-units"}, method={RequestMethod.POST})
    public Long insertUnit(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/template-units/{unitId}"}, method={RequestMethod.GET})
    public CoreLesTemplateUnitBean selectUnit(@PathVariable String var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/template-units/{unitId}"}, method={RequestMethod.PUT})
    public void saveUnit(@PathVariable String var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/template-units/{unitId}"}, method={RequestMethod.DELETE})
    public void deleteUnit(@PathVariable String var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/template-units/tree-grid"}, method={RequestMethod.POST})
    public Pagination<CoreLesTemplateUnitDTO> selectUnitTreePagination(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/template-units/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapUnit(@PathVariable String var1, @PathVariable Long var2, @PathVariable Long var3);

    @RequestMapping(value={"/{unitId}/action/dedent"}, method={RequestMethod.PUT})
    public void dedentSection(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{unitId}/action/indent/{parentId}"}, method={RequestMethod.PUT})
    public void indentSection(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/queries/template-units"}, method={RequestMethod.POST})
    public Pagination<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/template-orgs/queries"}, method={RequestMethod.POST})
    public Page<CoreLesTemplateOrgBean> selectTemplateOrgPagation(RestJsonWrapperBean var1);

    @RequestMapping(value={"/template-orgs"}, method={RequestMethod.POST})
    public void insertTemplateOrg(RestJsonWrapperBean var1);

    @RequestMapping(value={"/template-orgs"}, method={RequestMethod.DELETE})
    public void deleteTemplateOrg(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/submit-validate"}, method={RequestMethod.POST})
    public RestValidationResultBean submitValidate(RestJsonWrapperBean var1);
}
