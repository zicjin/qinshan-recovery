package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptService extends GenericService<EmcEquiptBean, Long>, GenericChoosableService<EmcEquiptBean, Long> {
    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptTestMethodBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deleteTestMethod(Long id, RestJsonWrapperBean wrapper);

    EmcEquiptDCUConfBean selectDCUConfDetail(Long id);

    Page<EmcEquiptTestItemBean> selectEquiptTestItemRawPagination(Long id, RestJsonWrapperBean wrapper);

    Long insertTestItem(Long id, RestJsonWrapperBean wrapper);

    void deleteTestItem(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectChoosablePagination(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateDcuConfig(Long id);

    Page<EmcEquiptBean> selectCaliChoose(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectVerifyChoose(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectPerCheckChoose(RestJsonWrapperBean wrapper);
}
