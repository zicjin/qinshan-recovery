/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.resource;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreElnTmplResource
extends GenericCloudResource<CoreElnTmplBean, Long>,
GenericActivatableCloudResource<CoreElnTmplBean, Long>,
GenericChoosableCloudResource<CoreElnTmplBean, Long>,
GenericSelectableCloudResource<CoreElnTmplBean, Long> {
    @RequestMapping(value={"/{id}/dses"}, method={RequestMethod.POST})
    public Long insertDs(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/dses"}, method={RequestMethod.DELETE})
    public void deleteDs(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/dses/queries/raw"}, method={RequestMethod.POST})
    public Pagination<CoreElnTmplDsBean> selectDsByTmpl(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/versions/{versionId}"}, method={RequestMethod.POST})
    public CoreElnTmplVersionDTO selectElnDsDTO(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/versions/queries/raw"}, method={RequestMethod.POST})
    public Pagination<CoreElnTmplVersionBean> selectVersionByTmpl(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/versions"}, method={RequestMethod.POST})
    public Long insertVersion(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/versions"}, method={RequestMethod.PUT})
    public void updateVersion(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/versions/{versionId}"}, method={RequestMethod.GET})
    public CoreElnTmplVersionBean selectVsersionDetail(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/versions/{versionId}/eln-tests"}, method={RequestMethod.POST})
    public CoreElnDTO selectElnTestDTO(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/top-versions/queries"}, method={RequestMethod.GET})
    public CoreElnTmplVersionBean selectTopVersionByTmplId(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/versions/{versionId}/action/copy"}, method={RequestMethod.POST})
    public void copyTmpl(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);
}
