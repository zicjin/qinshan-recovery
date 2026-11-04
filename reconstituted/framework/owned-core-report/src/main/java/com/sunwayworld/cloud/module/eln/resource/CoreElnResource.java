/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.resource;

import com.sunwayworld.cloud.module.eln.bean.CoreElnAtRecordBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreElnResource
extends GenericCloudResource<CoreElnBean, Long> {
    @RequestMapping(value={"/{id}/action/update"}, method={RequestMethod.PUT})
    public void updateEln(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/tmpls/{tmplId}/queries"}, method={RequestMethod.POST})
    public CoreElnDTO selectElnDTO(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/tmpls/{tmplId}/queries"}, method={RequestMethod.POST})
    public CoreElnDTO selectElnDTO(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/{sheetNo}/{colUuidCol}/eln-at-records/queries/raw"}, method={RequestMethod.POST})
    public Pagination<CoreElnAtRecordBean> selectElnAtRecordByCol(@PathVariable Long var1, @PathVariable String var2, @PathVariable String var3, RestJsonWrapperBean var4);
}
