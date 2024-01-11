package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcPropertyFieldService extends GenericService<EmcPropertyFieldBean, Long> {

    Long insert(Long id, RestJsonWrapperBean jsonWrapper);

    Page<EmcPropertyFieldBean> selectTmpls(Long id, String monitorTmplId);

    Page<EmcPropertyFieldBean> selectTmpls(Long id, Long monitorPointId, String monitorPointCategoryId);

    Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorPointId(Long id);

    Page<EmcPropertyFieldBean> selectPropertyFieldByTmplId(Long id);

    void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper);

}
