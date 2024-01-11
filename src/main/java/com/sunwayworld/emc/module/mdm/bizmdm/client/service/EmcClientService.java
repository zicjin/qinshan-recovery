package com.sunwayworld.emc.module.mdm.bizmdm.client.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

public interface EmcClientService extends GenericService<EmcClientBean, Long>, GenericChoosableService<EmcClientBean, Long> {

    void insertContact(Long clientId, RestJsonWrapperBean wrapper);

    void insertMonitorPoint(Long clientId, RestJsonWrapperBean wrapper);

    void insertMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper);

    void deleteContact(RestJsonWrapperBean wrapper);

    void deleteMonitorPoint(RestJsonWrapperBean wrapper);

    void deleteMonitorPointScheme(RestJsonWrapperBean wrapper);

    Page<EmcClientContactBean> selectContactPaginationByClientId(Long clientId, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointBean> selectMonitorPointPaginationByClientId(Long clientId, RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointSchemeBean> selectSchemePaginationByMonitorPointId(Long monitorPointId, RestJsonWrapperBean wrapper);

    void insertMonitorPointIndex(RestJsonWrapperBean wrapper);

    void copyMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper);

    void copyMonitorPointIndex(RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointBean> selectMonitorPointChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcMonitorPointSchemeBean> selectMonitorPointSchemeChoosablePagination(RestJsonWrapperBean wrapper);

    List<EmcMonitorPointBean> selectFilterPointCategorySelectableList();

    Page<EmcMonitorPointBean> selectMonitorPointByTestOrgId(RestJsonWrapperBean wrapper);

    Page<EmcClientContactBean> selectContactChoosablePagination(RestJsonWrapperBean wrapper);

    void copyMonitorPoint(RestJsonWrapperBean wrapper);

    Page<EmcClientBean> selectSubClientChoosablePagination(RestJsonWrapperBean wrapper);

}
