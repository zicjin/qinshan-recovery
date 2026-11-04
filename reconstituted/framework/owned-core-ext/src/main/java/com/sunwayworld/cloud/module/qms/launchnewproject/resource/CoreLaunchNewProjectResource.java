/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.resource;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/launch-new-projects"})
public interface CoreLaunchNewProjectResource
extends GenericCloudResource<CoreLaunchNewProjectBean, Long> {
    public Page<CoreLaunchNewProjectItemBean> selectNewProjectItemPaginationByLaunchNewProjectId(Long var1, RestJsonWrapperBean var2);

    public List<Long> insertNewProjectItem(Long var1, RestJsonWrapperBean var2);

    public void deleteNewProjectItem(Long var1, RestJsonWrapperBean var2);
}
