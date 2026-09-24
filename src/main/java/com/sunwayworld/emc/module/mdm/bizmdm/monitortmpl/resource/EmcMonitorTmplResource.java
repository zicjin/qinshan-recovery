package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/monitor-tmpls")
public interface EmcMonitorTmplResource extends GenericResource<EmcMonitorTmplService, EmcMonitorTmplBean, String>, GenericActivatableResource<EmcMonitorTmplService, EmcMonitorTmplBean, String>,
        GenericChoosableResource<EmcMonitorTmplService, EmcMonitorTmplBean, String>, GenericSelectableResource<EmcMonitorTmplService, EmcMonitorTmplBean, String> {
    void copy(RestJsonWrapperBean jsonWrapper);

    Page<EmcMonitorSchemeBean> selectSchemePaginationByTmplId(String id, RestJsonWrapperBean wrapper);

    public Long insertScheme(String id, RestJsonWrapperBean wrapper);

    public void deleteScheme(String id, RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectSchemeDetectionChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectPaginationGroupByTestMethod(RestJsonWrapperBean wrapper);

    public List<EmcMonitorSchemeBean> selectSchemeSelectableList();

    void editScheme(String id, String schemeName, RestJsonWrapperBean wrapper);

    void addSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper);

    void deleteSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectSchemeItemGroupChoosablePagination(RestJsonWrapperBean wrapper);
    
    Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper);

}
