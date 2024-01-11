package com.sunwayworld.emc.module.mdm.bizmdm.client.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/clients")
public interface EmcClientResource extends GenericResource<EmcClientService, EmcClientBean, Long>, GenericChoosableResource<EmcClientService, EmcClientBean, Long> {

    Page<EmcClientContactBean> selectContactPaginationByClientId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointBean> selectMonitorPointPaginationByClientId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointSchemeBean> selectSchemePaginationByMonitorPointId(Long monitorPointId, RestJsonWrapperBean wrapper);

    void insertContact(Long id, RestJsonWrapperBean wrapper);

    void insertMonitorPoint(Long id, RestJsonWrapperBean wrapper);

    void insertMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointBean> selectMonitorPointChoosablePagination(RestJsonWrapperBean wrapper);

    void deleteContact(RestJsonWrapperBean wrapper);

    void deleteMonitorPoint(RestJsonWrapperBean wrapper);

    void deleteMonitorPointScheme(RestJsonWrapperBean wrapper);

    void insertIndex(Long monitorPointId, RestJsonWrapperBean wrapper);

    void copyScheme(Long monitorPointId, RestJsonWrapperBean wrapper);

    void copyIndex(Long monitorPointId, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointSchemeBean> selectMonitorPointSchemeChoosablePagination(RestJsonWrapperBean wrapper);

    List<EmcMonitorPointBean> selectFilterPointCategorySelectableList();

    Page<EmcMonitorPointBean> selectMonitorPointByTestOrgId(RestJsonWrapperBean wrapper);

    Page<EmcClientContactBean> selectContactChoosablePagination(RestJsonWrapperBean wrapper);

    void copyMonitorPoints(RestJsonWrapperBean wrapper);

    Page<EmcClientBean> selectSubClientChoosablePagination(RestJsonWrapperBean wrapper);

}
