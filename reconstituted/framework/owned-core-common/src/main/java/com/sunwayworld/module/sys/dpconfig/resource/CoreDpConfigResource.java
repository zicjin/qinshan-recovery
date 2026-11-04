/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigImportDataDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigLineBean;
import com.sunwayworld.module.sys.dpconfig.service.CoreDpConfigService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/dp-configs"})
public interface CoreDpConfigResource
extends GenericResource<CoreDpConfigService, CoreDpConfigBean, Long>,
GenericTreeResource<CoreDpConfigService, CoreDpConfigBean, Long>,
GenericChoosableResource<CoreDpConfigService, CoreDpConfigBean, Long> {
    public Page<CoreDpConfigLineBean> selectConfigLinePaginationByDpConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertConfigLine(Long var1, RestJsonWrapperBean var2);

    public void deleteConfigLine(Long var1, RestJsonWrapperBean var2);

    public String export(RestJsonWrapperBean var1);

    public CoreFileImportResultDTO importData(Long var1, RestJsonWrapperBean var2);

    public List<CoreDpConfigImportDataDTO> importValidate(Long var1, RestJsonWrapperBean var2);
}
