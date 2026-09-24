package com.sunwayworld.emc.module.mdm.basemdm.cert.service;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcCertService extends GenericService<EmcCertBean, Long>, GenericChoosableService<EmcCertBean, Long> {
    Long insert(String id, RestJsonWrapperBean wrapper);

    Long updateCertMethodAuth(Long id, RestJsonWrapperBean wrapper);

    Page<EmcCertBean> selectOverdueCert(RestJsonWrapperBean wrapper);

    Page<EmcEmpQualBean> selectOverdueQual(RestJsonWrapperBean wrapper);
}
